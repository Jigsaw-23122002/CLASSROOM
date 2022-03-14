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

public class AssignmentsArrayAdapter extends ArrayAdapter<Map> {
    public AssignmentsArrayAdapter(@NonNull Context context, ArrayList<Map> arrayList){
        super(context,0,arrayList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentItem = convertView;

        if (currentItem == null) {
            currentItem = LayoutInflater.from(getContext()).inflate(R.layout.assignment_box_layout, parent, false);
        }

        Map particularSubjectPosition = getItem(position);

        assert currentItem != null;
        assert particularSubjectPosition != null;
        TextView Subject = currentItem.findViewById(R.id.ASSSubject);
        Subject.setText((String) particularSubjectPosition.get("AssignmentTitle"));

        TextView Professor = currentItem.findViewById(R.id.ASSProfessor);
        Professor.setText((String) particularSubjectPosition.get("AssignmentDescription"));

        return currentItem;
    }
}
