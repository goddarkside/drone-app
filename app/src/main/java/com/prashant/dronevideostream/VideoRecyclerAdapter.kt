package com.prashant.dronevideostream

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prashant.dronevideostream.databinding.ListItemVideoBinding
import kotlinx.coroutines.*
import java.io.File

class VideoRecyclerAdapter(
    private val videos: MutableList<File>,
    private val onClick: (File) -> Unit,
    private val onShare: (File) -> Unit,
    private val onDelete: (File, Int) -> Unit
) : RecyclerView.Adapter<VideoRecyclerAdapter.VideoViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Main + Job())

    inner class VideoViewHolder(
        private val binding: ListItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var job: Job? = null

        fun bind(file: File, position: Int) {
            binding.txtName.text = file.name
            
            // Clear previous data / placeholders
            binding.imgThumbnail.setImageBitmap(null)
            binding.txtMeta.text = "..."

            job?.cancel()
            job = adapterScope.launch {
                val thumbnail = withContext(Dispatchers.IO) { getThumbnail(file) }
                val metaText = withContext(Dispatchers.IO) { buildMeta(file) }

                if (isActive) {
                    binding.imgThumbnail.setImageBitmap(thumbnail)
                    binding.txtMeta.text = metaText
                }
            }

            binding.root.setOnClickListener {
                onClick(file)
            }

            binding.root.setOnLongClickListener {
                showMenu(file, position)
                true
            }
        }

        private fun showMenu(file: File, position: Int) {
            val popup = android.widget.PopupMenu(binding.root.context, binding.root)
            popup.menuInflater.inflate(R.menu.menu_video_item, popup.menu)

            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_share -> {
                        onShare(file)
                        true
                    }
                    R.id.action_delete -> {
                        onDelete(file, position)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ListItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position], position)
    }

    override fun getItemCount(): Int = videos.size

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        adapterScope.cancel()
    }

    // ------------------------
    // HELPER FUNCTIONS
    // ------------------------

    private fun getThumbnail(file: File): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val bitmap = retriever.getFrameAtTime(0)
            retriever.release()
            bitmap
        } catch (_: Exception) {
            null
        }
    }

    private fun buildMeta(file: File): String {
        val duration = getDuration(file)
        val size = formatSize(file.length())
        return "${formatDuration(duration)}  •  $size"
    }

    private fun getDuration(file: File): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val duration =
                retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )?.toLong() ?: 0L
            retriever.release()
            duration
        } catch (_: Exception) {
            0L
        }
    }

    private fun formatDuration(ms: Long): String {
        val totalSeconds = (ms / 1000).toInt()
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun formatSize(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb >= 1024) {
            String.format("%.0f GB", mb / 1024)
        } else {
            String.format("%.0f MB", mb)
        }
    }

    fun removeAt(position: Int) {
        videos.removeAt(position)
        notifyItemRemoved(position)
    }
}
