package com.prashant.dronevideostream

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.prashant.dronevideostream.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen ON
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {

        // START STREAM
        binding.playButton.setOnClickListener {
            val url = binding.rtspUrl.text.toString().trim()

            if (url.isEmpty()) {
                Toast.makeText(this, "Enter RTSP / RTMP URL", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("RTSP_URL", url)
            startActivity(intent)
        }

        // OPEN RECORDED VIDEOS
        binding.fileButton.setOnClickListener {
            startActivity(
                Intent(this, RecordedVideosActivity::class.java)
            )
        }
    }
}
