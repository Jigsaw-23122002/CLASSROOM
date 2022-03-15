package com.example.test_01;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AssignmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignmentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AssignmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssignmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AssignmentFragment newInstance(String param1, String param2) {
        AssignmentFragment fragment = new AssignmentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    FloatingActionButton AddAssignmentFloatingButton;
    FirebaseFirestore DatabaseUsers = FirebaseFirestore.getInstance();
    ArrayList<Map> AssignmentsArrayList;
    ListView AssignmentsPageOfClass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AddAssignmentFloatingButton = (FloatingActionButton) view.findViewById(R.id.AddAssignmentFloatingButton);
        AssignmentsPageOfClass = (ListView) view.findViewById(R.id.AssignmentsListOfClass);

        DatabaseUsers.collection("AssignmentsCreated").whereEqualTo("ClassCode",((SubjectClassParticular)getActivity()).getClassCode())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                AssignmentsArrayList = new ArrayList<>();
                for(QueryDocumentSnapshot ds : task.getResult()){

                    Map<String,Object> u = new HashMap<>();
                    u.put("AssignmentTitle",String.valueOf(ds.getData().get("AssignmentTitle")));
                    u.put("AssignmentDescription",String.valueOf(ds.getData().get("AssignmentDescription")));
                    u.put("AssignmentCode",String.valueOf(ds.getData().get("AssignmentCode")));
                    AssignmentsArrayList.add(u);
                }
                if(getContext()!=null) {
                    AssignmentsArrayAdapter AssignmentsArrayAdapter = new AssignmentsArrayAdapter(getContext(),
                            AssignmentsArrayList);
                    AssignmentsPageOfClass.setAdapter(AssignmentsArrayAdapter);
                }
            }
        });

        AddAssignmentFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseUsers.collection("RegisteredUsers").whereEqualTo("Email",
                        ((SubjectClassParticular)getActivity()).getEmail())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot documents : task.getResult()){
                            Boolean status = false;
                            ArrayList<Map> ClassesCreated = (ArrayList<Map>) documents.getData().get("ClassesCreated");
                            for(Map item : ClassesCreated){
                                if(item.get("ClassCode").equals(((SubjectClassParticular)getActivity()).getClassCode())){
                                    status = true;
                                }
                            }
                            if(status.equals(false)){
                                Log.d("Status","You are not the teacher of the class");
                                Toast.makeText(getContext(),"You are not the teacher of the class",Toast.LENGTH_SHORT);
                            }
                            else{
                                Intent AddAssignmentIntent = new Intent(getContext(),AddAssignmentPage.class);
                                AddAssignmentIntent.putExtra("Username",((SubjectClassParticular)getActivity()).getUsername());
                                AddAssignmentIntent.putExtra("Email",((SubjectClassParticular)getActivity()).getEmail());
                                AddAssignmentIntent.putExtra("ClassName",((SubjectClassParticular)getActivity()).getClassName());
                                AddAssignmentIntent.putExtra("ClassCode",((SubjectClassParticular)getActivity()).getClassCode());
                                AddAssignmentIntent.putExtra("ClassTeacher",((SubjectClassParticular)getActivity()).getClassTeacher());
                                startActivity(AddAssignmentIntent);
                            }
                        }
                    }
                });
            }
        });
        AssignmentsPageOfClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),AssignmentParticular.class);
                intent.putExtra("Username",((SubjectClassParticular)getActivity()).getUsername());
                intent.putExtra("Email",((SubjectClassParticular)getActivity()).getEmail());
                intent.putExtra("ClassCode",((SubjectClassParticular)getActivity()).getClassCode());
                intent.putExtra("ClassName",((SubjectClassParticular)getActivity()).getClassName());
                intent.putExtra("ClassTeacher",((SubjectClassParticular)getActivity()).getClassTeacher());
                intent.putExtra("AssignmentTitle",String.valueOf(AssignmentsArrayList.get(position).get("AssignmentTitle")));
                intent.putExtra("AssignmentDescription",String.valueOf(AssignmentsArrayList.get(position).get("AssignmentDescription")));
                intent.putExtra("AssignmentCode",String.valueOf(AssignmentsArrayList.get(position).get("AssignmentCode")));
                startActivity(intent);
            }
        });
    }

}