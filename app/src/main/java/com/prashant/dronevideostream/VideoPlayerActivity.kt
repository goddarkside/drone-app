package com.prashant.dronevideostream

import android.net.Uri
import android.os.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.prashant.dronevideostream.databinding.ActivityVideoPlayerBinding
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding

    private var libVLC: LibVLC? = null
    private var mediaPlayer: MediaPlayer? = null

    private var currentRtspUrl = ""
    private var isRecording = false

    private var recordStartTime = 0L
    private var recordFile: File? = null

    private val hudHandler = Handler(Looper.getMainLooper())
    private val hudRunnable = object : Runnable {
        override fun run() {
            updateRecordingHud()
            hudHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentRtspUrl = intent.getStringExtra("RTSP_URL") ?: ""

        initVlc()
        playStream(currentRtspUrl, false)

        binding.backButton.setOnClickListener { finish() }
        binding.recordButton.setOnClickListener { toggleRecording() }
    }

    private fun initVlc() {
        libVLC = LibVLC(this, arrayListOf())
        mediaPlayer = MediaPlayer(libVLC)

        mediaPlayer?.vlcVout?.apply {
            setVideoView(binding.videoSurface)
            attachViews()
        }
    }

    private fun toggleRecording() {
        isRecording = !isRecording

        if (isRecording) {
            recordStartTime = System.currentTimeMillis()
            binding.recordingHud.visibility = View.VISIBLE
            hudHandler.post(hudRunnable)
        } else {
            stopRecordingHud()
        }

        mediaPlayer?.stop()
        playStream(currentRtspUrl, isRecording)
    }

    private fun playStream(rtspUrl: String, record: Boolean) {
        val mediaOptions = arrayListOf<String>()

        if (record) {
            val dir = File(getExternalFilesDir(null), "DroneRecords")
            if (!dir.exists()) dir.mkdirs()

            val timestamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())

            recordFile = File(dir, "drone_record_$timestamp.mp4")

            val sout =
                ":sout=#duplicate{dst=display,dst=std{access=file,mux=ts,dst=${recordFile!!.absolutePath}}}"

            mediaOptions.add(sout)
            mediaOptions.add(":no-sout-all")
            mediaOptions.add(":sout-keep")
        }

        val media = Media(libVLC, Uri.parse(rtspUrl)).apply {
            setHWDecoderEnabled(true, false)
            mediaOptions.forEach { addOption(it) }
        }

        mediaPlayer?.media = media
        media.release()
        mediaPlayer?.play()
    }

    private fun updateRecordingHud() {
        val elapsed = System.currentTimeMillis() - recordStartTime
        val seconds = (elapsed / 1000).toInt()
        val mins = seconds / 60
        val secs = seconds % 60

        binding.tvRecTimer.text = String.format("%02d:%02d", mins, secs)

        recordFile?.let {
            val sizeMb = it.length() / (1024 * 1024)
            binding.tvRecSize.text = "$sizeMb MB"
        }

        binding.recDot.alpha =
            if (binding.recDot.alpha == 1f) 0.3f else 1f
    }

    private fun stopRecordingHud() {
        hudHandler.removeCallbacks(hudRunnable)
        binding.recordingHud.visibility = View.GONE
        recordFile = null
    }

    override fun onStop() {
        super.onStop()
        if (isRecording) {
            isRecording = false
            stopRecordingHud()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.vlcVout?.detachViews()
        mediaPlayer?.release()
        libVLC?.release()
    }
}
