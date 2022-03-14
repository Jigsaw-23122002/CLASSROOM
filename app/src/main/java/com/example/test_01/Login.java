package com.example.test_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.test_01.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {

    EditText Email;
    EditText Password;
    EditText Username;
    Button LoginButton;
    TextView Status;
    FirebaseFirestore UsersDatabase = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.Password);
        Username = (EditText) findViewById(R.id.ClassroomName);
        LoginButton = (Button) findViewById(R.id.LoginButton);
        Status = (TextView) findViewById(R.id.Status);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(Email.getText());
                String password = String.valueOf(Password.getText());
                String username = String.valueOf(Username.getText());

                if(email.equals("") || username.equals("") || password.equals("")){
                    Status.setText("Fill all the fields correctly for Login.");
                }
                else {
                    UsersDatabase.collection("RegisteredUsers")
                            .whereEqualTo("Email", email)
                            .whereEqualTo("Password", password)
                            .whereEqualTo("Username", username)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    Status.setText("Invalid Email or Password");
                                } else {
                                    Intent MainPageIntent = new Intent(Login.this, NavigationDrawerActivity.class);
                                    MainPageIntent.putExtra("Email",email);
                                    MainPageIntent.putExtra("Password",password);
                                    MainPageIntent.putExtra("Username",username);
                                    startActivity(MainPageIntent);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}