package pl.droidsonroids.casty

import androidx.annotation.MainThread
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.framework.media.RemoteMediaClient

open class CastyPlayer {

    private lateinit var remoteMediaClient: RemoteMediaClient
    private lateinit var onMediaLoadedListener: OnMediaLoadedListener

    //Needed for NoOp instance
    internal constructor() {
        //no-op
    }

    internal constructor(onMediaLoadedListener: OnMediaLoadedListener) {
        this.onMediaLoadedListener = onMediaLoadedListener
    }

    fun setRemoteMediaClient(remoteMediaClient: RemoteMediaClient) {
        this.remoteMediaClient = remoteMediaClient
    }

    /**
     * Plays the current media file if it is paused
     */
    open fun play() {
        if (isPaused()) remoteMediaClient.play()
    }

    /**
     * Pauses the current media file if it is playing
     */
    open fun pause() {
        if (isPlaying()) remoteMediaClient.pause()
    }

    /**
     * Seeks the current media file
     *
     * @param time the number of milliseconds to seek by
     */
    open fun seek(time: Long) {
        remoteMediaClient.seek(time)
    }

    /**
     * Tries to play or pause the current media file, depending of the current state
     */
    open fun togglePlayPause() {
        if (remoteMediaClient.isPlaying) {
            remoteMediaClient.pause()
        } else if (remoteMediaClient.isPaused) {
            remoteMediaClient.play()
        }
    }

    /**
     * Checks if the media file is playing
     *
     * @return true if the media file is playing, false otherwise
     */
    open fun isPlaying(): Boolean {
        return remoteMediaClient.isPlaying
    }

    /**
     * Checks if the media file is paused
     *
     * @return true if the media file is paused, false otherwise
     */
    open fun isPaused(): Boolean {
        return remoteMediaClient.isPaused
    }

    /**
     * Checks if the media file is buffering
     *
     * @return true if the media file is buffering, false otherwise
     */
    open fun isBuffering(): Boolean {
        return remoteMediaClient.isBuffering
    }

    /**
     * Tries to load the media file and play it in the [ExpandedControlsActivity]
     *
     * @param mediaData Information about the media
     * @return true if attempt was successful, false otherwise
     * @see MediaData
     */
    @MainThread
    open fun loadMediaAndPlay(mediaData: MediaData): Boolean {
        return loadMediaAndPlay(mediaData.createMediaInfo(), mediaData.autoPlay, mediaData.position)
    }

    /**
     * Tries to load the media file and play it in the [ExpandedControlsActivity]
     *
     * @param mediaInfo Information about the media
     * @return true if attempt was successful, false otherwise
     * @see MediaInfo
     */
    @MainThread
    open fun loadMediaAndPlay(mediaInfo: MediaInfo): Boolean {
        return loadMediaAndPlay(mediaInfo, true, 0)
    }

    /**
     * Tries to load the media file and play it in the [ExpandedControlsActivity]
     *
     * @param mediaInfo Information about the media
     * @param autoPlay True if the media file should start automatically
     * @param position Start position of video in milliseconds
     * @return true if attempt was successful, false otherwise
     * @see MediaInfo
     */
    @MainThread
    open fun loadMediaAndPlay(mediaInfo: MediaInfo, autoPlay: Boolean, position: Long): Boolean {
        return playMediaBaseMethod(mediaInfo, autoPlay, position, false)
    }

    /**
     * Tries to load the media file and play in background
     *
     * @param mediaData Information about the media
     * @return true if attempt was successful, false otherwise
     * @see MediaData
     */
    @MainThread
    open fun loadMediaAndPlayInBackground(mediaData: MediaData): Boolean {
        return loadMediaAndPlayInBackground(
            mediaData.createMediaInfo(),
            mediaData.autoPlay,
            mediaData.position
        )
    }

    /**
     * Tries to load the media file and play in background
     *
     * @param mediaInfo Information about the media
     * @return true if attempt was successful, false otherwise
     * @see MediaInfo
     */
    @MainThread
    open fun loadMediaAndPlayInBackground(mediaInfo: MediaInfo): Boolean {
        return loadMediaAndPlayInBackground(mediaInfo, true, 0)
    }

    /**
     * Tries to load the media file and play in background
     *
     * @param mediaInfo Information about the media
     * @param autoPlay True if the media file should start automatically
     * @param position Start position of video in milliseconds
     * @return true if attempt was successful, false otherwise
     * @see MediaInfo
     */
    @MainThread
    open fun loadMediaAndPlayInBackground(
        mediaInfo: MediaInfo,
        autoPlay: Boolean,
        position: Long
    ): Boolean {
        return playMediaBaseMethod(mediaInfo, autoPlay, position, true)
    }

    private fun playMediaBaseMethod(
        mediaInfo: MediaInfo,
        autoPlay: Boolean,
        position: Long,
        inBackground: Boolean
    ): Boolean {
        if (!inBackground) {
            remoteMediaClient.addListener(createRemoteMediaClientListener())
        }
        remoteMediaClient.load(mediaInfo, autoPlay, position)
        return true
    }

    private fun createRemoteMediaClientListener(): RemoteMediaClient.Listener {
        return object : RemoteMediaClient.Listener {
            override fun onStatusUpdated() {
                onMediaLoadedListener.onMediaLoaded()
                remoteMediaClient.removeListener(this)
            }

            override fun onMetadataUpdated() {
                //no-op
            }

            override fun onQueueStatusUpdated() {
                //no-op
            }

            override fun onPreloadStatusUpdated() {
                //no-op
            }

            override fun onSendingRemoteMediaRequest() {
                //no-op
            }

            override fun onAdBreakStatusUpdated() {
                //no-op
            }
        }
    }

    internal interface OnMediaLoadedListener {
        fun onMediaLoaded()
    }

}