package com.prashant.dronevideostream;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        EditText rtspUrlInput = findViewById(R.id.rtsp_url);
        Button playButton = findViewById(R.id.play_button);
        ImageButton fileButton = findViewById(R.id.file_button);




        fileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordedVideosActivity.class);
            startActivity(intent);
        });

        playButton.setOnClickListener(v -> {
            String rtspUrl = rtspUrlInput.getText().toString().trim();
            if (!rtspUrl.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                intent.putExtra("RTSP_URL", rtspUrl);
                startActivity(intent);
            }

        });


    }


}