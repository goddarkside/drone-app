package com.prashant.dronevideostream

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.prashant.dronevideostream.databinding.ActivityRecordedVideosBinding
import java.io.File

class RecordedVideosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordedVideosBinding

    private val videoFiles = mutableListOf<File>()
    private val fileNames = mutableListOf<String>()

    private lateinit var adapter: VideoRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecordedVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadVideos()
        setupRecyclerView()
    }

    private fun loadVideos() {
        scanDirectory("Recordings")
        scanDirectory("DroneRecords")
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = VideoRecyclerAdapter(
            videos = videoFiles,
            onClick = { file ->
                playVideo(file)
            },
            onShare = { file ->
                shareVideo(file)
            },
            onDelete = { file, position ->
                confirmDelete(file, position)
            }
        )

        binding.recyclerView.adapter = adapter
    }

    private fun confirmDelete(file: File, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete video?")
            .setMessage("This video will be permanently deleted.")
            .setPositiveButton("Delete") { _, _ ->
                if (file.delete()) {
                    videoFiles.removeAt(position)
                    binding.recyclerView.adapter?.notifyItemRemoved(position)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun shareVideo(file: File) {
        val uri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share video"))
    }

    private fun playVideo(file: File) {
        val uri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "video/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }


    private fun scanDirectory(folderName: String) {
        val directory = File(getExternalFilesDir(null), folderName)
        if (!directory.exists()) return

        directory.listFiles()?.forEach { file ->
            if (file.extension == "mp4" || file.extension == "ts") {
                videoFiles.add(file)
                fileNames.add(file.name)
            }
        }
    }
}
