package com.loong.android.media.ui.monitor

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CpuMonitorScreen() {
    var snapshot by remember { mutableStateOf(CpuInfoUtils.snapshot()) }
    var cpuUsage by remember { mutableFloatStateOf(0f) }
    var usageDetail by remember { mutableStateOf<CpuInfoUtils.CpuUsageDetail?>(null) }
    var thermalZones by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
    var availableFreqsList by remember { mutableStateOf<Map<Int, List<Long>>>(emptyMap()) }
    var isMonitoring by remember { mutableStateOf(true) }

    // 自动刷新
    LaunchedEffect(isMonitoring) {
        withContext(Dispatchers.IO) {
            while (isActive && isMonitoring) {
                snapshot = CpuInfoUtils.snapshot()
                cpuUsage = CpuInfoUtils.cpuUsageAsync(500)
                usageDetail = CpuInfoUtils.cpuUsageDetail(500)
                thermalZones = CpuInfoUtils.allThermalZones()

                // 获取每个核心的可用频率（只获取一次）
                if (availableFreqsList.isEmpty()) {
                    availableFreqsList = (0 until snapshot.cores).associateWith {
                        CpuInfoUtils.availableFreqs(it)
                    }
                }

                delay(1000)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CPU 监控") },
                actions = {
                    IconButton(onClick = { isMonitoring = !isMonitoring }) {
                        Icon(
                            imageVector = if (isMonitoring) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isMonitoring) "暂停" else "开始"
                        )
                    }
                    IconButton(onClick = {
                        snapshot = CpuInfoUtils.snapshot()
                        thermalZones = CpuInfoUtils.allThermalZones()
                    }) {
                        Icon(Icons.Default.Refresh, "刷新")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CPU 使用率仪表盘
            CpuUsageGauge(cpuUsage)

            // 详细使用率
            usageDetail?.let { detail ->
                UsageDetailCard(detail)
            }

            // 基本信息（增强版）
            EnhancedBasicInfoCard(snapshot)

            // 温度和降频
            ThermalCard(
                temperature = snapshot.temperature,
                throttling = snapshot.throttlingPercentage,
                thermalZones = thermalZones
            )

            // 核心状态
            CoreStatusCard(snapshot)

            // 核心分组
            ClusterCard(snapshot.clusters)

            // 性能评分
            PerformanceCard(snapshot)

            // 频率详情
            FrequencyDetailCard(snapshot, availableFreqsList)

            // CPU Info 文本
            CpuInfoTextCard()
        }
    }
}

@Composable
fun CpuUsageGauge(usage: Float) {
    val animatedUsage by animateFloatAsState(
        targetValue = usage,
        animationSpec = tween(500)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CPU 使用率",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 圆形进度条
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                CircularProgressIndicator(
                    progress = { animatedUsage / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = when {
                        animatedUsage < 30 -> Color(0xFF4CAF50)
                        animatedUsage < 60 -> Color(0xFFFFC107)
                        else -> Color(0xFFF44336)
                    },
                    strokeWidth = 12.dp,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${animatedUsage.roundToInt()}%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = when {
                            animatedUsage < 30 -> "空闲"
                            animatedUsage < 60 -> "正常"
                            animatedUsage < 80 -> "繁忙"
                            else -> "高负载"
                        },
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun UsageDetailCard(detail: CpuInfoUtils.CpuUsageDetail) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "详细统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            UsageBar("用户空间", detail.user, Color(0xFF2196F3))
            Spacer(modifier = Modifier.height(8.dp))
            UsageBar("系统空间", detail.system, Color(0xFF9C27B0))
            Spacer(modifier = Modifier.height(8.dp))
            UsageBar("I/O 等待", detail.iowait, Color(0xFFFF9800))
        }
    }
}

@Composable
fun UsageBar(label: String, percentage: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 14.sp)
            Text(
                text = "${percentage.roundToInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((percentage / 100f).coerceIn(0f, 1f))
                    .background(color)
            )
        }
    }
}

@Composable
fun EnhancedBasicInfoCard(snapshot: CpuInfoUtils.CpuSnapshot) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            InfoTitle("基本信息")

            InfoRow(Icons.Default.Memory, "硬件", snapshot.hardware)
            InfoRow(Icons.Default.Build, "实现者", snapshot.implementer)
            InfoRow(
                Icons.Default.Architecture,
                "架构",
                snapshot.architecture.take(2).joinToString(", ")
            )
            InfoRow(Icons.Default.Category, "核心数", "${snapshot.cores} 核")
            InfoRow(
                Icons.Default.CheckCircle,
                "在线",
                "${snapshot.onlineCores.size}/${snapshot.cores}"
            )
            InfoRow(
                Icons.Default.DataArray,
                "ABI",
                CpuInfoUtils.primaryAbi()
            )
            InfoRow(
                Icons.Default.Numbers,
                "64位",
                if (snapshot.is64Bit) "是" else "否"
            )
            InfoRow(
                Icons.Default.Star,
                "性能",
                CpuInfoUtils.performanceTierName()
            )
        }
    }
}

@Composable
fun ThermalCard(
    temperature: Float?,
    throttling: Float,
    thermalZones: Map<String, Float>
) {
    val hasHighTemp = temperature != null && temperature > 70
    val hasThrottling = throttling > 30

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                hasHighTemp -> Color(0xFFFFEBEE)
                hasThrottling -> Color(0xFFFFF3E0)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            InfoTitle("温度与降频")

            // 主要温度和降频
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 温度
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = when {
                                temperature == null -> Color.Gray
                                temperature > 70 -> Color(0xFFF44336)
                                temperature > 60 -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "CPU温度",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = temperature?.let { "%.1f°C".format(it) } ?: "N/A",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 降频
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "降频比例",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${throttling.roundToInt()}%",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    throttling > 30 -> Color(0xFFF44336)
                                    throttling > 10 -> Color(0xFFFF9800)
                                    else -> Color(0xFF4CAF50)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Speed,
                            contentDescription = null,
                            tint = when {
                                throttling > 30 -> Color(0xFFF44336)
                                throttling > 10 -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (thermalZones.isNotEmpty()) {
                Text(
                    text = "所有热区",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    thermalZones.entries.forEach { (zone, temp) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = zone,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "%.1f°C".format(temp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    temp > 70 -> Color(0xFFF44336)
                                    temp > 60 -> Color(0xFFFF9800)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "无热区数据",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
fun CoreStatusCard(snapshot: CpuInfoUtils.CpuSnapshot) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            InfoTitle("核心状态")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                snapshot.maxFreqs.forEachIndexed { index, maxFreq ->
                    val curFreq = snapshot.curFreqs.getOrNull(index) ?: 0
                    val isOnline = snapshot.onlineCores.contains(index)
                    val governor = snapshot.governors.getOrNull(index) ?: "unknown"

                    CoreStatusItem(
                        coreIndex = index,
                        isOnline = isOnline,
                        currentFreq = curFreq,
                        maxFreq = maxFreq,
                        governor = governor
                    )
                }
            }
        }
    }
}

@Composable
fun CoreStatusItem(
    coreIndex: Int,
    isOnline: Boolean,
    currentFreq: Long,
    maxFreq: Long,
    governor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 核心编号和状态
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isOnline)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$coreIndex",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isOnline)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 频率信息
        Column(modifier = Modifier.weight(1f)) {
            val percentage = if (maxFreq > 0) (currentFreq.toFloat() / maxFreq * 100).roundToInt() else 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isOnline) "${currentFreq / 1000} MHz" else "离线",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$percentage%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 频率条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (isOnline && maxFreq > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(currentFreq.toFloat() / maxFreq)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4CAF50),
                                        Color(0xFF8BC34A),
                                        Color(0xFFFFC107),
                                        Color(0xFFFF9800),
                                        Color(0xFFF44336)
                                    )
                                )
                            )
                    )
                }
            }

            Text(
                text = governor,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ClusterCard(clusters: List<CpuInfoUtils.CoreCluster>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            InfoTitle("核心分组")

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                clusters.forEach { cluster ->
                    ClusterItem(cluster)
                }
            }
        }
    }
}

@Composable
fun ClusterItem(cluster: CpuInfoUtils.CoreCluster) {
    val (icon, color) = when (cluster.type) {
        CpuInfoUtils.ClusterType.PRIME -> Icons.Default.Rocket to Color(0xFFE91E63)
        CpuInfoUtils.ClusterType.BIG -> Icons.Default.Speed to Color(0xFF2196F3)
        CpuInfoUtils.ClusterType.MID -> Icons.AutoMirrored.Filled.TrendingUp to Color(0xFF4CAF50)
        CpuInfoUtils.ClusterType.LITTLE -> Icons.Default.BatteryChargingFull to Color(0xFF9E9E9E)
    }

    val typeName = when (cluster.type) {
        CpuInfoUtils.ClusterType.PRIME -> "超大核"
        CpuInfoUtils.ClusterType.BIG -> "大核"
        CpuInfoUtils.ClusterType.MID -> "中核"
        CpuInfoUtils.ClusterType.LITTLE -> "小核"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = typeName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "核心 ${cluster.cores.joinToString(", ")}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${cluster.maxFreq / 1000} MHz",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = color
            )
            Text(
                text = "${cluster.cores.size} 核",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun PerformanceCard(snapshot: CpuInfoUtils.CpuSnapshot) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            InfoTitle("性能评分")

            ScoreItem("基础分数", snapshot.baseScore)
            Spacer(modifier = Modifier.height(8.dp))
            ScoreItem("加权分数", snapshot.weightedScore)
            Spacer(modifier = Modifier.height(8.dp))
            ScoreItem("实时算力", snapshot.realtimePower)
        }
    }
}

@Composable
fun ScoreItem(label: String, score: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp)
        Text(
            text = String.format("%,d", score),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun FrequencyDetailCard(snapshot: CpuInfoUtils.CpuSnapshot, availableFreqs: Map<Int, List<Long>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            InfoTitle("频率详情")

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                snapshot.maxFreqs.forEachIndexed { index, maxFreq ->
                    val minFreq = snapshot.minFreqs.getOrNull(index) ?: 0
                    val available = availableFreqs[index]?.size ?: 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CPU $index",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.width(60.dp)
                        )
                        Text(
                            text = "${minFreq / 1000} - ${maxFreq / 1000} MHz",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        if (available > 0) {
                            Text(
                                text = "$available 档",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CpuInfoTextCard() {
    var expanded by remember { mutableStateOf(false) }
    val cpuInfoText = remember { CpuInfoUtils.cpuInfoText() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (expanded) 400.dp else 120.dp) // 固定高度切换
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CPU Info",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "收起" else "展开"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = cpuInfoText.ifEmpty { "无数据" },
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                Text(
                    text = cpuInfoText.lines().take(3).joinToString("\n"),
                    fontSize = 10.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun InfoTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}