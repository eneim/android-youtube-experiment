package com.google.android.youtube.player

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class OfficialYouTubePlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), DefaultLifecycleObserver {

    private val playerView: YouTubePlayerView
    private val activity = requireNotNull(context as? FragmentActivity) {
        "This View needs to be used in an Activity."
    }
    private var apiKey: String = ""
    private val componentsListener = ComponentsListener()

    private var player: YouTubePlayer? = null

    var isPlayWhenReady: Boolean = false
        set(value) {
            field = value
            // TODO: use ComponentsListener and an additional flag to track the loading/ready state.
            if (value) player?.play() else player?.pause()
        }

    // TODO: define attributes and get from it.
    private var playerStyle: YouTubePlayer.PlayerStyle = YouTubePlayer.PlayerStyle.DEFAULT
    private var playerState: Bundle? = null

    private val provider: InternalPlayerProvider

    init {
        activity.lifecycle.addObserver(this)
        playerView = YouTubePlayerView(
            activity,
            null,
            0,
            object : YouTubePlayerView.b {
                override fun a(
                    view: YouTubePlayerView,
                    apiKey: String,
                    onInitializedListener: YouTubePlayer.OnInitializedListener
                ) = throw UnsupportedOperationException("Use the initialize method instead.")

                override fun a(view: YouTubePlayerView) = view.a()
            }
        )

        super.addView(
            playerView,
            LayoutParams(MATCH_PARENT, MATCH_PARENT)
        )

        provider = InternalPlayerProvider(playerView)
    }

    override fun onSaveInstanceState(): Parcelable {
        playerState = playerView.e()
        return PlayerState(
            superState = super.onSaveInstanceState(),
            apiKey = apiKey,
            localState = playerState,
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is PlayerState) {
            super.onRestoreInstanceState(state)
        } else {
            super.onRestoreInstanceState(state.superState)
            apiKey = state.apiKey
            playerState = state.localState
            initialize(apiKey = apiKey, listener = null)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        playerState = playerView.e()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        player?.release()
        player = null
    }

    private fun initialize(apiKey: String, listener: YouTubePlayer.OnInitializedListener?) =
        provider.initialize(
            apiKey,
            InternalInitializedListener(listener)
        )

    fun prepare(apiKey: String, videoId: String) =
        player?.let {
            if (isPlayWhenReady) it.loadVideo(videoId) else it.cueVideo(videoId)
        } ?: initialize(
            apiKey = apiKey,
            listener = object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider?,
                    player: YouTubePlayer?,
                    isRestored: Boolean
                ) {
                    if (!isRestored) {
                        if (isPlayWhenReady) player?.loadVideo(videoId)
                        else player?.cueVideo(videoId)
                    } else {
                        if (isPlayWhenReady) player?.play()
                    }
                }

                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider?,
                    result: YouTubeInitializationResult?
                ) = Unit
            })

    internal class PlayerState : BaseSavedState {

        val apiKey: String
        val localState: Bundle?

        constructor(
            superState: Parcelable?,
            apiKey: String,
            localState: Bundle?
        ) : super(superState) {
            this.apiKey = apiKey
            this.localState = localState
        }

        constructor(parcel: Parcel) : super(parcel) {
            this.apiKey = parcel.readString().orEmpty()
            this.localState = parcel.readBundle(Bundle::class.java.classLoader)
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeString(apiKey)
            parcel.writeBundle(localState)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<PlayerState> {
            override fun createFromParcel(parcel: Parcel): PlayerState {
                return PlayerState(parcel)
            }

            override fun newArray(size: Int): Array<PlayerState?> {
                return arrayOfNulls(size)
            }
        }

    }

    private inner class InternalPlayerProvider(
        val playerView: YouTubePlayerView,
    ) : YouTubePlayer.Provider {
        override fun initialize(apiKey: String, listener: YouTubePlayer.OnInitializedListener?) {
            this@OfficialYouTubePlayerView.apiKey = apiKey
            playerView.a(
                activity,
                this,
                apiKey,
                InternalInitializedListener(listener),
                playerState,
            )
            playerState = null
        }
    }

    private inner class InternalInitializedListener(
        private val delegate: YouTubePlayer.OnInitializedListener? = null
    ) : YouTubePlayer.OnInitializedListener {
        override fun onInitializationSuccess(
            provider: YouTubePlayer.Provider?,
            player: YouTubePlayer?,
            isRestored: Boolean
        ) {
            Log.i("YtExp", "Player: $player, Provider: $provider")
            player?.apply {
                setPlayerStateChangeListener(componentsListener)
                setPlaybackEventListener(componentsListener)
                setShowFullscreenButton(false)
                setPlayerStyle(playerStyle)
                setShowFullscreenButton(true) // Handling fullscreen transition is tricky.
                setManageAudioFocus(true)
            }

            this@OfficialYouTubePlayerView.player = player
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

    private inner class ComponentsListener :
        YouTubePlayer.PlayerStateChangeListener,
        YouTubePlayer.PlaybackEventListener {

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
    }
}
