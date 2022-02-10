package com.google.android.youtube.player

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import app.experiment.youtube.BuildConfig
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle

/**
 * A replacement for [YouTubePlayerSupportFragment] because that class uses the non-AndroidX
 * Fragment.
 */
class ExperimentYouTubePlayerFragment : Fragment(),
    YouTubePlayer.PlayerStateChangeListener,
    YouTubePlayer.PlaybackEventListener {

    private var provider: InternalPlayerProvider? = null

    private var initialData: InitialData? = null
    private var playerState: Bundle? = null
    private var player: YouTubePlayer? = null

    private val apiKey: String by lazy(LazyThreadSafetyMode.NONE) {
        requireNotNull(
            (arguments?.getString(ARGS_API_KEY) ?: BuildConfig.YT_API_KEY)
                .takeIf(String::isNotEmpty)
        )
    }

    private var playerStyle: PlayerStyle = PlayerStyle.DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerState = savedInstanceState?.getBundle(PLAYER_STATE_KEY)
        playerStyle = savedInstanceState?.getSerializable(ARGS_PLAYER_STYLE) as? PlayerStyle
            ?: arguments?.getSerializable(ARGS_PLAYER_STYLE) as? PlayerStyle ?: PlayerStyle.DEFAULT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = YouTubePlayerView(
        requireActivity(),
        null,
        0,
        object : YouTubePlayerView.b {
            override fun a(
                view: YouTubePlayerView,
                apiKey: String,
                onInitializedListener: YouTubePlayer.OnInitializedListener
            ) = throw UnsupportedOperationException("Use the Fragment's initialize method instead.")

            override fun a(view: YouTubePlayerView) = view.a()
        }
    )
        .also { playerView ->
            provider = InternalPlayerProvider(playerView)
            val listener = initialData?.listener ?: return@also
            initialData = null
            initialize(listener)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                provider?.playerView?.a()
            }

            override fun onResume(owner: LifecycleOwner) {
                provider?.playerView?.b()
            }

            override fun onPause(owner: LifecycleOwner) {
                provider?.playerView?.c()
            }

            override fun onStop(owner: LifecycleOwner) {
                provider?.playerView?.d()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                player?.release()
                player = null
                val activity = this@ExperimentYouTubePlayerFragment.activity
                provider?.playerView?.c(activity != null && activity.isFinishing)
                provider = null
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ARGS_PLAYER_STYLE, playerStyle)
        val playerState = provider?.playerView?.e() ?: playerState
        if (playerState != null) outState.putBundle(PLAYER_STATE_KEY, playerState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        initialData = null
        provider = null
    }

    fun canPlay() = player != null

    fun play() {
        player?.play()
    }

    fun pause() {
        player?.pause()
    }

    fun initialize(listener: YouTubePlayer.OnInitializedListener?) {
        val provider = this.provider
        @Suppress("LiftReturnOrAssignment")
        if (provider != null) {
            provider.initialize(apiKey, InternalInitializedListener(listener))
            initialData = null
        } else {
            initialData = InitialData(listener = listener)
        }
    }

    fun prepare(videoId: String) = initialize(object : YouTubePlayer.OnInitializedListener {
        override fun onInitializationSuccess(
            provider: YouTubePlayer.Provider?,
            player: YouTubePlayer?,
            isRestored: Boolean
        ) {
            if (!isRestored) player?.cueVideo(videoId)
        }

        override fun onInitializationFailure(
            provider: YouTubePlayer.Provider?,
            result: YouTubeInitializationResult?
        ) = Unit
    })

    //region YouTubePlayer.PlayerStateChangeListener
    override fun onLoading() {
        Log.d("YtExp", "Loading $player")
    }

    override fun onLoaded(videoId: String?) {
        Log.d("YtExp", "Loaded $videoId, $player")
    }

    override fun onAdStarted() {
        Log.d("YtExp", "AdStarted")
    }

    override fun onVideoStarted() {
        Log.d("YtExp", "VideoStarted")
    }

    override fun onVideoEnded() {
        Log.d("YtExp", "VideoEnded")
    }

    override fun onError(error: YouTubePlayer.ErrorReason?) {
        Log.d("YtExp", "Error: $error")
    }
    //endregion

    //region YouTubePlayer.PlaybackEventListener
    override fun onPlaying() {
        Log.d("YtExp", "Playing")
    }

    override fun onPaused() {
        Log.d("YtExp", "Paused")
    }

    override fun onStopped() {
        Log.d("YtExp", "Stopped")
    }

    override fun onBuffering(buffering: Boolean) {
        Log.d("YtExp", "Buffering: $buffering")
    }

    override fun onSeekTo(position: Int) {
        Log.d("YtExp", "Seek to: $position")
    }
    //endregion

    private data class InitialData(
        val listener: YouTubePlayer.OnInitializedListener?
    )

    private inner class InternalPlayerProvider(
        val playerView: YouTubePlayerView,
    ) : YouTubePlayer.Provider {
        override fun initialize(apiKey: String, listener: YouTubePlayer.OnInitializedListener?) {
            playerView.a(
                requireActivity(),
                this,
                apiKey,
                InternalInitializedListener(listener),
                playerState,
            )
            playerState = null
        }
    }

    private inner class InternalInitializedListener constructor(
        private val delegate: YouTubePlayer.OnInitializedListener? = null
    ) : YouTubePlayer.OnInitializedListener {
        override fun onInitializationSuccess(
            provider: YouTubePlayer.Provider?,
            player: YouTubePlayer?,
            isRestored: Boolean
        ) {
            Log.i("YtExp", "Player: $player, Provider: $provider")
            player?.apply {
                setPlayerStateChangeListener(this@ExperimentYouTubePlayerFragment)
                setPlaybackEventListener(this@ExperimentYouTubePlayerFragment)
                setShowFullscreenButton(false)
                setPlayerStyle(playerStyle)
                // setManageAudioFocus(true)
            }

            this@ExperimentYouTubePlayerFragment.player = player
            delegate?.onInitializationSuccess(provider, player, isRestored)
        }

        override fun onInitializationFailure(
            provider: YouTubePlayer.Provider?,
            result: YouTubeInitializationResult?
        ) {
            Log.i("YtExp", "Result: $result, Provider: $provider")
            player?.release()
            player = null
            delegate?.onInitializationFailure(provider, result)
        }
    }

    companion object {
        private const val PLAYER_STATE_KEY = "ExperimentYouTubePlayerFragment.KEY_PLAYER_VIEW_STATE"
        private const val ARGS_PLAYER_STYLE = "ARGS_PLAYER_STYLE"
        private const val ARGS_API_KEY = "ARGS_API_KEY"

        fun newInstance(
            apiKey: String,
            playerStyle: PlayerStyle = PlayerStyle.DEFAULT,
        ) = ExperimentYouTubePlayerFragment().apply {
            arguments = bundleOf(
                ARGS_API_KEY to apiKey,
                ARGS_PLAYER_STYLE to playerStyle
            )
        }
    }
}

// Loads the video and starts playing it as soon as it is loaded
fun ExperimentYouTubePlayerFragment.play(videoId: String, startSeconds: Int = 0) {
    initialize(object : YouTubePlayer.OnInitializedListener {
        override fun onInitializationSuccess(
            provider: YouTubePlayer.Provider?,
            player: YouTubePlayer?,
            isRestored: Boolean
        ) {
            if (!isRestored) player?.loadVideo(videoId, startSeconds)
        }

        override fun onInitializationFailure(
            provider: YouTubePlayer.Provider?,
            result: YouTubeInitializationResult?
        ) = Unit
    })
}
