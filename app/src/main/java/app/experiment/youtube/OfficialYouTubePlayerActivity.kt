package app.experiment.youtube

import android.os.Bundle
import app.experiment.youtube.databinding.ActivityOfficialPlayerSingleVideoBinding

class OfficialYouTubePlayerActivity : CommonYouTubeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOfficialPlayerSingleVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Official Player (auto start = $autoStart)"

        if (savedInstanceState == null) {
            binding.youtubePlayer.prepare(BuildConfig.YT_API_KEY, "AjVShjnCJW4")
            binding.youtubePlayer.isPlayWhenReady = autoStart
        }
    }
}
