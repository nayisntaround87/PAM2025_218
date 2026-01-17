package com.example.chillbeads.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillbeads.R
import com.example.chillbeads.ui.theme.ChillBeadsTheme
import com.example.chillbeads.viewmodel.ProductViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onNavigateToProductList: () -> Unit,
    productViewModel: ProductViewModel = viewModel()
) {
    val searchQuery by productViewModel.searchQuery
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Admin Dashboard", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.dashboard_welcome), fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            
            Spacer(modifier = Modifier.height(48.dp))

            // Kolom Pencarian Baru
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { productViewModel.onSearchQueryChanged(it) },
                label = { Text(stringResource(R.string.search_product_hint)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    onNavigateToProductList()
                })
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashboardMenuItem(
                icon = Icons.Outlined.ListAlt,
                text = stringResource(R.string.product_list),
                onClick = onNavigateToProductList
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DashboardMenuItem(
                icon = Icons.Outlined.Logout,
                text = stringResource(R.string.logout),
                onClick = onLogout
            )
        }
    }
}

@Composable
private fun DashboardMenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(24.dp))
            Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    ChillBeadsTheme {
        DashboardScreen(onLogout = {}, onNavigateToProductList = {})
    }
}
