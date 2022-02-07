package app.experiment.youtube

import android.os.Bundle
import app.experiment.youtube.databinding.ActivityOfficialPlayerSingleVideoBinding
import com.google.android.youtube.player.ExperimentYouTubePlayerFragment
import com.google.android.youtube.player.play
import com.google.android.youtube.player.prepare

class OfficialYouTubePlayerActivity : CommonYouTubeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOfficialPlayerSingleVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Official Player (auto start = $autoStart)"

        val fragment = supportFragmentManager
            .findFragmentById(R.id.youtube_player_container) as ExperimentYouTubePlayerFragment

        if (savedInstanceState == null) {
            if (autoStart) {
                fragment.play(videoId = "AjVShjnCJW4")
            } else {
                fragment.prepare(videoId = "AjVShjnCJW4")
            }
        }
    }
}
