package com.example.myapplication.adaptor

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.extensions.isFavourite
import com.example.myapplication.request.MusicInfo
import com.example.myapplication.request.name
import com.example.myapplication.request.primaryKey

class MusicListAdaptor(
    var dataSet: ArrayList<MusicInfo>,
    var imageMap: Map<String, Bitmap>,
    private val context: Context
) : RecyclerView.Adapter<MusicListAdaptor.BaseViewHolder>() {

    var listener: MusicListEventListener? = null

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bindData(data: MusicInfo, image: Bitmap?, isFavourite: Boolean, listener: MusicListEventListener?)
    }

    class TrackViewHolder(view: View) : BaseViewHolder(view) {
        val leftImageView: ImageView
        val titleLbl: TextView
        val descLbl: TextView
        val btnFavourite: Button

        val onColor: Int
        val offColor: Int

        var isFavourite: Boolean

        init {
            leftImageView = view.findViewById(R.id.artwork)
            titleLbl = view.findViewById(R.id.titleLbl)
            descLbl = view.findViewById(R.id.descLbl)
            btnFavourite = view.findViewById(R.id.cellBtn)
            isFavourite = false
            onColor = view.context.resources.getColor(R.color.blue)
            offColor = view.context.resources.getColor(R.color.white)
        }

        override fun bindData(data: MusicInfo, image: Bitmap?, isFavourite: Boolean, listener: MusicListEventListener?) {
            if (image != null){
                leftImageView.setImageBitmap(image)
            }else{
                leftImageView.setImageResource(R.drawable.music_note)
            }
            titleLbl.text = data.name
            descLbl.text = data.artistName
            val color = if (isFavourite) onColor else offColor
            btnFavourite.setBackgroundColor(color)
            this.isFavourite = isFavourite

            btnFavourite.setOnClickListener { view ->
                this.isFavourite = !this.isFavourite
                val color = if (this.isFavourite) onColor else offColor
                btnFavourite.background.setTint(color)
                listener?.onClick(data, this.isFavourite, view)
            }
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return TrackViewHolder(
            inflater.inflate(
                R.layout.music_display_cell,
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {

        val info = dataSet[position]
        viewHolder.bindData(info, imageMap[info.artworkUrl60], context.isFavourite(info), listener)
    }

    override fun getItemCount() = dataSet.size

    fun setMusicListEventListener(listener: MusicListEventListener) {
        this.listener = listener
    }
}