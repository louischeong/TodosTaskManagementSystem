package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.todostaskmanagementsystem.model.Role;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ManageRoleFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Role> roles = new ArrayList();

    public ManageRoleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_role, container, false);
        Button btn = view.findViewById(R.id.button_create_role);
        RecyclerView recyclerView = view.findViewById(R.id.recycle_roles);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(getParentFragment()).navigate(ManageRoleFragmentDirections.actionManageRoleFragmentToCreateRoleFragment());
            }
        });
        return view;
    }
}