package app.experiment.youtube

import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import app.experiment.youtube.databinding.ActivityUnofficialPlayerSingleVideoBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

class UnofficialYouTubePlayerActivity : CommonYouTubeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUnofficialPlayerSingleVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Unofficial Player (auto start = $autoStart)"

        binding.youtubePlayer.enableAutomaticInitialization = false
        binding.youtubePlayer.initialize(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    if (autoStart) {
                        youTubePlayer.loadVideo("AjVShjnCJW4", 0f)
                    } else {
                        youTubePlayer.cueVideo("AjVShjnCJW4", 0f)
                    }
                }
            },
            handleNetworkEvents = true,
            playerOptions = IFramePlayerOptions.Builder()
                .controls(0)
                .build()
        )

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                binding.youtubePlayer.release()
            }
        })
    }
}
