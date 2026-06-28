package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.Product
import com.example.rememberRecentSearches
import com.example.ui.ProductViewModel
import com.example.ui.components.EmptyState
import com.example.ui.components.LiquidChromeBackground
import com.example.ui.components.GlassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KalkulasiScreen(
    products: List<Product>,
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredProducts = remember(searchQuery, products) {
        if (searchQuery.isBlank()) {
            products
        } else {
            products.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    LiquidChromeBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0x15FFFFFF), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(16.dp))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "KALKULASI BIAYA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
            
            val (recentSearches, addSearch, clearSearches) = rememberRecentSearches()
            var searchExpanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        searchExpanded = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { 
                            if (it.isFocused) searchExpanded = true 
                        },
                    placeholder = { Text("Cari produk...", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF94A3B8)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }
                    },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { 
                        addSearch(searchQuery)
                        searchExpanded = false
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00F0FF),
                        unfocusedBorderColor = Color(0x26FFFFFF),
                        focusedContainerColor = Color(0x15FFFFFF),
                        unfocusedContainerColor = Color(0x0AFFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                DropdownMenu(
                    expanded = searchExpanded && recentSearches.isNotEmpty(),
                    onDismissRequest = { searchExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color(0xFF0B1528))
                        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(8.dp)),
                    properties = PopupProperties(focusable = false)
                ) {
                    recentSearches.forEach { search ->
                        DropdownMenuItem(
                            text = { Text(search, color = Color.White) },
                            onClick = {
                                searchQuery = search
                                addSearch(search)
                                searchExpanded = false
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.History, contentDescription = "History", tint = Color(0xFF94A3B8))
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Hapus Riwayat", color = Color(0xFFFF4D4D)) },
                        onClick = {
                            clearSearches()
                            searchExpanded = false
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.TopCenter) {
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 600.dp)
                        .padding(horizontal = 24.dp)
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                ) {
                    val productsWithBahanBaku by viewModel.productsWithBahanBakuState.collectAsStateWithLifecycle()
                    
                    val selectedIds = remember(products) { products.map { it.id }.toSet() }
                    val selectedProductsWithBahanBaku = remember(productsWithBahanBaku, selectedIds) {
                        productsWithBahanBaku.filter { it.product.id in selectedIds }
                    }

                    val filteredProductsWithBahanBaku = remember(searchQuery, selectedProductsWithBahanBaku) {
                        if (searchQuery.isBlank()) {
                            selectedProductsWithBahanBaku
                        } else {
                            selectedProductsWithBahanBaku.filter { it.product.name.contains(searchQuery, ignoreCase = true) }
                        }
                    }

                    val grandTotal = remember(filteredProductsWithBahanBaku) {
                        filteredProductsWithBahanBaku.sumOf { pwb ->
                            val parsedAmounts = pwb.product.bahanBakuIds.split(",")
                                .filter { it.isNotBlank() }
                                .map { it.split(":") }
                                .associate {
                                    val id = it[0].trim().toIntOrNull() ?: 0
                                    val amount = if (it.size > 1) {
                                        it[1].trim().toDoubleOrNull() ?: 1.0
                                    } else 1.0
                                    id to amount
                                }
                            pwb.bahanBakuList.sumOf { it.price * (parsedAmounts[it.id] ?: 1.0) }
                        }
                    }

                    var profitMargin by remember { mutableStateOf(30f) }

                    if (filteredProductsWithBahanBaku.isNotEmpty()) {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Target Margin Keuntungan",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "${profitMargin.toInt()}%",
                                        color = Color(0xFF00F0FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Slider(
                                    value = profitMargin,
                                    onValueChange = { profitMargin = it },
                                    valueRange = 10f..100f,
                                    steps = 17,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF00F0FF),
                                        activeTrackColor = Color(0xFF00F0FF),
                                        inactiveTrackColor = Color(0x33FFFFFF)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "Gunakan penggeser ini untuk menentukan target margin keuntungan dari modal bahan baku.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    if (filteredProductsWithBahanBaku.isEmpty()) {
                        EmptyState(
                            title = if (searchQuery.isNotEmpty()) "Pencarian Tidak Ditemukan" else "Belum Ada Produk yang Dikalkulasi",
                            description = if (searchQuery.isNotEmpty()) "Coba sesuaikan kata kunci pencarian Anda." else "Silakan pilih produk di Dashboard dan tambahkan ke Kalkulator untuk melihat kalkulasi biaya produksi.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            illustrationRes = R.drawable.img_empty_state,
                            actionText = if (searchQuery.isNotEmpty()) null else "Kembali ke Dashboard",
                            onActionClick = if (searchQuery.isNotEmpty()) null else onBack
                        )
                    }

                    filteredProductsWithBahanBaku.forEach { productWithBahanBaku ->
                        val product = productWithBahanBaku.product
                        val relatedBahanBaku = productWithBahanBaku.bahanBakuList
                        
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Produk: ${product.name}",
                                    color = Color(0xFF00F0FF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                val parsedAmounts = remember(product.bahanBakuIds) {
                                    product.bahanBakuIds.split(",")
                                        .filter { it.isNotBlank() }
                                        .map { it.split(":") }
                                        .associate {
                                            val id = it[0].trim().toIntOrNull() ?: 0
                                            val amount = if (it.size > 1) {
                                                it[1].trim().toDoubleOrNull() ?: 1.0
                                            } else 1.0
                                            id to amount
                                        }
                                }
                                val productTotalCost = remember(parsedAmounts, relatedBahanBaku) {
                                    relatedBahanBaku.sumOf { it.price * (parsedAmounts[it.id] ?: 1.0) }
                                }
                                
                                if (relatedBahanBaku.isEmpty()) {
                                    Text("Tidak ada bahan baku yang dikalkulasi.", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                } else {
                                    relatedBahanBaku.forEach { bb ->
                                        val usedAmount = parsedAmounts[bb.id] ?: 1.0
                                        val portionCost = bb.price * usedAmount
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${bb.name} (${if(usedAmount % 1.0 == 0.0) usedAmount.toInt().toString() else usedAmount.toString()} ${bb.unit})",
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                            Text("Rp ${portionCost.toLong()}", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                        }
                                    }
                                    
                                    HorizontalDivider(color = Color(0x33FFFFFF), modifier = Modifier.padding(vertical = 12.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Subtotal Biaya", color = Color.White, fontSize = 14.sp)
                                        Text("Rp ${productTotalCost.toLong()}", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                    }

                                    val marginAmount = productTotalCost * (profitMargin / 100f)
                                    val recommendedPrice = productTotalCost + marginAmount

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Profit Margin (${profitMargin.toInt()}%)", color = Color.White, fontSize = 14.sp)
                                        Text("Rp ${marginAmount.toLong()}", color = Color(0xFF10B981), fontSize = 14.sp)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Rekomendasi Harga Jual", color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Rp ${recommendedPrice.toLong()}", color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }

                    if (products.isNotEmpty()) {
                        val grandMarginAmount = grandTotal * (profitMargin / 100f)
                        val grandRecommendedTotal = grandTotal + grandMarginAmount
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total Modal Bahan Baku", color = Color.White, fontSize = 14.sp)
                                    Text("Rp ${grandTotal.toLong()}", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Target Keuntungan (${profitMargin.toInt()}%)", color = Color.White, fontSize = 14.sp)
                                    Text("Rp ${grandMarginAmount.toLong()}", color = Color(0xFF10B981), fontSize = 14.sp)
                                }
                                HorizontalDivider(color = Color(0x33FFFFFF), modifier = Modifier.padding(vertical = 8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("TOTAL REKOMENDASI JUAL", color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Rp ${grandRecommendedTotal.toLong()}", color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
