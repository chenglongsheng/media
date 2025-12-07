package com.loong.android.media.ui.media

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.regex.Pattern

/**
 * 一行歌曲
 */
data class LyricEntry(
    val time: Long, // 时间戳 (毫秒)
    val text: String // 歌词内容
)

/**
 * 歌词风格
 */
data class LrcStyle(
    val normalTextSize: Int
)

@Composable
fun LrcView(
    lrcContent: String,
    currentTime: Long,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    onSeekTo: (Long) -> Unit = {}
) {
    // 解析歌词 (使用 remember 缓存，避免重组时重复解析)
    val lyrics = remember(lrcContent) { parseLrc(lrcContent) }

    // List 状态
    val listState = rememberLazyListState()

    // 计算当前应该高亮的行索引
    // derivedStateOf 确保只有当 index 真正变化时才触发后续逻辑，优化性能
    val currentLineIndex by remember(lyrics, currentTime) {
        derivedStateOf {
            if (lyrics.isEmpty()) return@derivedStateOf -1

            // 找到最后一个时间 <= 当前时间的歌词
            val index = lyrics.indexOfLast { it.time <= currentTime }
            if (index == -1) 0 else index
        }
    }

    // 检测用户是否正在拖动列表
    val isDragged by listState.interactionSource.collectIsDraggedAsState()

    // 是否暂停自动滚动 (用于处理用户手动滑动时的逻辑)
    var isAutoScrollPaused by remember { mutableStateOf(false) }

    // 处理用户交互的锁定与延迟恢复
    LaunchedEffect(isDragged) {
        if (isDragged) {
            // 用户手指按下，立即暂停自动滚动
            isAutoScrollPaused = true
        } else {
            // 用户手指抬起，启动倒计时
            // 如果之前是暂停状态，等待3秒后恢复自动滚动
            if (isAutoScrollPaused) {
                delay(3000) // 3秒无操作后归位
                isAutoScrollPaused = false
            }
        }
    }

    // 记录列表高度，用于计算居中偏移
    var containerHeightPx by remember { mutableIntStateOf(0) }

    // 自动滚动逻辑
    // 当 currentLineIndex 变化 OR 暂停状态解除时触发
    LaunchedEffect(currentLineIndex, isAutoScrollPaused) {
        // 只有当没有暂停自动滚动时，才执行滚动
        if (!isAutoScrollPaused && currentLineIndex >= 0 && currentLineIndex < lyrics.size) {
            // 计算偏移量让当前行居中
            // 确保列表高度已测量
            if (containerHeightPx > 0) {
                listState.animateScrollToItem(
                    index = currentLineIndex,
//                    scrollOffset = -(containerHeightPx / 2) + 100
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { containerHeightPx = it.height }
    ) {
        if (lyrics.isEmpty()) {
            Text(
                text = "暂无歌词",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                // 添加内边距，确保第一行和最后一行也能滚动到中间
                contentPadding = PaddingValues(vertical = with(LocalDensity.current) { (containerHeightPx / 2).toDp() })
            ) {
                itemsIndexed(lyrics) { index, entry ->
                    val isCurrentLine = index == currentLineIndex
                    LyricLineItem(
                        text = entry.text,
                        isCurrentLine = isCurrentLine,
                        textAlign,
                        onClick = {
                            // 点击行时，通常期望立即跳转并恢复跟随，所以这里手动重置暂停状态
                            // 但也可以选择不重置，取决于具体需求。这里选择不强制重置 View 状态，仅 Seek
                            onSeekTo(entry.time)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LyricLineItem(text: String, isCurrentLine: Boolean, textAlign: TextAlign, onClick: () -> Unit) {
    // 动画效果：当前行放大、高亮，非当前行变小、变暗
    val scale by animateFloatAsState(
        targetValue = if (isCurrentLine) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 300),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isCurrentLine) 1f else 0.5f,
        animationSpec = tween(durationMillis = 300),
        label = "alpha"
    )
    val color = if (isCurrentLine) Color.White else Color.LightGray

    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
                this.alpha = alpha
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // 移除点击涟漪，因为是歌词点击
            ) { onClick() },
        style = MaterialTheme.typography.bodyLarge.copy(
            textAlign = textAlign,
            fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    )
}

private fun parseLrc(lrcContent: String): List<LyricEntry> {
    val entries = mutableListOf<LyricEntry>()
    // 匹配 [mm:ss.xx] 或 [mm:ss:xxx] 格式
    val regex = Pattern.compile("\\[(\\d{2}):(\\d{2})[.:](\\d{2,3})](.*)")

    lrcContent.lineSequence().forEach { line ->
        val matcher = regex.matcher(line)
        if (matcher.matches()) {
            val min = matcher.group(1)?.toLongOrNull() ?: 0
            val sec = matcher.group(2)?.toLongOrNull() ?: 0
            val msStr = matcher.group(3) ?: "0"
            // 处理毫秒，有的 lrc 是 2位 (x10) 有的是 3位
            val ms = if (msStr.length == 2) msStr.toLong() * 10 else msStr.toLong()

            val content = matcher.group(4)?.trim() ?: ""

            val time = min * 60 * 1000 + sec * 1000 + ms
            entries.add(LyricEntry(time, content))
        }
    }
    return entries.sortedBy { it.time }
}

// --- 5. 演示界面 (模拟播放器) ---
@Composable
fun LrcPlayerScreen() {
    // 示例歌词 (周杰伦 - 七里香 片段)
    val sampleLrc = """
        [00:02.00]七里香 - 周杰伦
        [00:08.50]词：方文山 曲：周杰伦
        [00:28.00]窗外的麻雀 在电线杆上多嘴
        [00:34.50]你说这一句 很有夏天的感觉
        [00:41.00]手中的铅笔 在纸上来来回回
        [00:47.50]我用几行字形容你是我的谁
        [00:54.00]秋刀鱼的滋味 猫跟你都想了解
        [01:00.50]初恋的香味就这样被我们寻回
        [01:07.00]那温暖的阳光 像刚摘的鲜艳草莓
        [01:13.50]你说你舍不得吃掉这一种感觉
        [01:20.00]雨下整夜 我的爱溢出就像雨水
        [01:26.50]院子落叶 跟我的思念厚厚一叠
        [01:33.00]几句是非 也无法将我的热情冷却
        [01:40.00]你出现在我诗的每一页
    """.trimIndent()

    // 播放器状态
    var isPlaying by remember { mutableStateOf(false) }
    var currentTime by remember { mutableLongStateOf(0L) }
    val totalTime = 110 * 1000L // 1分50秒

    // 模拟播放时钟
    LaunchedEffect(isPlaying) {
        val startTime = System.currentTimeMillis() - currentTime
        while (isPlaying) {
            val newTime = System.currentTimeMillis() - startTime
            if (newTime >= totalTime) {
                currentTime = totalTime
                isPlaying = false
                break
            }
            currentTime = newTime
            delay(100) // 100ms 更新一次 UI 即可
        }
    }

    Surface(
        color = Color(0xFF1E1E1E), // 深色背景模拟音乐 App
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部标题
            Text(
                text = "正在播放",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            // 歌词视图 (占据大部分空间)
            LrcView(
                lrcContent = sampleLrc,
                currentTime = currentTime,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onSeekTo = { time ->
                    // 点击歌词跳转
                    currentTime = time
                }
            )

            // 底部控制栏
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D2D2D))
                    .padding(16.dp)
            ) {
                // 进度显示
                Text(
                    text = "${formatTime(currentTime)} / ${formatTime(totalTime)}",
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.End),
                    fontSize = 12.sp
                )

                // 进度条
                Slider(
                    value = currentTime.toFloat(),
                    onValueChange = {
                        currentTime = it.toLong()
                        isPlaying = false // 拖动时暂停
                    },
                    onValueChangeFinished = {
                        isPlaying = true // 拖动结束继续播放
                    },
                    valueRange = 0f..totalTime.toFloat()
                )

                // 播放/暂停按钮
                Button(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (isPlaying) "暂停" else "播放")
                }
            }
        }
    }
}

// 格式化时间 mm:ss
fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Preview
@Composable
fun PreviewLrcPlayer() {
    LrcPlayerScreen()
}