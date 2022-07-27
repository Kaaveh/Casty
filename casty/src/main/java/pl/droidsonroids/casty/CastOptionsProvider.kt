package pl.droidsonroids.casty

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import pl.droidsonroids.casty.Casty
import com.google.android.gms.cast.framework.media.NotificationOptions
import pl.droidsonroids.casty.ExpandedControlsActivity
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.framework.media.MediaIntentReceiver
import com.google.android.gms.cast.framework.SessionProvider
import java.util.*

class CastOptionsProvider : OptionsProvider {

    override fun getCastOptions(context: Context): CastOptions {
        val customCastOptions = Casty.customCastOptions
        return if (customCastOptions == null) {

            val buttonActions = createButtonActions()
            val compatButtonAction = intArrayOf(1, 3)

            val notificationOptions = NotificationOptions.Builder()
                .setActions(buttonActions, compatButtonAction)
                .setTargetActivityClassName(ExpandedControlsActivity::class.java.name)
                .build()

            val mediaOptions = CastMediaOptions.Builder()
                .setNotificationOptions(notificationOptions)
                .setExpandedControllerActivityClassName(ExpandedControlsActivity::class.java.name)
                .build()

            CastOptions.Builder()
                .setReceiverApplicationId(Casty.receiverId)
                .setCastMediaOptions(mediaOptions)
                .build()
        } else {
            customCastOptions
        }
    }

    private fun createButtonActions(): List<String> {
        return listOf(
            MediaIntentReceiver.ACTION_REWIND,
            MediaIntentReceiver.ACTION_TOGGLE_PLAYBACK,
            MediaIntentReceiver.ACTION_FORWARD,
            MediaIntentReceiver.ACTION_STOP_CASTING
        )
    }

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? {
        return null
    }

}