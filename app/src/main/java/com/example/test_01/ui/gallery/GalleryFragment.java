package com.example.test_01.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test_01.NavigationDrawerActivity;
import com.example.test_01.databinding.FragmentGalleryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button CreateClassButton = binding.CreateClassButton;
        final EditText SubjectName = binding.SubjectName;
        final EditText ClassCode = binding.ClassCode;
        final TextView StatusOfCreateClass = binding.StatusOfCreateClass;

        CreateClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateClassFunction(String.valueOf(SubjectName.getText()),String.valueOf(ClassCode.getText()),
                        StatusOfCreateClass);
            }
        });

        //final TextView textView = binding.textGallery;
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void CreateClassFunction(String subjectName,String subjectCode,TextView StatusOfCreateClass){

        FirebaseFirestore UsersDatabase = FirebaseFirestore.getInstance();

        UsersDatabase.collection("ClassesCreated").whereEqualTo("ClassCode",subjectCode)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    Map<String, Object> Class = new HashMap<>();
                    Class.put("ClassCode", subjectCode);
                    Class.put("ClassCreater", ((NavigationDrawerActivity)getActivity()).getUsername());
                    Class.put("ClassName", subjectName);
                    ArrayList<Map> announcements = new ArrayList<Map>();
                    ArrayList<Map> students = new ArrayList<Map>();
                    Class.put("AnnouncementsOfClass", announcements);
                    Class.put("StudentsOfClass", students);
                    Class.put("AssignmentNumber",0);
                    UsersDatabase.collection("ClassesCreated").add(Class);
                    StatusOfCreateClass.setText("Class Created Successfully");

                    UsersDatabase.collection("RegisteredUsers").whereEqualTo("Email",
                            ((NavigationDrawerActivity)getActivity()).getEmail())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(QueryDocumentSnapshot documents : task.getResult()){

                                Map<String,Object> t = new HashMap<>();
                                t.put("Email",documents.getData().get("Email"));
                                t.put("Password",documents.getData().get("Password"));
                                t.put("Username",documents.getData().get("Username"));
                                t.put("ClassesJoined",documents.getData().get("ClassesJoined"));

                                Map<String,Object> u = new HashMap<>();
                                u.put("ClassCode",subjectCode);
                                u.put("ClassName",subjectName);

                                ArrayList<Map> v = (ArrayList<Map>) documents.getData().get("ClassesCreated");
                                v.add(u);

                                t.put("ClassesCreated",v);

                                UsersDatabase.collection("RegisteredUsers")
                                        .document(documents.getId()).set(t);
                            }
                        }
                    });
                }
                else {
                    StatusOfCreateClass.setText("Class Code already exists.");
                }
            }
        });
    }
}