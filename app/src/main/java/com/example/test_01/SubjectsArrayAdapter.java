package com.example.test_01;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import javax.security.auth.Subject;

public class SubjectsArrayAdapter extends ArrayAdapter<Map> {

    String[] colors = {"one.jpg","two.jpg","three.jpg","four.jpg","five.jpg","six.jpg"};

    public SubjectsArrayAdapter(@NonNull Context context, ArrayList<Map> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentItem = convertView;

        if (currentItem == null) {
            currentItem = LayoutInflater.from(getContext()).inflate(R.layout.individual_subject_layout, parent, false);
        }

        Map particularSubjectPosition = getItem(position);

        assert currentItem != null;
        assert particularSubjectPosition != null;

        LinearLayout SubjectColors = (LinearLayout) currentItem.findViewById(R.id.SubjectColors);
        int i = position%6;
        if(i == 0){
            SubjectColors.setBackgroundResource(R.drawable.one);
        }
        else if(i == 1){
            SubjectColors.setBackgroundResource(R.drawable.two);
        }
        else if(i == 2){
            SubjectColors.setBackgroundResource(R.drawable.three);
        }
        else if(i == 3){
            SubjectColors.setBackgroundResource(R.drawable.four);
        }
        else if(i == 4){
            SubjectColors.setBackgroundResource(R.drawable.five);
        }
        else if(i == 5){
            SubjectColors.setBackgroundResource(R.drawable.six);
        }
        TextView Subject = currentItem.findViewById(R.id.ASSSubject);
        Subject.setText((String) particularSubjectPosition.get("ClassName"));
        Subject.setTextColor(Color.parseColor("white"));

        TextView Professor = currentItem.findViewById(R.id.ASSProfessor);
        Professor.setText((String) particularSubjectPosition.get("ClassTeacher"));
        Professor.setTextColor(Color.parseColor("white"));

        return currentItem;
    }
}
