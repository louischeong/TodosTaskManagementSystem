package com.example.todostaskmanagementsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.adapter.RoleAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.ChangesLog;
import com.example.todostaskmanagementsystem.model.Role;
import com.example.todostaskmanagementsystem.model.Section;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                createEditRoleDialog(position);
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

    private void createEditRoleDialog(int position) {
        //Declare variables
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        EditText dialogEditName;
        EditText dialogEditDesc;
        Button dialogConfirmBtn, dialogCancelBtn;
        dialogBuilder = new AlertDialog.Builder(getContext());

        //inflate views to layout
        final View editView = getLayoutInflater().inflate(R.layout.dialog_edit_role, null);
        dialogEditName = editView.findViewById(R.id.dialog_input_title);
        dialogEditDesc = editView.findViewById(R.id.dialog_input_desc);
        dialogConfirmBtn = editView.findViewById(R.id.dialog_btnConfirm);
        dialogCancelBtn = editView.findViewById(R.id.dialog_btnCancel);

        dialogEditName.setText(roles.get(position).getRoleName());
        dialogEditDesc.setText(roles.get(position).getDesc());

        //set view for dialog builder
        dialogBuilder.setView(editView);
        //create dialog
        dialog = dialogBuilder.create();
        //make dialog background transparent
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //set button listeners
        dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editName = dialogEditName.getText().toString();
                String editDesc = dialogEditDesc.getText().toString();
                Map<String, Object> docData = new HashMap<>();
                docData.put("roleName", editName);
                docData.put("desc", editDesc);

                db.collection("Todolists").document(todolistID).collection("Roles").document(roles.get(position).getId()).update(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Update successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadData(getView());
                    }
                });
            }
        });

        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //display dialog
        dialog.show();
    }
}