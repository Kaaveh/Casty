package pl.droidsonroids.casty

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.UiThread
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.framework.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import pl.droidsonroids.casty.CastyPlayer.OnMediaLoadedListener

/**
 * Core class of Casty. It manages buttons/widgets and gives access to the media player.
 */
open class Casty : OnMediaLoadedListener {

    private lateinit var sessionManagerListener: SessionManagerListener<CastSession>
    private lateinit var onConnectChangeListener: OnConnectChangeListener
    private lateinit var onCastSessionUpdatedListener: OnCastSessionUpdatedListener
    private var castSession: CastSession? = null
    private lateinit var activity: Activity
    private lateinit var introductionOverlay: IntroductoryOverlay
    private lateinit var castyPlayer: CastyPlayer

    /**
     * Gives access to [CastyPlayer], which allows to control the media files.
     *
     * @return the instance of [CastyPlayer]
     */

    open fun getPlayer(): CastyPlayer? {
        return castyPlayer
    }

    //Needed for NoOp instance
    internal constructor() {
        //no-op
    }

    private constructor(activity: Activity) {
        this.activity = activity
        sessionManagerListener = createSessionManagerListener()
        castyPlayer = CastyPlayer(this)
        activity.application.registerActivityLifecycleCallbacks(createActivityCallbacks())
        CastContext.getSharedInstance(activity).addCastStateListener(createCastStateListener())
    }

    /**
     * Checks if a Google Cast device is connected.
     *
     * @return true if a Google Cast is connected, false otherwise
     */
    open fun isConnected(): Boolean {
        return castSession != null
    }

    /**
     * Adds the discovery menu item on a toolbar and creates Introduction Overlay
     * Should be used in [Activity.onCreateOptionsMenu].
     *
     * @param menu Menu in which MenuItem should be added
     */
    @UiThread
    open fun addMediaRouteMenuItem(menu: Menu) {
        activity.menuInflater.inflate(R.menu.casty_discovery, menu)
        setUpMediaRouteMenuItem(menu)
        val menuItem = menu.findItem(R.id.casty_media_route_menu_item)
        introductionOverlay = createIntroductionOverlay(menuItem)
    }

    /**
     * Makes [MediaRouteButton] react to discovery events.
     * Must be run on UiThread.
     *
     * @param mediaRouteButton Button to be set up
     */
    @UiThread
    open fun setUpMediaRouteButton(mediaRouteButton: MediaRouteButton) {
        CastButtonFactory.setUpMediaRouteButton(activity, mediaRouteButton)
    }

    /**
     * Adds the Mini Controller at the bottom of Activity's layout.
     * Must be run on UiThread.
     *
     * @return the Casty instance
     */
    @UiThread
    open fun withMiniController(): Casty? {
        addMiniController()
        return this
    }

    /**
     * Adds the Mini Controller at the bottom of Activity's layout
     * Must be run on UiThread.
     */
    @UiThread
    open fun addMiniController() {
        val contentView = activity.findViewById<View>(android.R.id.content) as ViewGroup
        val rootView = contentView.getChildAt(0)
        val linearLayout = LinearLayout(activity)
        val linearLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = linearLayoutParams
        contentView.removeView(rootView)
        val oldRootParams = rootView.layoutParams
        val rootParams = LinearLayout.LayoutParams(oldRootParams.width, 0, 1f)
        rootView.layoutParams = rootParams
        linearLayout.addView(rootView)
        activity.layoutInflater.inflate(R.layout.mini_controller, linearLayout, true)
        activity.setContentView(linearLayout)
    }

    /**
     * Sets [OnConnectChangeListener]
     *
     * @param onConnectChangeListener Connect change callback
     */
    open fun setOnConnectChangeListener(onConnectChangeListener: OnConnectChangeListener) {
        this.onConnectChangeListener = onConnectChangeListener
    }

    /**
     * Sets [OnCastSessionUpdatedListener]
     *
     * @param onCastSessionUpdatedListener Cast session updated callback
     */
    open fun setOnCastSessionUpdatedListener(onCastSessionUpdatedListener: OnCastSessionUpdatedListener) {
        this.onCastSessionUpdatedListener = onCastSessionUpdatedListener
    }

    private fun setUpMediaRouteMenuItem(menu: Menu) {
        CastButtonFactory.setUpMediaRouteButton(activity, menu, R.id.casty_media_route_menu_item)
    }

    private fun createCastStateListener(): CastStateListener {
        return CastStateListener { state ->
            if (state != CastState.NO_DEVICES_AVAILABLE) {
                showIntroductionOverlay()
            }
        }
    }

    private fun showIntroductionOverlay() {
        introductionOverlay.show()
    }

    private fun createSessionManagerListener(): SessionManagerListener<CastSession> {
        return object : SessionManagerListener<CastSession> {
            override fun onSessionStarted(castSession: CastSession, s: String) {
                activity.invalidateOptionsMenu()
                onConnected(castSession)
            }

            override fun onSessionEnded(castSession: CastSession, i: Int) {
                activity.invalidateOptionsMenu()
                onDisconnected()
            }

            override fun onSessionResumed(castSession: CastSession, b: Boolean) {
                activity.invalidateOptionsMenu()
                onConnected(castSession)
            }

            override fun onSessionStarting(castSession: CastSession) {
                //no-op
            }

            override fun onSessionStartFailed(castSession: CastSession, i: Int) {
                //no-op
            }

            override fun onSessionEnding(castSession: CastSession) {
                //no-op
            }

            override fun onSessionResuming(castSession: CastSession, s: String) {
                //no-op
            }

            override fun onSessionResumeFailed(castSession: CastSession, i: Int) {
                //no-op
            }

            override fun onSessionSuspended(castSession: CastSession, i: Int) {
                //no-op
            }
        }
    }

    private fun onConnected(castSession: CastSession) {
        this.castSession = castSession
        castyPlayer.setRemoteMediaClient(castSession.remoteMediaClient!!)
        if (::onConnectChangeListener.isInitialized)
            onConnectChangeListener.onConnected()
        if (::onCastSessionUpdatedListener.isInitialized)
            onCastSessionUpdatedListener.onCastSessionUpdated(
                castSession
            )
    }

    private fun onDisconnected() {
        castSession = null
        if (::onConnectChangeListener.isInitialized)
            onConnectChangeListener.onDisconnected()
        if (::onCastSessionUpdatedListener.isInitialized)
            onCastSessionUpdatedListener.onCastSessionUpdated(
                null
            )
    }

    private fun createActivityCallbacks(): ActivityLifecycleCallbacks {
        return object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                //no-op
            }

            override fun onActivityStarted(activity: Activity) {
                //no-op
            }

            override fun onActivityResumed(activity: Activity) {
                if (this@Casty.activity === activity) {
                    handleCurrentCastSession()
                    registerSessionManagerListener()
                }
            }

            override fun onActivityPaused(activity: Activity) {
                if (this@Casty.activity === activity) unregisterSessionManagerListener()
            }

            override fun onActivityStopped(activity: Activity) {
                //no-op
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                //no-op
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (this@Casty.activity === activity) {
                    activity.application.unregisterActivityLifecycleCallbacks(this)
                }
            }
        }
    }

    private fun createIntroductionOverlay(menuItem: MenuItem): IntroductoryOverlay {
        return IntroductoryOverlay.Builder(activity, menuItem)
            .setTitleText(R.string.casty_introduction_text)
            .setSingleTime()
            .build()
    }

    private fun registerSessionManagerListener() {
        CastContext.getSharedInstance(activity).sessionManager.addSessionManagerListener(
            sessionManagerListener,
            CastSession::class.java
        )
    }

    private fun unregisterSessionManagerListener() {
        CastContext.getSharedInstance(activity).sessionManager.removeSessionManagerListener(
            sessionManagerListener,
            CastSession::class.java
        )
    }

    private fun handleCurrentCastSession() {
        val newCastSession =
            CastContext.getSharedInstance(activity).sessionManager.currentCastSession
        if (castSession == null) {
            newCastSession?.let { onConnected(it) }
        } else {
            if (newCastSession == null) {
                onDisconnected()
            } else if (newCastSession !== castSession) {
                onConnected(newCastSession)
            }
        }
    }

    override fun onMediaLoaded() {
        startExpandedControlsActivity()
    }

    private fun startExpandedControlsActivity() {
        val intent = Intent(activity, ExpandedControlsActivity::class.java)
        activity.startActivity(intent)
    }

    interface OnConnectChangeListener {
        fun onConnected()
        fun onDisconnected()
    }

    interface OnCastSessionUpdatedListener {
        fun onCastSessionUpdated(castSession: CastSession?)
    }

    companion object Factory {
        private const val TAG = "Casty"
        var receiverId = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID
        var customCastOptions: CastOptions? = null

        /**
         * Sets the custom receiver ID. Should be used in the [Application] class.
         *
         * @param receiverId the custom receiver ID, e.g. Styled Media Receiver - with custom logo and background
         */
        fun configure(receiverId: String) {
            Factory.receiverId = receiverId
        }

        /**
         * Sets the custom CastOptions, should be used in the [Application] class.
         *
         * @param castOptions the custom CastOptions object, must include a receiver ID
         */
        fun configure(castOptions: CastOptions) {
            customCastOptions = castOptions
        }

        /**
         * Creates the Casty object.
         *
         * @param activity [Activity] in which Casty object is created
         * @return the Casty object
         */
        @JvmStatic
        fun create(activity: Activity): Casty {
            val playServicesState =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)
            return if (playServicesState == ConnectionResult.SUCCESS) {
                Casty(activity)
            } else {
                Log.w(
                    TAG,
                    "Google Play services not found on a device, Casty won't work."
                )
                CastyNoOp()
            }
        }
    }
}