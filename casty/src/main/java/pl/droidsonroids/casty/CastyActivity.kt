package pl.droidsonroids.casty

import androidx.appcompat.app.AppCompatActivity
import pl.droidsonroids.casty.Casty
import androidx.annotation.CallSuper
import android.os.Bundle
import android.view.Menu
import android.view.View
import pl.droidsonroids.casty.R

/**
 * Extensible [AppCompatActivity], which helps with setting widgets
 */
abstract class CastyActivity : AppCompatActivity() {

    protected lateinit var casty: Casty

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        casty = Casty.create(this)
    }

    @CallSuper
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        if (findViewById<View?>(R.id.casty_mini_controller) == null) {
            casty.addMiniController()
        }
        casty.addMediaRouteMenuItem(menu)
        return true
    }

}