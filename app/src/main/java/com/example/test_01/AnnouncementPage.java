package com.example.test_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnnouncementPage extends AppCompatActivity {

    EditText AnnouncementForClass;
    EditText AnnouncementTitle;
    Button UploadAnnouncementButton;
    TextView AnnouncementPageText;
    TextView AnnouncementPageStatus;

    public String ClassCode;
    public String Username;
    public String ClassName;
    public String ClassTeacher;

    FirebaseFirestore UsersDatabase = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_page);

        UploadAnnouncementButton = (Button) findViewById(R.id.UploadAnnouncementButton);
        AnnouncementForClass = (EditText) findViewById(R.id.AnnouncementForClass);
        AnnouncementTitle = (EditText) findViewById(R.id.AnnouncementTitle);
        AnnouncementPageText = (TextView) findViewById(R.id.AnnouncementPageText);
        AnnouncementPageStatus = (TextView) findViewById(R.id.AnnouncementPageStatus);

        Intent previousIntent = getIntent();
        ClassCode = previousIntent.getStringExtra("ClassCode");
        Username = previousIntent.getStringExtra("Username");
        ClassName = previousIntent.getStringExtra("ClassName");
        ClassTeacher = previousIntent.getStringExtra("ClassTeacher");

        AnnouncementPageText.setText("You are announcing for the class " +
                ClassName + " which is created by "+ ClassTeacher +
                " having code " + ClassCode);
        UploadAnnouncementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Title = String.valueOf(AnnouncementTitle.getText());
                String Description = String.valueOf(AnnouncementForClass.getText());

                if(Title.equals("") || Description.equals("")){
                    AnnouncementPageStatus.setText("Title and Description are mandatory for the announcement to be made.");
                    return;
                }
                else {
                    Map<String, Object> t = new HashMap<>();
                    t.put("AnnouncementTitle", Title);
                    t.put("AnnouncementDescription", Description);
                    t.put("Announcer", Username);

                    UsersDatabase.collection("ClassesCreated").whereEqualTo("ClassCode", ClassCode)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documents : task.getResult()) {

                                Map<String, Object> u = new HashMap<String, Object>();
                                u.put("ClassCode", documents.getData().get("ClassCode"));
                                u.put("ClassCreater", documents.getData().get("ClassCreater"));
                                u.put("ClassName", documents.getData().get("ClassName"));
                                u.put("StudentsOfClass", documents.getData().get("StudentsOfClass"));

                                ArrayList<Map> AnnouncementsOfClass = (ArrayList<Map>) documents.getData().get("AnnouncementsOfClass");
                                AnnouncementsOfClass.add(t);

                                u.put("AnnouncementsOfClass", AnnouncementsOfClass);

                                UsersDatabase.collection("ClassesCreated").document(documents.getId())
                                        .set(u);
                                AnnouncementTitle.setText("");
                                AnnouncementForClass.setText("");
                                AnnouncementPageStatus.setText("Successfully added announcement for the class");
                            }
                        }
                    });
                }
            }
        });
    }
}