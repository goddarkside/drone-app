# Drone Video Streamer
**Drone Video Streamer** is an Android application designed to stream live drone footage using RTSP URLs. The app leverages VLC’s powerful LibVLC library for real-time playback and offers one-click recording functionality. Users can also access and manage previously recorded drone videos directly from the app.

## 🚀 Features

- 🎥 **Live RTSP Video Streaming**  
  Stream high-quality live drone feeds directly within the app.

- ⏺️ **One-Tap Recording**  
  Record live streams and store them locally with timestamped filenames.

- 📂 **Access Recorded Videos**  
  Browse and play saved recordings with the system’s default media player.

- 📁 **Clean and Modern UI**  

- **Language:** Java  
- **Framework:** Android SDK  
- **Video Playback:** [LibVLC (org.videolan.libvlc) https://code.videolan.org/videolan/libvlc)  
- **UI:** XML (Custom layouts, buttons, overlays)

## 📁 Project Structure


com.prashant.dronevideostream/
├── MainActivity.java            # Home screen with RTSP input
├── VideoPlayerActivity.java    # Stream and record RTSP video
├── VideoListActivity.java      # Displays list of recorded videos
├── VideoListAdapter.java       # Custom adapter for video file listing
└── res/
    ├── layout/                 # XML layout files
    ├── drawable/               # Custom button backgrounds, icons
    └── values/                 # Strings, themes, and colours


🧪 How It Works

1. Start Streaming
        *Launch the app.
        *Enter a valid RTSP URL (e.g., from your drone camera).
        *Tap Play to start live streaming via LibVLC.

2. Record Video
       *While streaming, tap the record button to begin saving.
       *Video is stored as an .mp4 file in:
	/Android/data/com.prashant.dronevideostream/files/DroneRecords/
      *Filenames are automatically generated using the current timestamp:
 	drone_record_YYYYMMDD_HHMMSS.mp4
3. View Recordings
       *Tap the folder icon on the main screen.
       *Navigate the list of saved recordings.
        *Tap a file to open it with any installed media player.

⚙️ Setup & Installation
	*Clone the repository or download the source.
	*Add the following dependency to your build.gradle:
		implementation 'org.videolan.android:libvlc-all:3.5.0'
	*Sync your project.
	*Build and run on a real Android device (API 21+ recommended).
App LINK   
	Google Drive Link:https://drive.google.com/drive/folders/1AH7MWYBu3W_rDHDQv1Ceiw2oTMEnCW7S?usp=sharing
👤 Author
Prashant seth
Android Developer | Drone Tech Enthusiast
📫 Contact: 
LinkedIn:https://www.linkedin.com/in/prashant-seth 
GitHub: https://github.com/goddarkside
Email: Prashant143seth@gmail.com 
