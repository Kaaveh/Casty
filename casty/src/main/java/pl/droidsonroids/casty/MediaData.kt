package pl.droidsonroids.casty

import android.net.Uri
import android.text.TextUtils
import androidx.annotation.IntDef
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaMetadata.KEY_SUBTITLE
import com.google.android.gms.cast.MediaMetadata.KEY_TITLE
import com.google.android.gms.common.images.WebImage

/**
 * Media information class
 */
class MediaData private constructor(private val url: String) {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(STREAM_TYPE_NONE, STREAM_TYPE_BUFFERED, STREAM_TYPE_LIVE)
    annotation class StreamType

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        MEDIA_TYPE_GENERIC,
        MEDIA_TYPE_MOVIE,
        MEDIA_TYPE_TV_SHOW,
        MEDIA_TYPE_MUSIC_TRACK,
        MEDIA_TYPE_PHOTO,
        MEDIA_TYPE_USER
    )
    annotation class MediaType

    private var streamType = STREAM_TYPE_NONE
    private lateinit var contentType: String
    private var streamDuration = UNKNOWN_DURATION

    private var mediaType = MEDIA_TYPE_GENERIC
    private lateinit var title: String
    private lateinit var subtitle: String

    var autoPlay = true
    var position: Long = 0

    private var imageUrls = mutableListOf<String>()

    private fun setStreamType(streamType: Int) {
        this.streamType = streamType
    }

    private fun setContentType(contentType: String) {
        this.contentType = contentType
    }

    private fun setStreamDuration(streamDuration: Long) {
        this.streamDuration = streamDuration
    }

    private fun setTitle(title: String) {
        this.title = title
    }

    private fun setSubtitle(subtitle: String) {
        this.subtitle = subtitle
    }

    private fun setMediaType(mediaType: Int) {
        this.mediaType = mediaType
    }

    fun createMediaInfo(): MediaInfo {
        val mediaMetadata = MediaMetadata(mediaType)
        if (!TextUtils.isEmpty(title)) mediaMetadata.putString(KEY_TITLE, title)
        if (!TextUtils.isEmpty(subtitle)) mediaMetadata.putString(KEY_SUBTITLE, subtitle)
        for (imageUrl in imageUrls) {
            mediaMetadata.addImage(WebImage(Uri.parse(imageUrl)))
        }
        return MediaInfo.Builder(url)
            .setStreamType(streamType)
            .setContentType(contentType)
            .setStreamDuration(streamDuration)
            .setMetadata(mediaMetadata)
            .build()
    }

    class Builder(url: String) {
        private val mediaData: MediaData

        /**
         * Sets the stream type. Required.
         * @param streamType One of [.STREAM_TYPE_NONE], [.STREAM_TYPE_BUFFERED], [.STREAM_TYPE_LIVE]
         * @return this instance for chain calls
         */
        fun setStreamType(@StreamType streamType: Int): Builder {
            mediaData.setStreamType(streamType)
            return this
        }

        /**
         * Sets the content type. Required.
         * @param contentType Valid content type, supported by Google Cast
         * @return this instance for chain calls
         */
        fun setContentType(contentType: String): Builder {
            mediaData.setContentType(contentType)
            return this
        }

        /**
         * Sets stream duration.
         * @param streamDuration Valid stream duration
         * @return this instance for chain calls
         */
        fun setStreamDuration(streamDuration: Long): Builder {
            mediaData.setStreamDuration(streamDuration)
            return this
        }

        /**
         * Sets the title.
         * @param title any String
         * @return this instance for chain calls
         */
        fun setTitle(title: String): Builder {
            mediaData.setTitle(title)
            return this
        }

        /**
         * Sets the subtitle.
         * @param subtitle any String
         * @return this instance for chain calls
         */
        fun setSubtitle(subtitle: String): Builder {
            mediaData.setSubtitle(subtitle)
            return this
        }

        /**
         * Sets the media type.
         * @param mediaType One of [.MEDIA_TYPE_GENERIC], [.MEDIA_TYPE_MOVIE], [.MEDIA_TYPE_TV_SHOW], [.MEDIA_TYPE_MUSIC_TRACK],
         * [.MEDIA_TYPE_PHOTO], [.MEDIA_TYPE_USER]
         * @return this instance for chain calls
         */
        fun setMediaType(@MediaType mediaType: Int): Builder {
            mediaData.setMediaType(mediaType)
            return this
        }

        /**
         * Adds the photo url
         * @param photoUrl valid url to image
         * @return this instance for chain calls
         */
        fun addPhotoUrl(photoUrl: String): Builder {
            mediaData.imageUrls.add(photoUrl)
            return this
        }

        /**
         * Sets up playing on start
         * @param autoPlay True if the media file should start automatically
         * @return this instance for chain calls
         */
        fun setAutoPlay(autoPlay: Boolean): Builder {
            mediaData.autoPlay = autoPlay
            return this
        }

        /**
         * Sets the start position
         * @param position Start position of video in milliseconds
         * @return this instance for chain calls
         */
        fun setPosition(position: Long): Builder {
            mediaData.position = position
            return this
        }

        fun build(): MediaData {
            return mediaData
        }

        /**
         * Create the MediaData builder
         * @param url String url of media data
         */
        init {
            mediaData = MediaData(url)
        }
    }

    companion object {
        const val STREAM_TYPE_NONE = 0
        const val STREAM_TYPE_BUFFERED = 1
        const val STREAM_TYPE_LIVE = 2
        const val STREAM_TYPE_INVALID = -1
        const val MEDIA_TYPE_GENERIC = 0
        const val MEDIA_TYPE_MOVIE = 1
        const val MEDIA_TYPE_TV_SHOW = 2
        const val MEDIA_TYPE_MUSIC_TRACK = 3
        const val MEDIA_TYPE_PHOTO = 4
        const val MEDIA_TYPE_USER = 100
        const val UNKNOWN_DURATION = -1L
    }
}