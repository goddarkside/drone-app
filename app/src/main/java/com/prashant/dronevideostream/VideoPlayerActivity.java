package com.prashant.dronevideostream;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VideoPlayerActivity extends AppCompatActivity {
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private String currentRtspUrl;
    private boolean isRecording = false;
    private View recordingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_player);

        surfaceView = findViewById(R.id.video_surface);
        ImageButton backBtn = findViewById(R.id.back_button);
        ImageButton recordBtn = findViewById(R.id.record_button);

        recordingIndicator = findViewById(R.id.recording_indicator);

        ArrayList<String> options = new ArrayList<>();
        libVLC = new LibVLC(this, options);
        mediaPlayer = new MediaPlayer(libVLC);
        mediaPlayer.getVLCVout().setVideoView(surfaceView);
        mediaPlayer.getVLCVout().attachViews();

        currentRtspUrl = getIntent().getStringExtra("RTSP_URL");

        // Play without recording initially
        playStream(currentRtspUrl, false);

        backBtn.setOnClickListener(v -> finish());

        recordBtn.setOnClickListener(v -> {
            isRecording = !isRecording;
            if (isRecording) {
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
                recordingIndicator.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
                recordingIndicator.setVisibility(View.GONE);
            }

            // Stop previous playback and restart with or without recording
            mediaPlayer.stop();
            playStream(currentRtspUrl, isRecording);
        });
    }

    private void playStream(String rtspUrl, boolean record) {
        ArrayList<String> mediaOptions = new ArrayList<>();

        if (record) {
            File dir = new File(getExternalFilesDir(null), "DroneRecords");
            if (!dir.exists()) dir.mkdirs();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File outputFile = new File(dir, "drone_record_" + timestamp + ".mp4");
            String outputPath = outputFile.getAbsolutePath();

            // VLC sout options
            String sout = ":sout=#duplicate{dst=display,dst=std{access=file,mux=ts,dst=" + outputPath + "}}";
            mediaOptions.add(sout);
            mediaOptions.add(":no-sout-all");
            mediaOptions.add(":sout-keep");

            Log.d("DroneRecording", "Recording drone stream to: " + outputPath);
        }

        Media media = new Media(libVLC, Uri.parse(rtspUrl));
        media.setHWDecoderEnabled(true, false);

        for (String opt : mediaOptions) {
            media.addOption(opt);
        }

        mediaPlayer.setMedia(media);
        mediaPlayer.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (libVLC != null) {
            libVLC.release();
        }
    }
}