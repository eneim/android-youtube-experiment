package app.experiment.youtube

import android.view.View
import app.experiment.youtube.databinding.HolderYoutubePlayerUnofficialBinding
import coil.load
import coil.request.Disposable
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

@EpoxyModelClass
abstract class UnofficialYouTubePlayerModel :
    EpoxyModelWithHolder<UnofficialYouTubePlayerModel.Holder>() {

    @EpoxyAttribute
    lateinit var videoId: String

    override fun getDefaultLayout(): Int = R.layout.holder_youtube_player_unofficial

    private var disposable: Disposable? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        disposable?.dispose()
        disposable = holder.binding.thumbnail.load(videoId.getVideoThumbnail())
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
        disposable?.dispose()
        disposable = null
    }

    class Holder : EpoxyHolder() {

        private lateinit var view: View

        val binding: HolderYoutubePlayerUnofficialBinding by lazy(LazyThreadSafetyMode.NONE) {
            HolderYoutubePlayerUnofficialBinding.bind(view)
        }

        override fun bindView(itemView: View) {
            this.view = itemView
        }
    }
}
