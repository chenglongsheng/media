package com.loong.android.media.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.loong.android.media.local.model.AudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {
    // 使用 Flow 实时监控数据变化，UI 自动刷新
    @Query("SELECT * FROM audio ORDER BY addedTimestamp ASC")
    fun getAllAudio(): Flow<List<AudioEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBatch(audios: List<AudioEntity>)

    // 用于更新 ID3 信息
    @Query("UPDATE audio SET title = :title, artist = :artist, durationMs = :duration, isMetadataExtracted = 1 WHERE path = :path")
    suspend fun updateMetadata(path: String, title: String, artist: String?, duration: Long)
    
    // 拔出 USB 时清空数据
    @Query("DELETE FROM audio WHERE path LIKE :rootPath || '%'")
    suspend fun deleteByRootPath(rootPath: String)
    
    // 获取未解析元数据的文件列表
    @Query("SELECT * FROM audio WHERE isMetadataExtracted = 0")
    suspend fun getUnprocessedDocs(): List<AudioEntity>
}