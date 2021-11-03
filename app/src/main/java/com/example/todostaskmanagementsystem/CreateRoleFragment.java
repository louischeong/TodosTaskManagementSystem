package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.Role;
import com.example.todostaskmanagementsystem.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
                EditText newName = getView().findViewById(R.id.txt_roleName);
                String roleName = newName.getText().toString();
                EditText newDesc = getView().findViewById(R.id.txt_description);
                String desc = newDesc.getText().toString();
                //EditText newAccess = getView().findViewById(R.id.recycle_emails);
                //String access = newAccess.getText().toString();

                if (TextUtils.isEmpty(roleName)) {
                    newName.setError("Role Name is required!");
                    return;
                }

                if (TextUtils.isEmpty(desc)) {
                    newDesc.setError("Description is required!");
                    return;
                }

                //if(TextUtils.isEmpty(access)){
                //    newAccess.setError("Please select which section can this role edit!");
                //    return;
                //}

                db.collection("Todolists").document(todolistID).collection("Data").document("Data").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int currRoleID = Integer.parseInt(documentSnapshot.get("currRoleID").toString()) + 1;
                        String strCurrRoleID = "R" + (currRoleID);
                        Role role = new Role(roleName, desc, strCurrRoleID);
                        db.collection("Todolists").document(todolistID).collection("Roles").document(strCurrRoleID).set(role);
                        db.collection("Todolists").document(todolistID).collection("Data").document("Data").update("currRoleID", currRoleID);
                        Toast.makeText(getActivity(), "Successfully added!", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    }
                });
            }
        });
        return view;
    }
}