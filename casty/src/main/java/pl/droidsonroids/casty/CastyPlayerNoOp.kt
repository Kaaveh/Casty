package pl.droidsonroids.casty

import com.google.android.gms.cast.MediaInfo

internal class CastyPlayerNoOp : CastyPlayer() {

    override fun play() {
        //no-op
    }

    override fun pause() {
        //no-op
    }

    override fun seek(time: Long) {
        //no-op
    }

    override fun togglePlayPause() {
        //no-op
    }

    override fun isPlaying(): Boolean {
        return false
    }

    override fun isPaused(): Boolean {
        return false
    }

    override fun isBuffering(): Boolean {
        return false
    }

    override fun loadMediaAndPlay(mediaData: MediaData): Boolean {
        return false
    }

    override fun loadMediaAndPlay(mediaInfo: MediaInfo): Boolean {
        return false
    }

    override fun loadMediaAndPlay(
        mediaInfo: MediaInfo,
        autoPlay: Boolean,
        position: Long
    ): Boolean {
        return false
    }

    override fun loadMediaAndPlayInBackground(mediaData: MediaData): Boolean {
        return false
    }

    override fun loadMediaAndPlayInBackground(mediaInfo: MediaInfo): Boolean {
        return false
    }

    override fun loadMediaAndPlayInBackground(
        mediaInfo: MediaInfo,
        autoPlay: Boolean,
        position: Long
    ): Boolean {
        return false
    }
}