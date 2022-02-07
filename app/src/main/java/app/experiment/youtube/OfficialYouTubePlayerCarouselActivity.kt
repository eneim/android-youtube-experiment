package app.experiment.youtube

import android.content.Context
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import app.experiment.youtube.databinding.ActivityVideosCarouselOfficialBinding
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.VisibilityState
import com.google.android.youtube.player.OfficialYouTubePlayerManager
import com.google.android.youtube.player.YouTubePlayer

class OfficialYouTubePlayerCarouselActivity : CommonYouTubeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityVideosCarouselOfficialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Video Carousel by Official YouTube player (auto start = $autoStart)"

        val videos = listOf(
            "HpdO5Kq3o7Y",
            "5qap5aO4i9A",
            "XEGQWb8IzUw",
            "kgx4WGK0oNU",
            "coYw-eVU0Ks",
            "DWcJFNfaw9c",
            "rOX7qlep9Do",
        )

        Carousel.setDefaultGlobalSnapHelperFactory(object : Carousel.SnapHelperFactory() {
            override fun buildSnapHelper(context: Context?): SnapHelper = PagerSnapHelper()
        })

        val playerManager = OfficialYouTubePlayerManager(supportFragmentManager)

        binding.videosOfficial.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.videosOfficial.numViewsToShowOnScreen = 1.15f
        binding.videosOfficial.withModels {
            videos.forEachIndexed { index, videoId ->
                OfficialYouTubePlayerModel_()
                    .videoId(videoId)
                    .id("$index :: $videoId")
                    .onVisibilityStateChanged { _, view, visibilityState ->
                        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
                            view.binding.playerFragment.isVisible = true
                            view.binding.thumbnail.isVisible = false
                            playerManager.open(
                                id = view.binding.playerFragment.id,
                                videoId = videoId,
                                autoStart = true,
                                playerStyle = YouTubePlayer.PlayerStyle.CHROMELESS,
                            )
                        } else {
                            playerManager.close(view.binding.playerFragment.id)
                            view.binding.thumbnail.isVisible = true
                            view.binding.playerFragment.isVisible = false
                        }
                    }
                    .addTo(this)
            }
        }

        EpoxyVisibilityTracker().attach(binding.videosOfficial)
    }
}
