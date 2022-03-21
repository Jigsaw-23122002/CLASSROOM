package com.example.test_01;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnnounceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnounceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AnnounceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnnounceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnnounceFragment newInstance(String param1, String param2) {
        AnnounceFragment fragment = new AnnounceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    TextView Header;
    TextView Footer;
    ListView OthersList;
    LinearLayout IssColor1;
    ImageButton AnnouncementLink;

    FirebaseFirestore UsersDatabase = FirebaseFirestore.getInstance();

    AlertDialog alertDialog;
    AlertDialog.Builder builder;

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
        return inflater.inflate(R.layout.fragment_announce, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(view != null) {

            Header = (TextView) view.findViewById(R.id.Header);
            Footer = (TextView) view.findViewById(R.id.Footer);
            OthersList = (ListView) view.findViewById(R.id.OthersList);
            IssColor1 = (LinearLayout) view.findViewById(R.id.IssColor1);
            AnnouncementLink = (ImageButton) view.findViewById(R.id.AnnouncementLink);

            Header.setText(((SubjectClassParticular) getActivity()).getClassName());
            if(((SubjectClassParticular)getActivity()).getClassTeacher()!=null){
                Footer.setText(((SubjectClassParticular) getActivity()).getClassTeacher());
            }
            Header.setTextColor(Color.parseColor("white"));
            Footer.setTextColor(Color.parseColor("white"));

            if (((SubjectClassParticular) getActivity()).getColor().equals("one.jpg")) {
                IssColor1.setBackgroundResource(R.drawable.one);
            } else if (((SubjectClassParticular) getActivity()).getColor().equals("two.jpg")) {
                IssColor1.setBackgroundResource(R.drawable.two);
            } else if (((SubjectClassParticular) getActivity()).getColor().equals("three.jpg")) {
                IssColor1.setBackgroundResource(R.drawable.three);
            } else if (((SubjectClassParticular) getActivity()).getColor().equals("four.jpg")) {
                IssColor1.setBackgroundResource(R.drawable.four);
            } else if (((SubjectClassParticular) getActivity()).getColor().equals("five.jpg")) {
                IssColor1.setBackgroundResource(R.drawable.five);
            } else {
                IssColor1.setBackgroundResource(R.drawable.six);
            }

            UsersDatabase.collection("ClassesCreated").whereEqualTo("ClassCode", ((SubjectClassParticular) getActivity()).getClassCode())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot documents : task.getResult()) {
                        ArrayList<Map> Announcements = (ArrayList<Map>) documents.getData().get("AnnouncementsOfClass");
                        AnnouncementsArrayAdapter announcementsArrayAdapter = new AnnouncementsArrayAdapter(getContext(), Announcements);
                        OthersList.setAdapter(announcementsArrayAdapter);
                    }
                }
            });

            AnnouncementLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),AnnouncementPage.class);
                    intent.putExtra("Username",((SubjectClassParticular)getActivity()).getUsername());
                    intent.putExtra("ClassCode",((SubjectClassParticular)getActivity()).getClassCode());
                    intent.putExtra("ClassName",((SubjectClassParticular)getActivity()).getClassName());
                    intent.putExtra("ClassTeacher",((SubjectClassParticular)getActivity()).getClassTeacher());
                    startActivity(intent);
                }
            });
        }
    }
}