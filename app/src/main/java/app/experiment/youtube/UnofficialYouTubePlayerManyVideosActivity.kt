package app.experiment.youtube

import android.os.Bundle
import app.experiment.youtube.databinding.ActivityUnofficialPlayerManyVideosBinding

class UnofficialYouTubePlayerManyVideosActivity : CommonYouTubeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUnofficialPlayerManyVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Unofficial Player (many videos, auto start = $autoStart)"

        // Note: check the layout for YouTube player options. The player Views are initialized
        // automatically.
    }
}
