package pl.droidsonroids.casty

import android.view.Menu
import androidx.mediarouter.app.MediaRouteButton

internal class CastyNoOp : Casty() {

    private val castyPlayer: CastyPlayer

    init {
        castyPlayer = CastyPlayerNoOp()
    }

    override fun getPlayer(): CastyPlayer {
        return castyPlayer
    }

    override fun isConnected(): Boolean {
        return false
    }

    override fun addMediaRouteMenuItem(menu: Menu) {
        //no-op
    }

    override fun setUpMediaRouteButton(mediaRouteButton: MediaRouteButton) {
        //no-op
    }

    override fun withMiniController(): Casty {
        return this
    }

    override fun addMiniController() {
        //no-op
    }

    override fun setOnConnectChangeListener(onConnectChangeListener: OnConnectChangeListener) {
        //no-op
    }

    override fun setOnCastSessionUpdatedListener(onCastSessionUpdatedListener: OnCastSessionUpdatedListener) {
        //no-op
    }

}