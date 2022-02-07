package com.google.android.youtube.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import app.experiment.youtube.R
import app.experiment.youtube.cueVideo
import app.experiment.youtube.databinding.WidgetUnofficialPlayerViewBinding
import app.experiment.youtube.loadVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.concurrent.atomic.AtomicReference
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer as UnofficialYouTubePlayer

class UnofficialYouTubePlayerManager {

    private val recycledPlayerView = AtomicReference<YouTubePlayerView>(null)

    fun close(container: ViewGroup) {
        val playerView: YouTubePlayerView? =
            container.children.filterIsInstance<YouTubePlayerView>().firstOrNull()
        if (playerView != null) {
            val player =
                playerView.getTag(R.id.unofficial_youtube_player) as? UnofficialYouTubePlayer
            player?.pause()
            container.removeView(playerView)
            recycledPlayerView.set(playerView)
        }
    }

    fun open(
        container: ViewGroup,
        videoId: String,
        autoStart: Boolean = true,
    ) {
        val playerView: YouTubePlayerView = container.children.firstOrNull() as? YouTubePlayerView
            ?: (recycledPlayerView.get() ?: WidgetUnofficialPlayerViewBinding
                .inflate(LayoutInflater.from(container.context)).root)
                .also {
                    container.removeAllViews()
                    container.addView(it)
                }
        val player: UnofficialYouTubePlayer? =
            playerView.getTag(R.id.unofficial_youtube_player) as? UnofficialYouTubePlayer
        if (autoStart) {
            player?.loadVideo(videoId, 0F) ?: playerView.loadVideo(videoId)
        } else {
            player?.cueVideo(videoId, 0F) ?: playerView.cueVideo(videoId)
        }
    }
}
