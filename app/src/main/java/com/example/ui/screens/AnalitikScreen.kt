package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ProductViewModel
import com.example.ui.components.LiquidChromeBackground
import com.example.ui.components.LocalErrorHandler
import com.example.ui.components.DashboardSummarySkeleton
import com.example.ui.theme.LocalIsDark

@Composable
fun AnalitikScreen(
    viewModel: ProductViewModel,
    onNavigateToDashboard: () -> Unit
) {
    val products by viewModel.uiState.collectAsStateWithLifecycle()
    val bahanBakuList by viewModel.bahanBakuState.collectAsStateWithLifecycle()
    val productCosts by viewModel.productProductionCostsState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorHandler = LocalErrorHandler.current
    
    val totalProducts = products.size
    val totalBahanBaku = bahanBakuList.size
    
    val totalBahanBakuAsset = remember(bahanBakuList) {
        try {
            bahanBakuList.sumOf { it.price * it.amount }
        } catch (t: Throwable) {
            errorHandler(t)
            0.0
        }
    }
    
    val topProducts = remember(productCosts) {
        try {
            productCosts.sortedByDescending { it.totalCost }.take(3)
        } catch (t: Throwable) {
            errorHandler(t)
            emptyList()
        }
    }
    
    val topBahanBaku = remember(bahanBakuList) {
        try {
            bahanBakuList.sortedByDescending { it.price }.take(3)
        } catch (t: Throwable) {
            errorHandler(t)
            emptyList()
        }
    }

    LiquidChromeBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .safeDrawingPadding()
        ) {
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
                        .clickable { onNavigateToDashboard() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Analitik",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardSummarySkeleton()
                    DashboardSummarySkeleton()
                    DashboardSummarySkeleton()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 120.dp)
                ) {
                    // Summary Cards Row 1
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AnalitikMetricCard(
                            title = "Total Produk",
                            value = totalProducts.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        AnalitikMetricCard(
                            title = "Total Bahan Baku",
                            value = totalBahanBaku.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Asset Value
                    AnalitikMetricCard(
                        title = "Estimasi Nilai Aset Bahan Baku",
                        value = "Rp ${totalBahanBakuAsset.toLong()}",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    val chartData = productCosts.take(5).map { it.productName to it.totalCost }
                    if (chartData.isNotEmpty()) {
                        GlassmorphicBarChart(
                            data = chartData,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    Text(
                        text = "Top 3 Produk Termahal (Biaya)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    if (topProducts.isEmpty()) {
                        Text("Belum ada produk", color = Color(0xFF94A3B8))
                    } else {
                        topProducts.forEach { productCost ->
                            AnalitikItemRow(name = productCost.productName, value = "Rp ${productCost.totalCost.toLong()}")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Top 3 Bahan Baku Termahal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    if (topBahanBaku.isEmpty()) {
                        Text("Belum ada bahan baku", color = Color(0xFF94A3B8))
                    } else {
                        topBahanBaku.forEach { bb ->
                            AnalitikItemRow(name = bb.name, value = "Rp ${bb.price.toLong()}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalitikMetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x15FFFFFF), Color(0x0800F0FF))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x4DFFFFFF), Color(0x1A00F0FF))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Text(text = title, color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = Color(0xFF00F0FF), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AnalitikItemRow(name: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0x0AFFFFFF), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text(text = value, color = Color(0xFF00F0FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GlassmorphicBarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    
    val maxCost = remember(data) { (data.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0) }
    val isDark = LocalIsDark.current
    val accentColor = if (isDark) Color(0xFF00F0FF) else Color(0xFF8B5CF6)
    val textMutedColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(Color(0x15FFFFFF), Color(0x0500F0FF)) else listOf(Color(0x0FFFFFFF), Color(0x0A8B5CF6))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(Color(0x33FFFFFF), Color(0x1000F0FF)) else listOf(Color(0x66FFFFFF), Color(0x208B5CF6))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Grafik Perbandingan Biaya",
                color = if (isDark) Color.White else Color(0xFF0F172A),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { (name, value) ->
                    val barHeightFraction = (value / maxCost).toFloat()
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        val formattedValue = when {
                            value >= 1_000_000.0 -> "Rp ${(value / 100_000.0).toInt() / 10.0}jt"
                            value >= 1_000.0 -> "Rp ${(value / 1000.0).toInt()}k"
                            else -> "Rp ${value.toInt()}"
                        }

                        Text(
                            text = formattedValue,
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .fillMaxHeight(barHeightFraction * animationProgress.value)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                                    clip = false,
                                    spotColor = accentColor.copy(alpha = 0.4f)
                                )
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            accentColor,
                                            accentColor.copy(alpha = 0.2f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                                    ),
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (name.length > 8) "${name.take(7)}…" else name,
                            color = textMutedColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}
