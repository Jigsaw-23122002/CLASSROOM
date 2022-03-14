package com.example.test_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SubjectClassParticular extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    AnnounceFragment announceFragment = new AnnounceFragment();
    AssignmentFragment assignmentFragment = new AssignmentFragment();
    PeopleFragment peopleFragment = new PeopleFragment();

    public String ClassCode;
    public String ClassName;
    public String ClassTeacher;
    public String Email;
    public String Username;
    public String Color;

    public String getClassCode(){
        return ClassCode;
    }
    public String getClassName(){
        return ClassName;
    }
    public String getClassTeacher(){
        return ClassTeacher;
    }
    public String getEmail(){
        return Email;
    }
    public String getUsername(){
        return Username;
    }
    public String getColor(){
        return Color;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_class_particular);

        Intent previousIntent = getIntent();
        ClassCode = previousIntent.getStringExtra("ClassCode");
        ClassName = previousIntent.getStringExtra("ClassName");
        ClassTeacher = previousIntent.getStringExtra("ClassTeacher");
        Email = previousIntent.getStringExtra("Email");
        Username = previousIntent.getStringExtra("Username");
        Color = previousIntent.getStringExtra("SubjectColor");


        bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        getSupportFragmentManager().beginTransaction().replace(R.id.BottomNavFrame,announceFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                switch(item.getItemId()){
                    case R.id.navigation_home_BNB:
                        getSupportFragmentManager().beginTransaction().replace(R.id.BottomNavFrame,announceFragment).commit();
                        return true;

                    case R.id.navigation_dashboard_BNB:
                        getSupportFragmentManager().beginTransaction().replace(R.id.BottomNavFrame,peopleFragment).commit();
                        return true;

                    case R.id.navigation_notifications_BNB:
                        getSupportFragmentManager().beginTransaction().replace(R.id.BottomNavFrame,assignmentFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}