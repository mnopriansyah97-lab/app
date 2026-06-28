package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.Product
import com.example.ui.ProductViewModel
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.LiquidChromeBackground
import com.example.ui.components.GlassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailProductScreen(
    product: Product,
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onEditBahanBaku: () -> Unit
) {
    var showEditMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            title = "Hapus Produk",
            message = "Apakah Anda yakin ingin menghapus produk '${product.name}'? Seluruh data bahan baku terkait produk ini akan dilepas.",
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteProduct(product)
                onBack()
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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

                Text(
                    text = "DETAIL PRODUK",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Box {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0x15FFFFFF), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(16.dp))
                            .clickable { showEditMenu = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF00F0FF)
                        )
                    }

                    DropdownMenu(
                        expanded = showEditMenu,
                        onDismissRequest = { showEditMenu = false },
                        modifier = Modifier
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0x3300F0FF))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Produk", color = Color.White) },
                            onClick = {
                                showEditMenu = false
                                onEditProduct(product)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Bahan Baku", color = Color.White) },
                            onClick = {
                                showEditMenu = false
                                onEditBahanBaku()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus Produk", color = Color(0xFFFF4C4C)) },
                            onClick = {
                                showEditMenu = false
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.TopCenter) {
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 600.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    // Images
                    if (product.imageUris.isNotEmpty()) {
                        val images = product.imageUris.split(",")
                        val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { images.size })
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f/9f)
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(20.dp))
                        ) {
                            androidx.compose.foundation.pager.HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                AsyncImage(
                                    model = images[page],
                                    contentDescription = product.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            if (images.size > 1) {
                                Row(
                                    Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    repeat(images.size) { iteration ->
                                        val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                                        Box(
                                            modifier = Modifier
                                                .padding(2.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .size(6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detail Box
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = product.name,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = product.category,
                                        color = Color(0xFF00F0FF),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "|",
                                        color = Color(0xFF64748B),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = product.type,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                                
                                if (product.type == "Paket") {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = product.packageName,
                                        color = Color(0xCCFFFFFF),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                            
                            if (product.type == "Paket") {
                                DetailItem("Deskripsi Paket", product.packageDesc)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bahan Baku:",
                                color = Color(0xFF00F0FF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            val productsWithBahanBaku by viewModel.productsWithBahanBakuState.collectAsStateWithLifecycle()
                            val currentProductWithBahanBaku = remember(productsWithBahanBaku, product) {
                                productsWithBahanBaku.find { it.product.id == product.id }
                            }
                            val relatedBahanBaku = currentProductWithBahanBaku?.bahanBakuList ?: emptyList()
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

                            if (relatedBahanBaku.isEmpty()) {
                                Text("Tidak ada bahan baku terkait.", color = Color(0xFF94A3B8), fontSize = 14.sp)
                            } else {
                                relatedBahanBaku.forEach { bb ->
                                    val usedAmount = parsedAmounts[bb.id] ?: 1.0
                                    val portionCost = bb.price * usedAmount
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0x1AFFFFFF), RoundedCornerShape(8.dp))
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(bb.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                            Text(bb.category, color = Color(0xFF94A3B8), fontSize = 12.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Rp ${portionCost.toLong()}", color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("${if(usedAmount % 1.0 == 0.0) usedAmount.toInt().toString() else usedAmount.toString()} ${bb.unit}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                        }
                                    }
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

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
