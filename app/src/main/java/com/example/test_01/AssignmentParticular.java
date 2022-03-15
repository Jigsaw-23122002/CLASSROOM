package com.example.test_01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class AssignmentParticular extends AppCompatActivity {

    TextView AssignTitle;
    TextView AssignDescription;
    TextView AssignPDF;
    TextView PDFSelectedForSubmission;
    Button UploadAssignmentForSubmissionButton;
    ListView SubmittedPDFListview;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference_01;
    FirebaseFirestore DatabaseUsers = FirebaseFirestore.getInstance();

    Uri pdfSelectedForSubmission;

    public static String Username;
    public static String Email;
    public static String ClassCode;
    public static String ClassName;
    public static String ClassTeacher;
    public static String AssignmentTitle;
    public static String AssignmentDescription;
    public static String AssignmentCode;

    putPDF AssignmentPDF;
    ArrayList<putPDF> submittedPDF;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("Changed", String.valueOf(PDFSelectedForSubmission.getText()));
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(String.valueOf(PDFSelectedForSubmission.getText()).equals("")){
                UploadAssignmentForSubmissionButton.setText("Select");
            }
            else{
                UploadAssignmentForSubmissionButton.setText("Upload");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_particular);

        Intent previousIntent = getIntent();
        Username = previousIntent.getStringExtra("Username");
        Email = previousIntent.getStringExtra("Email");
        ClassCode = previousIntent.getStringExtra("ClassCode");
        ClassName = previousIntent.getStringExtra("ClassName");
        ClassTeacher = previousIntent.getStringExtra("ClassTeacher");
        AssignmentTitle = previousIntent.getStringExtra("AssignmentTitle");
        AssignmentDescription = previousIntent.getStringExtra("AssignmentDescription");
        AssignmentCode = previousIntent.getStringExtra("AssignmentCode");

        AssignTitle = (TextView) findViewById(R.id.AssignTitle);
        AssignDescription = (TextView) findViewById(R.id.AssignDescription);
        AssignPDF = (TextView) findViewById(R.id.AssignPDF);
        PDFSelectedForSubmission = (TextView) findViewById(R.id.PDFSelectedForSubmission);
        UploadAssignmentForSubmissionButton = (Button) findViewById(R.id.UploadAssignmentForSubmissionButton);
        SubmittedPDFListview = (ListView) findViewById(R.id.SubmittedPDFListview);

        PDFSelectedForSubmission.addTextChangedListener(textWatcher);
        submittedPDF = new ArrayList<>();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(ClassCode.replace("#", ""));
        databaseReference_01 = FirebaseDatabase.getInstance().getReference(AssignmentCode.replace("#", "") + "_submissions");
        Log.d("Status", AssignmentCode);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d("Status", ds.getValue(com.example.test_01.putPDF.class).getId());
                    if (ds.getValue(com.example.test_01.putPDF.class).getId().equals(AssignmentCode + ".pdf")) {

                        AssignmentPDF = ds.getValue(com.example.test_01.putPDF.class);
                        Log.d("Status", "Assign hogaya bhai");
                    }
                }
                AssignTitle.setText(AssignmentTitle);
                AssignDescription.setText(AssignmentDescription);
                AssignPDF.setText(AssignmentCode);

                AssignPDF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AssignmentParticular.this, viewPDF.class);
                        if (AssignmentPDF == null) {
                            Log.d("Status", "Null hai bhai");
                        }
                        intent.putExtra("fileUrl", AssignmentPDF.getUrl());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        retrievePDFFiles();

        UploadAssignmentForSubmissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UploadAssignmentForSubmissionButton.getText().equals("Select")){
                    selectPDF();
                }
                else if(UploadAssignmentForSubmissionButton.getText().equals("Upload")){

                    DatabaseUsers.collection("AssignmentsCreated").whereEqualTo("AssignmentCode",AssignmentCode)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(QueryDocumentSnapshot documents : task.getResult()){

                                Map<String,Object> u = new HashMap<>();
                                u.put("AssignmentCode",documents.getData().get("AssignmentCode"));
                                u.put("AssignmentDescription",documents.getData().get("AssignmentDescription"));
                                u.put("AssignmentTitle",documents.getData().get("AssignmentTitle"));
                                u.put("ClassCode",documents.getData().get("ClassCode"));

                                ArrayList<Map> submittedStudents = (ArrayList<Map>) documents.getData().get("AssignmentSubmissions");


                                for(Map k:submittedStudents){
                                    if(k.get("StudentEmail").equals(Email)){
                                        Toast.makeText(AssignmentParticular.this, "File already submitted", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                Map<String,Object> t = new HashMap<>();
                                t.put("CheckStatus","Not Checked");
                                t.put("StudentEmail",Email);
                                t.put("StudentName",Username);
                                t.put("StudentFileCode",Email);

                                submittedStudents.add(t);

                                u.put("AssignmentsSubmissions",submittedStudents);

                                uploadPDFFileFirebase(pdfSelectedForSubmission);

                                DatabaseUsers.collection("AssignmentsCreated")
                                        .document(documents.getId()).set(u);
                            }
                        }
                    });
                }
            }
        });
        SubmittedPDFListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssignmentParticular.this, viewPDF.class);
                intent.putExtra("fileUrl", submittedPDF.get(position).getUrl())  ;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
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
            PDFSelectedForSubmission.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
            pdfSelectedForSubmission = data.getData();
        }
    }
    private void uploadPDFFileFirebase(Uri data){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.show();

        StorageReference reference = storageReference.child(Email + ".pdf");

        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while(!uriTask.isComplete());
                Uri uri = uriTask.getResult();

                putPDF pdf = new putPDF(PDFSelectedForSubmission.getText().toString(),uri.toString(),Email+".pdf");
                databaseReference_01.child(databaseReference_01.push().getKey()).setValue(pdf);
                Toast.makeText(AssignmentParticular.this,"File Uploaded",Toast.LENGTH_LONG).show();
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
    private void retrievePDFFiles(){
        databaseReference_01.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //A DataSnapshot instance contains data from a Firebase Database location. Any time
                //you read Database data, you receive the data as a DataSnapshot.
                for(DataSnapshot ds : snapshot.getChildren()){
                    //Here snapshot.getChildren() gives all the pdf's data stored in the database.

                    putPDF pdf = ds.getValue(com.example.test_01.putPDF.class);
                    //In above line the getValue()function returns the data in the form which is
                    //mentioned in the argument of the function

                    submittedPDF.add(pdf);
                }
                String[] uploadsName = new String[submittedPDF.size()];
                for( int i = 0 ; i < uploadsName.length ; i++){
                    uploadsName[i] = submittedPDF.get(i).getId();
                }

                //Below is the custom layout for the list view
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AssignmentParticular.this,
                        android.R.layout.simple_list_item_1,uploadsName){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(20);
                        return view;
                    }
                };
                SubmittedPDFListview.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}