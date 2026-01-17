package com.example.chillbeads.product

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.chillbeads.R
import com.example.chillbeads.model.Product
import com.example.chillbeads.ui.theme.CardText
import com.example.chillbeads.ui.theme.GradientBlue
import com.example.chillbeads.ui.theme.GradientPink
import com.example.chillbeads.viewmodel.ProductOperationState
import com.example.chillbeads.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onAddProduct: () -> Unit,
    onProductClick: (String) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    productViewModel: ProductViewModel = viewModel()
) {
    val filteredProducts by productViewModel.filteredProducts
    val operationState by productViewModel.operationState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isLoading = operationState is ProductOperationState.Loading
    
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is ProductOperationState.Success -> {
                productViewModel.resetOperationState()
            }
            is ProductOperationState.Error -> {
                scope.launch { snackbarHostState.showSnackbar(state.message) }
                productViewModel.resetOperationState()
            }
            else -> {}
        }
    }

    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text(stringResource(R.string.delete_product_confirmation_title)) },
            text = { Text(stringResource(R.string.delete_product_confirmation_message, productToDelete?.name ?: "")) },
            confirmButton = {
                Button(onClick = {
                    productToDelete?.id?.let { onDeleteProduct(it) }
                    productToDelete = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = { Button(onClick = { productToDelete = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.product_list)) },
                navigationIcon = { // Tombol Kembali
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, contentDescription = stringResource(R.string.logout))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_product))
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (filteredProducts.isEmpty() && !isLoading) {
                EmptyStateView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts, key = { it.id ?: "" }) { product ->
                        ProductListItem(
                            product = product,
                            onClick = { product.id?.let(onProductClick) },
                            onDeleteClick = { productToDelete = product }
                        )
                    }
                }
            }

            if (isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.3f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.SearchOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Produk tidak ditemukan",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun ProductListItem(product: Product, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    val gradientBrush = Brush.horizontalGradient(colors = listOf(GradientPink, GradientBlue))

    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.background(gradientBrush)) {
            Canvas(modifier = Modifier.matchParentSize()) { 
                drawCircle(Color.White.copy(alpha = 0.1f), radius = 60f, center = Offset(x = size.width - 20f, y = 20f))
                drawCircle(Color.White.copy(alpha = 0.1f), radius = 30f, center = Offset(x = size.width - 90f, y = 60f))
                drawCircle(Color.White.copy(alpha = 0.1f), radius = 40f, center = Offset(x = 40f, y = size.height - 40f))
            }

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!product.photoUrl.isNullOrBlank()) {
                        AsyncImage(model = product.photoUrl, contentDescription = product.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(imageVector = Icons.Outlined.Category, contentDescription = "Product Image", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = product.name, color = CardText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = stringResource(R.string.product_price_label, product.price.toLong()), color = CardText.copy(alpha = 0.8f), fontSize = 14.sp)
                    Text(text = stringResource(R.string.product_stock_label, product.stock), color = CardText.copy(alpha = 0.8f), fontSize = 14.sp)
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = CardText)
                }
            }
        }
    }
}
