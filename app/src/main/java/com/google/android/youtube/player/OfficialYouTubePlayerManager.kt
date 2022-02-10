package com.google.android.youtube.player

import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import app.experiment.youtube.BuildConfig
import app.experiment.youtube.databinding.WidgetOfficialPlayerViewBinding
import java.util.concurrent.atomic.AtomicReference

class OfficialYouTubePlayerManager {

    private val recycledPlayerView = AtomicReference<OfficialYouTubePlayerView>(null)

    fun pause(container: ViewGroup) {
        val playerView: OfficialYouTubePlayerView? =
            container.children.firstOrNull() as? OfficialYouTubePlayerView
        playerView?.isPlayWhenReady = false
    }

    fun play(
        container: ViewGroup,
        videoId: String,
        autoStart: Boolean = true,
    ) {
        val playerView: OfficialYouTubePlayerView =
            container.children.firstOrNull() as? OfficialYouTubePlayerView
                ?: recycledPlayerView.get()
                ?: WidgetOfficialPlayerViewBinding.inflate(from(container.context)).root
                    .also(recycledPlayerView::set)

        container.ensureChild(playerView)
        playerView.prepare(BuildConfig.YT_API_KEY, videoId)
        playerView.isPlayWhenReady = autoStart
    }

    private companion object {
        private fun View.removeFromParent() {
            val parent = this.parent as? ViewGroup ?: return
            parent.removeView(this)
        }

        fun ViewGroup.ensureChild(view: View) {
            if (view.parent == this) return
            view.removeFromParent()
            removeAllViews()
            addView(view)
        }
    }
}
