package com.prashant.dronevideostream;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class VideoListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final List<String> fileNames;

    public VideoListAdapter(Activity context, List<String> fileNames) {
        super(context, R.layout.list_item_video, fileNames);
        this.context = context;
        this.fileNames = fileNames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = convertView != null ? convertView :
                inflater.inflate(R.layout.list_item_video, parent, false);

        TextView fileNameText = rowView.findViewById(R.id.video_name);
        ImageView icon = rowView.findViewById(R.id.video_icon);

        fileNameText.setText(fileNames.get(position));
        icon.setImageResource(R.drawable.ic_video_library); // Your video icon in drawable

        return rowView;
    }
}
