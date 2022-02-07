package app.experiment.youtube

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.experiment.youtube.databinding.ActivityMainBinding
import com.airbnb.epoxy.SimpleEpoxyModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.items.withModels {
            activityParams.forEachIndexed { _, (clazz, title, autoStart) ->
                object : SimpleEpoxyModel(android.R.layout.simple_list_item_1) {
                    override fun bind(view: View) {
                        super.bind(view)
                        view.findViewById<TextView>(android.R.id.text1).text = title
                    }
                }
                    .onClick {
                        val intent = Intent(this@MainActivity, clazz)
                        intent.putExtra(CommonYouTubeActivity.EXTRA_AUTO_START, autoStart)
                        startActivity(intent)
                    }
                    .id(clazz.canonicalName)
                    .addTo(this)
            }
        }
    }

    companion object {

        internal val activityParams = listOf(
            ActivityParams(
                clazz = OfficialYouTubePlayerActivity::class.java,
                title = "Official YouTube Player (auto start = true)",
                autoStart = true,
            ),
            ActivityParams(
                clazz = OfficialYouTubePlayerActivity::class.java,
                title = "Official YouTube Player (auto start = false)",
                autoStart = false,
            ),
            ActivityParams(
                clazz = UnofficialYouTubePlayerActivity::class.java,
                title = "Unofficial YouTube Player (auto start = true)",
                autoStart = true,
            ),
            ActivityParams(
                clazz = UnofficialYouTubePlayerActivity::class.java,
                title = "Unofficial YouTube Player (auto start = false)",
                autoStart = false,
            ),
            ActivityParams(
                clazz = OfficialYouTubePlayerManyVideosActivity::class.java,
                title = "Official YouTube Player (many videos, auto start = true)",
                autoStart = true,
            ),
            ActivityParams(
                clazz = UnofficialYouTubePlayerManyVideosActivity::class.java,
                title = "Unofficial YouTube Player (many videos, auto start = true)",
                autoStart = true,
            ),
            ActivityParams(
                clazz = OfficialYouTubePlayerCarouselActivity::class.java,
                title = "Official YouTube Player Carousel (auto start = true)",
                autoStart = true,
            ),
            ActivityParams(
                clazz = UnofficialYouTubePlayerCarouselActivity::class.java,
                title = "Unofficial YouTube Player Carousel (auto start = true)",
                autoStart = true,
            ),
        )
    }
}

internal data class ActivityParams(
    val clazz: Class<out Activity>,
    val title: String,
    val autoStart: Boolean = true,
)
