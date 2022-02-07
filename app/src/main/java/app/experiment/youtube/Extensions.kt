package app.experiment.youtube

import android.util.Log
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

private typealias UnofficialYouTubePlayer = com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

fun YouTubePlayerView.cueVideo(videoId: String, showControl: Boolean = false) {
    initialize(
        youTubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: UnofficialYouTubePlayer) {
                setTag(R.id.unofficial_youtube_player, youTubePlayer)
                Log.i("YtExp", "Player: $youTubePlayer")
                youTubePlayer.cueVideo(videoId, 0F)
            }

            override fun onError(
                youTubePlayer: UnofficialYouTubePlayer,
                error: PlayerConstants.PlayerError
            ) {
                Log.i("YtExp", "Error: $error, Player: $youTubePlayer")
            }
        },
        playerOptions = IFramePlayerOptions.Builder()
            .controls(if (showControl) 1 else 0)
            .build()
    )
}

fun YouTubePlayerView.loadVideo(videoId: String, showControl: Boolean = false) {
    initialize(
        youTubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: UnofficialYouTubePlayer) {
                setTag(R.id.unofficial_youtube_player, youTubePlayer)
                Log.i("YtExp", "Player: $youTubePlayer")
                youTubePlayer.loadVideo(videoId, 0F)
            }

            override fun onError(
                youTubePlayer: UnofficialYouTubePlayer,
                error: PlayerConstants.PlayerError
            ) {
                Log.i("YtExp", "Error: $error, Player: $youTubePlayer")
            }
        },
        playerOptions = IFramePlayerOptions.Builder()
            .controls(if (showControl) 1 else 0)
            .build()
    )
}

fun YouTubePlayerView.setupHolder(
    videoId: String,
    startSeconds: Float = 0F,
) {
    initialize(
        youTubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: UnofficialYouTubePlayer) {
                youTubePlayer.cueVideo(videoId = videoId, startSeconds = startSeconds)
            }
        },
        playerOptions = IFramePlayerOptions.Builder()
            .controls(0)
            .build()
    )
}

fun String.getVideoThumbnail(): String = "https://img.youtube.com/vi/$this/maxresdefault.jpg"
