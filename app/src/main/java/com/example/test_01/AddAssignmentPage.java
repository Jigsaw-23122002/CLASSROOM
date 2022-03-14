package com.example.test_01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddAssignmentPage extends AppCompatActivity {

    ImageView BackFromAddAssignmentPage;
    EditText AddAssignmentTitle;
    EditText AddAssignmentDescription;
    EditText AddAssignmentFileCode;
    Button UploadFileButton;
    Button AddAssignmentAndCreate;

    public static String Username;
    public static String Email;
    public static String ClassName;
    public static String ClassCode;
    public static String ClassTeacher;

    FirebaseFirestore DatabaseUsers = FirebaseFirestore.getInstance();
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Uri Data;

    private void selectPDF(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF file"),12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null){

            AddAssignmentFileCode.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
            Data = data.getData();

        }
    }
    private void uploadPDFFileFirebase(Uri data,String file_name){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.show();

        StorageReference reference = storageReference.child(file_name + ".pdf");

        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while(!uriTask.isComplete());
                Uri uri = uriTask.getResult();

                putPDF pdf = new putPDF(AddAssignmentFileCode.getText().toString(),uri.toString(),file_name+".pdf");
                databaseReference.child(databaseReference.push().getKey()).setValue(pdf);
                Toast.makeText(AddAssignmentPage.this,"File Uploaded",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override

            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploading File...." + (int)progress + "%");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment_page);

        storageReference = FirebaseStorage.getInstance().getReference();

        AddAssignmentTitle = (EditText) findViewById(R.id.AddAssignmentTitle);
        AddAssignmentDescription = (EditText) findViewById(R.id.AddAssignmentDescription);
        AddAssignmentFileCode = (EditText) findViewById(R.id.AddAssignmentFileCode);
        UploadFileButton = (Button) findViewById(R.id.UploadFileButton);
        AddAssignmentAndCreate = (Button) findViewById(R.id.CreateAssignmentAndAdd);

        Intent previousIntent = getIntent();
        Username = previousIntent.getStringExtra("Username");
        Email = previousIntent.getStringExtra("Email");
        ClassCode = previousIntent.getStringExtra("ClassCode");
        ClassName = previousIntent.getStringExtra("ClassName");
        ClassTeacher = previousIntent.getStringExtra("ClassTeacher");

        databaseReference = FirebaseDatabase.getInstance().getReference(ClassCode.replace("#",""));

        UploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });

        AddAssignmentAndCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String AssignmentTitle = String.valueOf(AddAssignmentTitle.getText());
                String AssignmentDescription = String.valueOf(AddAssignmentDescription.getText());
                String AssignmentFileCode = String.valueOf(AddAssignmentFileCode.getText());

                Map<String,Object> u = new HashMap<>();
                u.put("AssignmentDescription",AssignmentDescription);
                u.put("AssignmentTitle",AssignmentTitle);
                u.put("ClassCode",ClassCode);
                ArrayList<Map> AssignmentSubmissions = new ArrayList<>();
                u.put("AssignmentSubmissions",AssignmentSubmissions);

                DatabaseUsers.collection("ClassesCreated").whereEqualTo("ClassCode",ClassCode)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot documents : task.getResult()){
                            Long AssignmentNumber = (Long) documents.getData().get("AssignmentNumber");
                            Map<String,Object> Obj = new HashMap<>();
                            Obj.put("ClassCode",documents.getData().get("ClassCode"));
                            Obj.put("ClassCreater",documents.getData().get("ClassCreater"));
                            Obj.put("ClassName",documents.getData().get("ClassName"));
                            Obj.put("AnnouncementsOfClass",documents.getData().get("AnnouncementsOfClass"));
                            Obj.put("StudentsOfClass",documents.getData().get("StudentsOfClass"));
                            Obj.put("AssignmentNumber",AssignmentNumber+1);


                            u.put("AssignmentCode",ClassCode + "_assignment_" + AssignmentNumber);

                            uploadPDFFileFirebase(Data,ClassCode + "_assignment_" + AssignmentNumber);

                            DatabaseUsers.collection("ClassesCreated")
                                    .document(documents.getId()).set(Obj);
                            DatabaseUsers.collection("AssignmentsCreated").add(u);
                        }
                    }
                });
            }
        });
    }
}