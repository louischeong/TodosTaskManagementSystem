package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.todostaskmanagementsystem.adapter.RoleAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Role;
import com.example.todostaskmanagementsystem.model.Section;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManageRoleFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Role> roles = new ArrayList();
    private RoleAdapter roleAdapter;
    private String todolistID;

    public ManageRoleFragment() {
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
        View view = inflater.inflate(R.layout.fragment_manage_role, container, false);
        Button btn = view.findViewById(R.id.button_create_role);
        RecyclerView recyclerView = view.findViewById(R.id.recycle_roles);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("todolistID", todolistID);
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_manageRoleFragment_to_createRoleFragment, bundle);
            }
        });
        roleAdapter = new RoleAdapter(roles);
        roleAdapter.setOnItemClickedListener(new OnItemClicked() {
            @Override
            public void onItemClicked(int position) {
                String roleID = roles.get(position).getId();
                Bundle bundle = new Bundle();
                bundle.putString("todolistID", todolistID);
                bundle.putString("roleID", roleID);
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_manageRoleFragment_to_editRoleFragment, bundle);
            }
        });
        recyclerView.setAdapter(roleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadData(view);
        return view;
    }

    private void loadData(View view) {
        roles.clear();
        db.collection("Todolists").document(todolistID).collection("Roles").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Role role = documentSnapshot.toObject(Role.class);
                    roles.add(role);
                }
                updateRecycleView();
            }
        });
    }

    private void updateRecycleView() {

        TextView txt = getView().findViewById(R.id.empty_hint);
        if (roles.isEmpty()) {
            txt.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.INVISIBLE);
        }
        roleAdapter.notifyDataSetChanged();
    }
}