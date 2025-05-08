package com.loong.android.media.ui.recorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RecorderScreen(modifier: Modifier = Modifier) {
    var isRecording by remember { mutableStateOf(false) }
    var recorder: AudioRecord? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 1. 创建一个权限请求 Launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                // 授权后，立即开始录制
                isRecording = true
                startAudioRecord { newRecorder ->
                    recorder = newRecorder
                    // 在 IO 线程循环读取帧
                    scope.launch(Dispatchers.IO) {
                        val samplesPerFrame = 1024
                        val audioFrame = ShortArray(samplesPerFrame)
                        while (isRecording) {
                            val count = recorder?.read(audioFrame, 0, samplesPerFrame) ?: 0
                            if (count > 0) {

                            }
                        }
                    }
                }
            } else {
                Toast.makeText(context, "录音权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }

    // 按钮点击逻辑
    val onRecordClick = {
        if (!isRecording) {
            // 2. 先检查权限
            when {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 已授权，直接开始
                    isRecording = true
                    startAudioRecord { newRecorder ->
                        recorder = newRecorder
                        scope.launch(Dispatchers.IO) {
                            val samplesPerFrame = 1024
                            val audioFrame = ShortArray(samplesPerFrame)
                            while (isRecording) {
                                val count = recorder?.read(audioFrame, 0, samplesPerFrame) ?: 0
                                if (count > 0) {

                                }
                            }
                        }
                    }
                }

                else -> {
                    // 未授权或被拒绝，弹出申请
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        } else {
            // 停止录制
            isRecording = false
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
        }
    }

    Scaffold { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onRecordClick) {
                Text(if (isRecording) "停止录制" else "开始录制")
            }
        }
    }
}

/**
 * Helper: 初始化并启动 AudioRecord，然后通过 callback 返回实例
 */
@RequiresPermission(Manifest.permission.RECORD_AUDIO)
private fun startAudioRecord(onReady: (AudioRecord) -> Unit) {
    val sampleRate = 44100
    val channelConfig = AudioFormat.CHANNEL_IN_MONO
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    val samplesPerFrame = 1024
    val bytesPerSample = 2
    val frameSizeBytes = samplesPerFrame * bytesPerSample
    val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    val bufferSize = maxOf(minBufSize, frameSizeBytes)

    AudioRecord(
        MediaRecorder.AudioSource.MIC,
        sampleRate,
        channelConfig,
        audioFormat,
        bufferSize
    ).apply {
        startRecording()
        onReady(this)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRecorder() {
    RecorderScreen()
}