package app.experiment.youtube

import androidx.appcompat.app.AppCompatActivity

abstract class CommonYouTubeActivity : AppCompatActivity() {

    protected val autoStart: Boolean
        get() = intent?.getBooleanExtra(EXTRA_AUTO_START, true) ?: true

    companion object {
        const val EXTRA_AUTO_START = "EXTRA_AUTO_START"
    }
}
