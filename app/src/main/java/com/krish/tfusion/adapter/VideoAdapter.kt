package com.krish.tfusion.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.krish.tfusion.R
import com.krish.tfusion.model.Video

class VideoAdapter : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private var videoList = emptyList<Video>()

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val webView: WebView = itemView.findViewById(R.id.webView)
        val profilePic: ImageView = itemView.findViewById(R.id.ivProfilePic)
        val username: TextView = itemView.findViewById(R.id.username)
        val videoDesc: TextView = itemView.findViewById(R.id.videoDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.video_list_layout, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.apply {
            val currentVideo = videoList[position]
            webView.loadUrl(currentVideo.videoLink!!)
            webView.settings.javaScriptEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true;
            webView.settings.loadsImagesAutomatically = true

            profilePic.load(currentVideo.imageURL)
            username.text = currentVideo.videoTitle
            videoDesc.text = currentVideo.videoDesc
        }

    }

    override fun getItemCount(): Int = videoList.size


    fun setData(newData: ArrayList<Video>) {
        videoList = newData
        notifyDataSetChanged()
    }
}