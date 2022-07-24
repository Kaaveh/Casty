package pl.droidsonroids.casty

import android.view.Menu
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity

/**
 * Fullscreen media controls
 */
class ExpandedControlsActivity : ExpandedControllerActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.casty_discovery, menu)
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.casty_media_route_menu_item)
        return true
    }

}