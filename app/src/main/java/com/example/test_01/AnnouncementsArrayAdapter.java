package com.example.test_01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Map;

public class AnnouncementsArrayAdapter extends ArrayAdapter<Map>{

    public AnnouncementsArrayAdapter(@NonNull Context context, ArrayList<Map> arrayList){
        super(context,0,arrayList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentItem = convertView;

        if (currentItem == null) {
            currentItem = LayoutInflater.from(getContext()).inflate(R.layout.individual_announcement_layout, parent, false);
        }

        Map particularSubjectPosition = getItem(position);

        assert currentItem != null;
        assert particularSubjectPosition != null;
        TextView Subject = currentItem.findViewById(R.id.Title);
        Subject.setText((String) particularSubjectPosition.get("AnnouncementTitle"));

        TextView Professor = currentItem.findViewById(R.id.Descriptor);
        Professor.setText((String) particularSubjectPosition.get("AnnouncementDescription"));

        TextView AnnouncementBy = currentItem.findViewById(R.id.AnnouncementBy);
        AnnouncementBy.setText("-" + (String)particularSubjectPosition.get("Announcer"));

        return currentItem;
    }
}
