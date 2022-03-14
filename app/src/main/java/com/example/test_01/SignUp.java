package com.example.test_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    Button LoginPageRedirectButton;
    Button SignUpButton;
    EditText Email;
    EditText Password;
    EditText Username;
    TextView Statuses;
    FirebaseFirestore UsersDatabase = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        LoginPageRedirectButton = (Button) findViewById(R.id.LoginPageRedirectButton);
        SignUpButton = (Button) findViewById(R.id.SignUpButton);
        Email = (EditText) findViewById(R.id.Emails);
        Password = (EditText) findViewById(R.id.Passwords);
        Username = (EditText) findViewById(R.id.ClassroomNames);
        Statuses = (TextView) findViewById(R.id.Statuses);

        LoginPageRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent loginIntent = new Intent(SignUp.this,Login.class);
               startActivity(loginIntent);
            }
        });
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(Email.getText());
                String password = String.valueOf(Password.getText());
                String username = String.valueOf(Username.getText());

                if(email.endsWith("@gmail.com") && !email.contains(" ")) {

                    ArrayList<Map> classesCreated = new ArrayList<Map>();
                    ArrayList<Map> classesJoined = new ArrayList<Map>();
                    Map<String, Object> User = new HashMap<>();
                    User.put("Email", email);
                    User.put("Password", password);
                    User.put("Username", username);
                    User.put("ClassesCreated", classesCreated);
                    User.put("ClassesJoined", classesJoined);

                    UsersDatabase.collection("RegisteredUsers").whereEqualTo("Email", email)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    UsersDatabase.collection("RegisteredUsers").add(User)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d("Status", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Status", "Error adding document", e);
                                                }
                                            });
                                    Log.d("Status", "User Registered Successfully");
                                    Statuses.setText("User Registered Successfully");
                                } else {
                                    Log.d("Status", "User already exists");
                                    Statuses.setText("User already exists");
                                }
                            }
                        }
                    });
                }
                else{
                    Statuses.setText("Enter the valid Email.");
                }
            }
        });
    }
}