package com.example.todostaskmanagementsystem;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.adapter.MemberCheckAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnActionClicked;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Role;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.example.todostaskmanagementsystem.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CreateRoleFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String todolistID;


    public CreateRoleFragment() {
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
        View view = inflater.inflate(R.layout.fragment_create_role, container, false);
        Button btn = view.findViewById(R.id.btn_createRole);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createConfirmationDialog();
            }
        });
        return view;
    }


    private void createConfirmationDialog() {
        //Declare variables
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        TextView dialogConfirmMsg;
        Button dialogConfirmBtn, dialogCancelBtn;
        dialogBuilder = new AlertDialog.Builder(getContext());

        //inflate views to layout
        final View confirmView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        dialogConfirmMsg = confirmView.findViewById(R.id.confirm_msg);
        dialogConfirmBtn = confirmView.findViewById(R.id.btnConfirm);
        dialogCancelBtn = confirmView.findViewById(R.id.btnCancel);

        //change dialog message
        dialogConfirmMsg.setText("Are you sure you want to create the role?");

        //set view for dialog builder
        dialogBuilder.setView(confirmView);

        //create dialog
        dialog = dialogBuilder.create();

        //make dialog background transparent
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //set button listeners
        dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newName = getView().findViewById(R.id.txt_roleName);
                String roleName = newName.getText().toString();
                EditText newDesc = getView().findViewById(R.id.txt_description);
                String desc = newDesc.getText().toString();

                if (TextUtils.isEmpty(roleName)) {
                    newName.setError("Role Name is required!");
                    return;
                }

                if (TextUtils.isEmpty(desc)) {
                    newDesc.setError("Description is required!");
                    return;
                }
                db.collection("Todolists").document(todolistID).collection("Data").document("Data").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int currRoleID = Integer.parseInt(documentSnapshot.get("currRoleID").toString()) + 1;
                        String roleID = "R" + currRoleID;
                        Role role = new Role(roleID, roleName, desc);
                        db.collection("Todolists").document(todolistID).collection("Roles").document(roleID).set(role);
                        db.collection("Todolists").document(todolistID).collection("Data").document("Data").update("currRoleID", currRoleID);
                        Toast.makeText(getActivity(), "Successfully created Role!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        requireActivity().onBackPressed();
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