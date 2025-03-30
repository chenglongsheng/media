package com.loong.android.media.player

import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Rating
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture
import com.loong.android.media.common.TLog

@OptIn(UnstableApi::class)
class MediaLibrarySessionCallback : MediaLibrarySession.Callback {

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val interfaceVersion = controller.interfaceVersion
        val controllerVersion = controller.controllerVersion
        TLog.i("onConnect: $session,$controller,$interfaceVersion,$controllerVersion")
        return super.onConnect(session, controller)
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
        val interfaceVersion = controller.interfaceVersion
        val controllerVersion = controller.controllerVersion
        TLog.i("onPostConnect: $session,$controller,$interfaceVersion,$controllerVersion")
    }

    override fun onDisconnected(session: MediaSession, controller: MediaSession.ControllerInfo) {
        val interfaceVersion = controller.interfaceVersion
        val controllerVersion = controller.controllerVersion
        TLog.i("onDisconnected: $session,$controller,$interfaceVersion,$controllerVersion")
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onPlayerCommandRequest(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        playerCommand: Int
    ): Int {
        TLog.i("onPlayerCommandRequest: $session,$controller,$playerCommand")
        return super.onPlayerCommandRequest(session, controller, playerCommand)
    }

    override fun onSetRating(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaId: String,
        rating: Rating
    ): ListenableFuture<SessionResult> {
        TLog.i("onSetRating: $session,$controller,$mediaId,$rating")
        return super.onSetRating(session, controller, mediaId, rating)
    }

    override fun onSetRating(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        rating: Rating
    ): ListenableFuture<SessionResult> {
        TLog.i("onSetRating: $session,$controller,$rating")
        return super.onSetRating(session, controller, rating)
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        TLog.i("onCustomCommand: $session,$controller,$customCommand,$args")
        return super.onCustomCommand(session, controller, customCommand, args)
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        TLog.i("onAddMediaItems: $mediaItems,$controller,$mediaItems")
        return super.onAddMediaItems(mediaSession, controller, mediaItems)
    }

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        TLog.i("onSetMediaItems: $mediaItems,$controller,$mediaItems,$startIndex,$startPositionMs")
        return super.onSetMediaItems(
            mediaSession,
            controller,
            mediaItems,
            startIndex,
            startPositionMs
        )
    }

    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        TLog.i("onPlaybackResumption: $mediaSession,$controller")
        return super.onPlaybackResumption(mediaSession, controller)
    }

    override fun onMediaButtonEvent(
        session: MediaSession,
        controllerInfo: MediaSession.ControllerInfo,
        intent: Intent
    ): Boolean {
        TLog.i("onMediaButtonEvent: $session,$controllerInfo,$intent")
        return super.onMediaButtonEvent(session, controllerInfo, intent)
    }

    override fun onPlayerInteractionFinished(
        session: MediaSession,
        controllerInfo: MediaSession.ControllerInfo,
        playerCommands: Player.Commands
    ) {
        TLog.i("onPlayerInteractionFinished: $session,$controllerInfo,$playerCommands")
        super.onPlayerInteractionFinished(session, controllerInfo, playerCommands)
    }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        TLog.i("onGetLibraryRoot: $session,$browser,$params")
        return super.onGetLibraryRoot(session, browser, params)
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
        TLog.i("onGetItem: $session,$browser,$mediaId")
        return super.onGetItem(session, browser, mediaId)
    }

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        TLog.i("onGetChildren: $session,$browser,$parentId,$page,$pageSize,$params")
        return super.onGetChildren(session, browser, parentId, page, pageSize, params)
    }

    override fun onSubscribe(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<Void>> {
        TLog.i("onSubscribe: $session,$browser,$parentId,$params")
        return super.onSubscribe(session, browser, parentId, params)
    }

    override fun onUnsubscribe(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String
    ): ListenableFuture<LibraryResult<Void>> {
        TLog.i("onUnsubscribe: $session,$browser,$parentId")
        return super.onUnsubscribe(session, browser, parentId)
    }

    override fun onSearch(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<Void>> {
        TLog.i("onSearch: $session,$browser,$query,$params")
        return super.onSearch(session, browser, query, params)
    }

    override fun onGetSearchResult(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        TLog.i("onGetSearchResult: $session,$browser,$query,$page,$pageSize,$params")
        return super.onGetSearchResult(session, browser, query, page, pageSize, params)
    }
}