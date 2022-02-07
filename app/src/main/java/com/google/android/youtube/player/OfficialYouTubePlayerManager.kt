package com.google.android.youtube.player

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import app.experiment.youtube.BuildConfig

class OfficialYouTubePlayerManager(
    private val fragmentManager: FragmentManager
) {

    fun close(id: Int) {
        val playerFragment = findPlayerFragment()
        if (playerFragment != null && playerFragment.id == id) {
            playerFragment.pause()
        }
    }

    fun open(
        id: Int,
        videoId: String,
        autoStart: Boolean = true,
        playerStyle: YouTubePlayer.PlayerStyle = YouTubePlayer.PlayerStyle.DEFAULT
    ) {
        val playerFragment = findPlayerFragment() ?: ExperimentYouTubePlayerFragment.newInstance(
            apiKey = BuildConfig.YT_API_KEY,
            playerStyle = playerStyle
        )

        if (playerFragment.id != id) {
            fragmentManager.commitNow(allowStateLoss = true) {
                remove(playerFragment)
            }
            fragmentManager.executePendingTransactions()
            fragmentManager.commitNow(allowStateLoss = true) {
                replace(id, playerFragment, YOUTUBE_FRAGMENT_TAG)
            }
        }

        if (autoStart) {
            if (playerFragment.canPlay()) playerFragment.play()
            else playerFragment.play(videoId)
        } else {
            playerFragment.prepare(videoId)
        }
    }

    private fun findPlayerFragment(): ExperimentYouTubePlayerFragment? = fragmentManager
        .findFragmentByTag(YOUTUBE_FRAGMENT_TAG) as? ExperimentYouTubePlayerFragment

    private companion object {
        const val YOUTUBE_FRAGMENT_TAG = "YOUTUBE_FRAGMENT_TAG"
    }
}
