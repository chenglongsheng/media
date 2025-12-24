package com.loong.android.media.ui.monitor

import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile
import java.util.Locale
import kotlin.math.roundToInt

object CpuInfoUtils {

    private const val TAG = "CpuInfoUtils"

    // ==================== 基础信息 ====================

    /** 逻辑核心数 */
    fun coreCount(): Int = Runtime.getRuntime().availableProcessors()

    /** 是否 64 位 CPU */
    fun is64Bit(): Boolean = Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()

    /** ABI 列表 */
    fun supportedAbis(): Array<String> = Build.SUPPORTED_ABIS

    /** 主要 ABI */
    fun primaryAbi(): String = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"

    /** 原始 cpuinfo 文本 */
    fun cpuInfoText(): String = runCatching {
        File("/proc/cpuinfo").readText()
    }.getOrElse { "" }

    /** CPU 硬件名称 (如: Qualcomm Snapdragon 888) */
    fun hardwareName(): String = runCatching {
        cpuInfoText().lines()
            .firstOrNull { it.startsWith("Hardware") }
            ?.substringAfter(":")
            ?.trim()
    }.getOrElse { null } ?: Build.HARDWARE

    /** CPU 实现者 (ARM/Qualcomm/etc) */
    fun cpuImplementer(): String = runCatching {
        cpuInfoText().lines()
            .firstOrNull { it.contains("CPU implementer") }
            ?.substringAfter(":")
            ?.trim()
            ?.let { parseImplementer(it) }
    }.getOrElse { null } ?: "Unknown"

    private fun parseImplementer(hex: String): String = when (hex) {
        "0x41" -> "ARM"
        "0x42" -> "Broadcom"
        "0x43" -> "Cavium"
        "0x44" -> "DEC"
        "0x4e" -> "Nvidia"
        "0x50" -> "APM"
        "0x51" -> "Qualcomm"
        "0x53" -> "Samsung"
        "0x56" -> "Marvell"
        "0x69" -> "Intel"
        else -> "Unknown($hex)"
    }

    /** CPU 架构代号 (如: Cortex-A78) */
    fun cpuArchitecture(): List<String> = runCatching {
        val parts = cpuInfoText().lines()
            .filter { it.contains("CPU part") }
            .map { it.substringAfter(":").trim() }
            .distinct()
        parts.map { parseCpuPart(it) }
    }.getOrElse { emptyList() }

    private fun parseCpuPart(hex: String): String = when (hex) {
        "0xd03" -> "Cortex-A53"
        "0xd04" -> "Cortex-A35"
        "0xd05" -> "Cortex-A55"
        "0xd07" -> "Cortex-A57"
        "0xd08" -> "Cortex-A72"
        "0xd09" -> "Cortex-A73"
        "0xd0a" -> "Cortex-A75"
        "0xd0b" -> "Cortex-A76"
        "0xd0c" -> "Neoverse-N1"
        "0xd0d" -> "Cortex-A77"
        "0xd0e" -> "Cortex-A76AE"
        "0xd40" -> "Neoverse-V1"
        "0xd41" -> "Cortex-A78"
        "0xd44" -> "Cortex-X1"
        "0xd46" -> "Cortex-A510"
        "0xd47" -> "Cortex-A710"
        "0xd48" -> "Cortex-X2"
        "0xd4d" -> "Cortex-A715"
        "0xd4e" -> "Cortex-X3"
        else -> "Unknown($hex)"
    }

    // ==================== 频率信息 ====================

    /** 每个核心的最大频率 (kHz) */
    fun maxFreqs(): List<Long> = (0 until coreCount()).map { maxFreqOfCore(it) }

    /** 每个核心的最小频率 (kHz) */
    fun minFreqs(): List<Long> = (0 until coreCount()).map { minFreqOfCore(it) }

    /** 每个核心的当前频率 (kHz) */
    fun curFreqs(): List<Long> = (0 until coreCount()).map { curFreqOfCore(it) }

    /** 每个核心的可用频率列表 */
    fun availableFreqs(core: Int): List<Long> = runCatching {
        File("/sys/devices/system/cpu/cpu$core/cpufreq/scaling_available_frequencies")
            .readText()
            .trim()
            .split("\\s+".toRegex())
            .mapNotNull { it.toLongOrNull() }
            .sortedDescending()
    }.getOrElse { emptyList() }

    /** 调度器策略 */
    fun scalingGovernor(core: Int): String = 
        readString("/sys/devices/system/cpu/cpu$core/cpufreq/scaling_governor")

    fun maxFreqOfCore(core: Int): Long =
        readLong("/sys/devices/system/cpu/cpu$core/cpufreq/cpuinfo_max_freq")

    fun minFreqOfCore(core: Int): Long =
        readLong("/sys/devices/system/cpu/cpu$core/cpufreq/cpuinfo_min_freq")

    fun curFreqOfCore(core: Int): Long =
        readLong("/sys/devices/system/cpu/cpu$core/cpufreq/scaling_cur_freq")

    /** 核心是否在线 */
    fun isCoreOnline(core: Int): Boolean =
        if (core == 0) true // cpu0 总是在线
        else readLong("/sys/devices/system/cpu/cpu$core/online") == 1L

    /** 在线核心列表 */
    fun onlineCores(): List<Int> = (0 until coreCount()).filter { isCoreOnline(it) }

    // ==================== CPU 使用率 ====================

    data class CpuStat(
        val user: Long,
        val nice: Long,
        val system: Long,
        val idle: Long,
        val iowait: Long,
        val irq: Long,
        val softirq: Long,
        val steal: Long = 0
    ) {
        val total: Long get() = user + nice + system + idle + iowait + irq + softirq + steal
        val active: Long get() = total - idle
    }

    private fun readCpuStat(): CpuStat {
        RandomAccessFile("/proc/stat", "r").use { raf ->
            val line = raf.readLine()
            // cpu  user nice system idle iowait irq softirq steal
            val toks = line.split("\\s+".toRegex()).drop(1)
            val nums = toks.mapNotNull { it.toLongOrNull() }
            return CpuStat(
                user = nums.getOrNull(0) ?: 0,
                nice = nums.getOrNull(1) ?: 0,
                system = nums.getOrNull(2) ?: 0,
                idle = nums.getOrNull(3) ?: 0,
                iowait = nums.getOrNull(4) ?: 0,
                irq = nums.getOrNull(5) ?: 0,
                softirq = nums.getOrNull(6) ?: 0,
                steal = nums.getOrNull(7) ?: 0
            )
        }
    }

    /**
     * 采样式 CPU 使用率（%）
     * @param intervalMs 采样间隔，建议 300~1000ms
     */
    fun cpuUsage(intervalMs: Long = 500): Float {
        val p = readCpuStat()
        Thread.sleep(intervalMs)
        val c = readCpuStat()
        val idleDelta = c.idle - p.idle
        val totalDelta = c.total - p.total
        return if (totalDelta > 0)
            (1f - idleDelta.toFloat() / totalDelta) * 100f
        else 0f
    }

    /**
     * 协程版 CPU 使用率
     */
    suspend fun cpuUsageAsync(intervalMs: Long = 500): Float = withContext(Dispatchers.IO) {
        val p = readCpuStat()
        kotlinx.coroutines.delay(intervalMs)
        val c = readCpuStat()
        val idleDelta = c.idle - p.idle
        val totalDelta = c.total - p.total
        if (totalDelta > 0)
            (1f - idleDelta.toFloat() / totalDelta) * 100f
        else 0f
    }

    /**
     * 详细 CPU 使用率统计
     */
    data class CpuUsageDetail(
        val total: Float,      // 总使用率
        val user: Float,       // 用户空间
        val system: Float,     // 内核空间
        val iowait: Float      // IO等待
    )

    fun cpuUsageDetail(intervalMs: Long = 500): CpuUsageDetail {
        val p = readCpuStat()
        Thread.sleep(intervalMs)
        val c = readCpuStat()
        
        val totalDelta = (c.total - p.total).toFloat()
        return if (totalDelta > 0) {
            CpuUsageDetail(
                total = ((c.active - p.active) / totalDelta * 100f),
                user = ((c.user - p.user) / totalDelta * 100f),
                system = ((c.system - p.system) / totalDelta * 100f),
                iowait = ((c.iowait - p.iowait) / totalDelta * 100f)
            )
        } else {
            CpuUsageDetail(0f, 0f, 0f, 0f)
        }
    }

    // ==================== 温度监控 ====================

    /** 获取 CPU 温度 (℃) */
    fun cpuTemperature(): Float? = runCatching {
        // 尝试常见的温度节点
        val paths = listOf(
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/class/thermal/thermal_zone1/temp",
            "/sys/devices/virtual/thermal/thermal_zone0/temp",
            "/sys/devices/virtual/thermal/thermal_zone1/temp"
        )
        
        for (path in paths) {
            val temp = readLong(path)
            if (temp > 0) {
                // 有些设备返回毫度 (milli-degrees)
                return@runCatching if (temp > 1000) temp / 1000f else temp.toFloat()
            }
        }
        null
    }.getOrNull()

    /** 获取所有热区温度 */
    fun allThermalZones(): Map<String, Float> = runCatching {
        val zones = mutableMapOf<String, Float>()
        var i = 0
        while (i < 20) { // 最多检查20个热区
            val tempPath = "/sys/class/thermal/thermal_zone$i/temp"
            val typePath = "/sys/class/thermal/thermal_zone$i/type"
            
            val temp = readLong(tempPath)
            if (temp > 0) {
                val type = readString(typePath).ifEmpty { "zone$i" }
                val celsius = if (temp > 1000) temp / 1000f else temp.toFloat()
                zones[type] = celsius
            } else {
                break
            }
            i++
        }
        zones
    }.getOrElse { emptyMap() }

    // ==================== 算力评分 ====================

    /**
     * 基础算力评分（Σ maxFreq）
     * 适合设备分档
     */
    fun baseCpuScore(): Long = maxFreqs().filter { it > 0 }.sum()

    /**
     * 加权算力（区分 big / mid / little）
     * 比 baseScore 更贴近真实性能
     */
    fun weightedCpuScore(): Long {
        var score = 0L
        maxFreqs().forEach { freq ->
            if (freq <= 0) return@forEach
            score += when {
                freq >= 2800000 -> freq * 3   // 超大核
                freq >= 2200000 -> freq * 2   // 大核
                freq >= 1800000 -> (freq * 1.5).toLong()  // 中核
                else -> freq                  // 小核
            }
        }
        return score
    }

    /**
     * 实时算力（Σ 当前频率）
     * 可判断热降频 / 负载情况
     */
    fun realtimeCpuPower(): Long = curFreqs().filter { it > 0 }.sum()

    /**
     * 降频比例 (0~100)
     * 越高说明降频越严重
     */
    fun throttlingPercentage(): Float {
        val maxPower = maxFreqs().filter { it > 0 }.sum()
        val curPower = realtimeCpuPower()
        return if (maxPower > 0)
            ((maxPower - curPower).toFloat() / maxPower * 100f).coerceIn(0f, 100f)
        else 0f
    }

    /**
     * 核心分组 (big.LITTLE 架构)
     */
    data class CoreCluster(
        val type: ClusterType,
        val cores: List<Int>,
        val maxFreq: Long,
        val minFreq: Long
    )

    enum class ClusterType {
        PRIME,   // 超大核 (>= 3.0 GHz)
        BIG,     // 大核 (>= 2.2 GHz)
        MID,     // 中核 (>= 1.8 GHz)
        LITTLE   // 小核 (< 1.8 GHz)
    }

    fun coreClusters(): List<CoreCluster> {
        val freqs = maxFreqs()
        val grouped = freqs.withIndex()
            .groupBy { it.value }
            .map { (freq, cores) ->
                val type = when {
                    freq >= 3000000 -> ClusterType.PRIME
                    freq >= 2200000 -> ClusterType.BIG
                    freq >= 1800000 -> ClusterType.MID
                    else -> ClusterType.LITTLE
                }
                CoreCluster(
                    type = type,
                    cores = cores.map { it.index },
                    maxFreq = freq,
                    minFreq = cores.map { minFreqs()[it.index] }.minOrNull() ?: 0
                )
            }
            .sortedByDescending { it.maxFreq }
        
        return grouped
    }

    // ==================== 设备性能等级 ====================

    enum class PerformanceTier {
        FLAGSHIP,    // 旗舰
        HIGH_END,    // 高端
        MID_RANGE,   // 中端
        LOW_END,     // 入门
        UNKNOWN
    }

    fun performanceTier(): PerformanceTier {
        val score = weightedCpuScore()
        return when {
            score >= 50000000 -> PerformanceTier.FLAGSHIP
            score >= 30000000 -> PerformanceTier.HIGH_END
            score >= 15000000 -> PerformanceTier.MID_RANGE
            score > 0 -> PerformanceTier.LOW_END
            else -> PerformanceTier.UNKNOWN
        }
    }

    fun performanceTierName(): String = when (performanceTier()) {
        PerformanceTier.FLAGSHIP -> "旗舰级"
        PerformanceTier.HIGH_END -> "高端"
        PerformanceTier.MID_RANGE -> "中端"
        PerformanceTier.LOW_END -> "入门级"
        PerformanceTier.UNKNOWN -> "未知"
    }

    // ==================== 数据快照 ====================

    data class CpuSnapshot(
        val cores: Int,
        val onlineCores: List<Int>,
        val is64Bit: Boolean,
        val abis: List<String>,
        val hardware: String,
        val implementer: String,
        val architecture: List<String>,
        val maxFreqs: List<Long>,
        val minFreqs: List<Long>,
        val curFreqs: List<Long>,
        val governors: List<String>,
        val clusters: List<CoreCluster>,
        val baseScore: Long,
        val weightedScore: Long,
        val realtimePower: Long,
        val throttlingPercentage: Float,
        val performanceTier: PerformanceTier,
        val temperature: Float?
    )

    fun snapshot(): CpuSnapshot = CpuSnapshot(
        cores = coreCount(),
        onlineCores = onlineCores(),
        is64Bit = is64Bit(),
        abis = supportedAbis().toList(),
        hardware = hardwareName(),
        implementer = cpuImplementer(),
        architecture = cpuArchitecture(),
        maxFreqs = maxFreqs(),
        minFreqs = minFreqs(),
        curFreqs = curFreqs(),
        governors = (0 until coreCount()).map { scalingGovernor(it) },
        clusters = coreClusters(),
        baseScore = baseCpuScore(),
        weightedScore = weightedCpuScore(),
        realtimePower = realtimeCpuPower(),
        throttlingPercentage = throttlingPercentage(),
        performanceTier = performanceTier(),
        temperature = cpuTemperature()
    )

    fun snapshotString(): String {
        val s = snapshot()
        val sb = StringBuilder()
        
        sb.appendLine("==================== CPU 信息 ====================")
        sb.appendLine("硬件: ${s.hardware}")
        sb.appendLine("实现者: ${s.implementer}")
        sb.appendLine("架构: ${s.architecture.joinToString(", ")}")
        sb.appendLine("核心数: ${s.cores} (在线: ${s.onlineCores.size})")
        sb.appendLine("64位: ${s.is64Bit}")
        sb.appendLine("ABI: ${s.abis.joinToString(", ")}")
        
        sb.appendLine("\n==================== 核心分组 ====================")
        s.clusters.forEach { cluster ->
            sb.appendLine("${cluster.type.name}: 核心${cluster.cores} " +
                    "(${cluster.maxFreq / 1000} MHz)")
        }
        
        sb.appendLine("\n==================== 频率状态 ====================")
        s.maxFreqs.forEachIndexed { i, maxFreq ->
            val curFreq = s.curFreqs.getOrNull(i) ?: 0
            val online = if (s.onlineCores.contains(i)) "●" else "○"
            val governor = s.governors.getOrNull(i) ?: "unknown"
            sb.appendLine("CPU$i $online ${curFreq / 1000} / ${maxFreq / 1000} MHz [$governor]")
        }
        
        sb.appendLine("\n==================== 性能评分 ====================")
        sb.appendLine("基础分数: ${s.baseScore}")
        sb.appendLine("加权分数: ${s.weightedScore}")
        sb.appendLine("实时算力: ${s.realtimePower}")
        sb.appendLine("性能等级: ${performanceTierName()}")
        sb.appendLine("降频程度: ${s.throttlingPercentage.roundToInt()}%")
        
        s.temperature?.let {
            sb.appendLine("\n==================== 温度 ====================")
            sb.appendLine("CPU温度: ${String.format(Locale.US, "%.1f", it)}°C")
        }
        
        return sb.toString()
    }

    // ==================== 工具方法 ====================

    private fun readLong(path: String): Long =
        runCatching { 
            File(path).readText().trim().toLongOrNull() ?: -1
        }.getOrElse { -1 }

    private fun readString(path: String): String =
        runCatching { 
            File(path).readText().trim()
        }.getOrElse { "" }

    /**
     * 日志输出完整信息
     */
    fun logInfo() {
        Log.d(TAG, snapshotString())
    }
}