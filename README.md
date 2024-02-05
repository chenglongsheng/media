# media

Visualizer音频可视化，需要使用录音权限
`android.permission.RECORD_AUDIO`
通过audio session ID 抓取音频数据

当值是0的时候，为混合输出。或者可以由MediaPlayer和AudioTrack提供audio session id，或者其他播放器提供这个id

audio session id 是0的时候，需要`Manifest.permission.MODIFY_AUDIO_SETTINGS`权限

可以对正在播放的音频执行测量，`setMeasurementMode(int)`设置模式

捕获两种数据：

- 波形数据：连续的8位无符号数据
- 频率数据：8位幅度的快速傅里叶变换数据

fft 数据格式

| Index | 0    | 1       | 2    | 3    | 4    | 5    | ...  | n-2       | n-1       |
| ----- | ---- | ------- | ---- | ---- | ---- | ---- | ---- | --------- | --------- |
| Date  | Rf0  | Rf(n/2) | Rf1  | If1  | Rf2  | If2  | ...  | Rf(n/2-1) | If(n/2-1) |

Rf是实部，If是虚部，n是getCaptureSize()

```kotlin
       int n = fft.size();
       float[] magnitudes = new float[n / 2 + 1];
       float[] phases = new float[n / 2 + 1];
       magnitudes[0] = (float)Math.abs(fft[0]);      // DC
       magnitudes[n / 2] = (float)Math.abs(fft[1]);  // Nyquist
       phases[0] = phases[n / 2] = 0;
       for (int k = 1; k < n / 2; k++) {
           int i = k * 2;
           magnitudes[k] = (float)Math.hypot(fft[i], fft[i + 1]);
           phases[k] = (float)Math.atan2(fft[i + 1], fft[i]);
       }
```