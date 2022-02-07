package app.experiment.youtube

import android.os.Bundle
import app.experiment.youtube.databinding.ActivityOfficialPlayerManyVideosBinding
import com.google.android.youtube.player.ExperimentYouTubePlayerFragment
import com.google.android.youtube.player.play
import com.google.android.youtube.player.prepare

class OfficialYouTubePlayerManyVideosActivity : CommonYouTubeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOfficialPlayerManyVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Official Player (many videos, auto start = $autoStart)"

        val topVideo = supportFragmentManager
            .findFragmentById(R.id.top_video) as ExperimentYouTubePlayerFragment

        if (autoStart) {
            topVideo.play(videoId = "AjVShjnCJW4")
        } else {
            topVideo.prepare(videoId = "AjVShjnCJW4")
        }

        val bottomVideo = supportFragmentManager
            .findFragmentById(R.id.bottom_video) as ExperimentYouTubePlayerFragment
        bottomVideo.prepare(videoId = "ljBAlUOQAGE")
    }
}
