package com.example.test_01.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test_01.NavigationDrawerActivity;
import com.example.test_01.databinding.FragmentSlideshowBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText ClassCode = binding.ClassCodeText;
        final TextView JoiningClassStatus = binding.JoiningClassStatus;
        final Button JoinClassButton = binding.JoinClassButton;
        JoinClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinClass(String.valueOf(ClassCode.getText()),JoiningClassStatus);
            }
        });

        //slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void JoinClass(String code,TextView JoiningClassStatus){

        FirebaseFirestore UsersDatabase = FirebaseFirestore.getInstance();

        UsersDatabase.collection("ClassesCreated").whereEqualTo("ClassCode",code)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    JoiningClassStatus.setText("Incorrect Class Code");
                }
                else{
                    for(QueryDocumentSnapshot documents : task.getResult()){
                        Map<String,Object> w = new HashMap<>();
                        w.put("ClassCode",documents.getData().get("ClassCode"));
                        w.put("ClassName",documents.getData().get("ClassName"));
                        w.put("ClassTeacher",documents.getData().get("ClassCreater"));

                        Map<String,Object> t = new HashMap<>();
                        t.put("ClassCode",documents.getData().get("ClassCode"));
                        t.put("ClassCreater",documents.getData().get("ClassCreater"));
                        t.put("ClassName",documents.getData().get("ClassName"));
                        t.put("AnnouncementsOfClass",documents.getData().get("AnnouncementsOfClass"));

                        ArrayList<String> u = (ArrayList<String>) documents.getData().get("StudentsOfClass");
                        u.add(((NavigationDrawerActivity)getActivity()).getUsername() + " (" +
                                ((NavigationDrawerActivity)getActivity()).getEmail() + ")");
                        t.put("StudentsOfClass",u);

                        UsersDatabase.collection("ClassesCreated")
                                .document(documents.getId()).set(t);

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
                                    t.put("ClassesCreated",documents.getData().get("ClassesCreated"));

                                    ArrayList<Map> u = (ArrayList<Map>) documents.getData().get("ClassesJoined");
                                    for(Map item : u){
                                        if(item.get("ClassCode").equals(code)){
                                            JoiningClassStatus.setText("Class Already Joined");
                                            return;
                                        }
                                    }
                                    u.add(w);
                                    t.put("ClassesJoined",u);

                                    UsersDatabase.collection("RegisteredUsers")
                                            .document(documents.getId()).set(t);
                                    JoiningClassStatus.setText("Class Joined Successfully");
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}