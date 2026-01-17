package com.example.chillbeads.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.chillbeads.R
import com.example.chillbeads.viewmodel.ProductOperationState
import com.example.chillbeads.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String,
    onProductUpdated: () -> Unit,
    onNavigateBack: () -> Unit,
    productViewModel: ProductViewModel = viewModel()
) {
    val product = remember(productId) { productViewModel.getProductById(productId) }

    if (product == null) {
        Text(stringResource(R.string.product_not_found))
        LaunchedEffect(Unit) { onProductUpdated() }
        return
    }

    var name by remember { mutableStateOf(product.name) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var stock by remember { mutableStateOf(product.stock.toString()) }
    var description by remember { mutableStateOf(product.description) }
    var photoUrl by remember { mutableStateOf(product.photoUrl ?: "") }

    val operationState by productViewModel.operationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isLoading = operationState is ProductOperationState.Loading

    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is ProductOperationState.Success -> {
                scope.launch { snackbarHostState.showSnackbar(state.message) }
                productViewModel.resetOperationState()
                onProductUpdated()
            }
            is ProductOperationState.Error -> {
                scope.launch { snackbarHostState.showSnackbar(state.message) }
                productViewModel.resetOperationState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_product)) },
                navigationIcon = { 
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUrl.isNotBlank()) {
                        AsyncImage(model = photoUrl, contentDescription = "Preview Gambar Produk", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = "Preview Gambar", modifier = Modifier.size(48.dp))
                            Text("Preview Gambar Akan Muncul di Sini")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.product_name)) }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text(stringResource(R.string.price)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text(stringResource(R.string.stock)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.description)) }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = photoUrl, onValueChange = { photoUrl = it }, label = { Text("URL Gambar Produk") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        productViewModel.updateProduct(
                            id = product.id!!,
                            name = name,
                            price = price.toDoubleOrNull() ?: 0.0,
                            stock = stock.toIntOrNull() ?: 0,
                            description = description,
                            photoUrl = photoUrl
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(stringResource(R.string.update))
                    }
                }
            }
        }
    }
}
