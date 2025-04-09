package com.prashant.dronevideostream;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.ArrayList;

public class RecordedVideosActivity extends AppCompatActivity {

    private ArrayList<File> videoFiles = new ArrayList<>();
    private ArrayList<String> fileNames = new ArrayList<>();
    private ListView listView;
    private VideoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recorded_videos);


        listView = findViewById(R.id.list_view);

        // Load videos from both folders
        scanDirectory("Recordings");
        scanDirectory("DroneRecords");

        // Set custom adapter
        adapter = new VideoListAdapter(this, fileNames);
        listView.setAdapter(adapter);

        // Handle item clicks
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            File selectedFile = videoFiles.get(position);
            Uri fileUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", selectedFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Open with"));
        });
    }

    private void scanDirectory(String folderName) {
        File directory = new File(getExternalFilesDir(null), folderName);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".mp4") || f.getName().endsWith(".ts")) {
                        videoFiles.add(f);
                        fileNames.add(f.getName());
                    }
                }
            }
        }
    }
}