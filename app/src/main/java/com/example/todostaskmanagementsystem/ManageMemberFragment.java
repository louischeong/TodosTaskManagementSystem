package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.todostaskmanagementsystem.adapter.MemberAdapter;
import com.example.todostaskmanagementsystem.model.ChangesLog;
import com.example.todostaskmanagementsystem.model.Member;
import com.example.todostaskmanagementsystem.model.Role;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageMemberFragment extends Fragment {

    private MemberAdapter memberAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String todolistID;
    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<Role> roles = new ArrayList<>();

    public ManageMemberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            todolistID = bundle.getString("todolistID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_member, container, false);
        List<String> roles = new ArrayList<>();
        Role role = new Role();
        roles.add(role.getRoleName());
        memberAdapter = new MemberAdapter(members, getContext(), roles);

        RecyclerView recyclerView = view.findViewById(R.id.memberRecycler);
        recyclerView.setAdapter(memberAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db.collection("Todolists").document(todolistID).collection("Members").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Member member = documentSnapshot.toObject(Member.class);
                    members.add(member);
                }
                memberAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}