package com.example.chillbeads.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chillbeads.model.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ProductOperationState {
    object Idle : ProductOperationState()
    object Loading : ProductOperationState()
    data class Success(val message: String) : ProductOperationState()
    data class Error(val message: String) : ProductOperationState()
}

class ProductViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val productsCollection = db.collection("products")

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    private val products = _products.asStateFlow()

    private val _operationState = MutableStateFlow<ProductOperationState>(ProductOperationState.Idle)
    val operationState = _operationState.asStateFlow()

    val searchQuery = mutableStateOf("")

    val filteredProducts: State<List<Product>> = derivedStateOf {
        val productList = products.value
        if (searchQuery.value.isBlank()) {
            productList
        } else {
            productList.filter { it.name.contains(searchQuery.value, ignoreCase = true) }
        }
    }

    init {
        productsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("ProductViewModel", "Listen failed.", error)
                return@addSnapshotListener
            }
            _products.value = snapshot?.toObjects() ?: emptyList()
        }
    }

    fun addProduct(name: String, price: Double, stock: Int, description: String, photoUrl: String?) {
        _operationState.value = ProductOperationState.Loading
        val newProduct = Product(name = name, price = price, stock = stock, description = description, photoUrl = if (photoUrl.isNullOrBlank()) null else photoUrl)
        productsCollection.add(newProduct)
            .addOnSuccessListener { documentReference ->
                val productWithId = newProduct.copy(id = documentReference.id)
                _products.value = _products.value + productWithId
                _operationState.value = ProductOperationState.Success("Produk berhasil ditambahkan!") 
            }
            .addOnFailureListener { _operationState.value = ProductOperationState.Error("Gagal menambahkan produk") }
    }

    fun updateProduct(id: String, name: String, price: Double, stock: Int, description: String, photoUrl: String?) {
        _operationState.value = ProductOperationState.Loading
        val updatedData = mapOf("name" to name, "price" to price, "stock" to stock, "description" to description, "photoUrl" to (if (photoUrl.isNullOrBlank()) null else photoUrl))
        productsCollection.document(id).update(updatedData)
            .addOnSuccessListener { 
                val updatedList = _products.value.map { product ->
                    if (product.id == id) product.copy(name = name, price = price, stock = stock, description = description, photoUrl = photoUrl) else product
                }
                _products.value = updatedList
                _operationState.value = ProductOperationState.Success("Produk berhasil diperbarui!") 
            }
            .addOnFailureListener { _operationState.value = ProductOperationState.Error("Gagal memperbarui produk") }
    }

    fun deleteProduct(id: String) {
        _operationState.value = ProductOperationState.Loading
        productsCollection.document(id).delete()
            .addOnSuccessListener { 
                _products.value = _products.value.filter { it.id != id }
                _operationState.value = ProductOperationState.Success("") 
            }
            .addOnFailureListener { _operationState.value = ProductOperationState.Error("Gagal menghapus produk") }
    }

    fun resetOperationState() { _operationState.value = ProductOperationState.Idle }
    fun onSearchQueryChanged(query: String) { searchQuery.value = query }
    fun getProductById(id: String): Product? = products.value.find { it.id == id }
}
