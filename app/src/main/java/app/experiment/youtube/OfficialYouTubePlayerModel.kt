package app.experiment.youtube

import android.view.View
import app.experiment.youtube.databinding.HolderYoutubePlayerOfficialBinding
import coil.load
import coil.request.Disposable
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

@EpoxyModelClass
abstract class OfficialYouTubePlayerModel :
    EpoxyModelWithHolder<OfficialYouTubePlayerModel.Holder>() {

    @EpoxyAttribute
    lateinit var videoId: String

    override fun getDefaultLayout(): Int = R.layout.holder_youtube_player_official

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

        val binding: HolderYoutubePlayerOfficialBinding by lazy(LazyThreadSafetyMode.NONE) {
            HolderYoutubePlayerOfficialBinding.bind(view)
        }

        override fun bindView(itemView: View) {
            this.view = itemView
        }
    }
}
