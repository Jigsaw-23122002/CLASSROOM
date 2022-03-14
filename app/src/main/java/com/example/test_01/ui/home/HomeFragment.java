package com.example.test_01.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test_01.BottomNavigation;
import com.example.test_01.NavigationDrawerActivity;
import com.example.test_01.SubjectClassParticular;
import com.example.test_01.SubjectsArrayAdapter;
import com.example.test_01.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ListView SubjectsListJoinedOrCreated = binding.SubjectsListJoinedOrCreated;
        SubjectsList(SubjectsListJoinedOrCreated);

        SubjectsListJoinedOrCreated.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), SubjectClassParticular.class);
                startActivity(intent);
            }
        });

        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void SubjectsList(ListView SubjectsListJoinedOrCreated){

        FirebaseFirestore UsersDatabase = FirebaseFirestore.getInstance();

        UsersDatabase.collection("RegisteredUsers").whereEqualTo("Email",
                ((NavigationDrawerActivity)getActivity()).getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot documents : task.getResult()){
                        ArrayList<Map> JoinedClasses = (ArrayList<Map>) documents.getData().get("ClassesJoined");
                        ArrayList<Map> CreatedClasses = (ArrayList<Map>) documents.getData().get("ClassesCreated");
                        JoinedClasses.addAll(CreatedClasses);
                        SubjectsArrayAdapter subjectsArrayAdapter = new SubjectsArrayAdapter(getContext(),JoinedClasses);
                        SubjectsListJoinedOrCreated.setAdapter(subjectsArrayAdapter);
                    }
                }
            }
        });
    }
}