package com.example.todostaskmanagementsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class EditProfileFragment extends Fragment {
    private String email = "WMY@GMAIL.COM";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EditProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Button btn = view.findViewById(R.id.btn_saveProf);
        SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        email = prefs.getString("pref_email",null);
        db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                EditText txtName = view.findViewById(R.id.txt_editUserName);
                TextView txtEmail = view.findViewById(R.id.txt_userEmail);
                EditText txtPhone = view.findViewById(R.id.txt_editPhone);
                EditText txtPass = view.findViewById(R.id.txt_editPass);
                txtName.setText(user.getName());
                txtEmail.setText(user.getEmail());
                txtPhone.setText(user.getContact());
                txtPass.setText(user.getPassword());
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newName = getView().findViewById(R.id.txt_editUserName);
                String name = newName.getText().toString();
                TextView newEmail = getView().findViewById(R.id.txt_userEmail);
                String email = newEmail.getText().toString();
                EditText newContact = getView().findViewById(R.id.txt_editPhone);
                String contact = newContact.getText().toString();
                EditText newPass = getView().findViewById(R.id.txt_editPass);
                String pass = newPass.getText().toString();
                EditText newConPass = getView().findViewById(R.id.txt_editConPass);
                String conPass = newConPass.getText().toString();

                if(TextUtils.isEmpty(name)){
                    newName.setError("Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(contact)){
                    newContact.setError("Contact number is required!");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    newPass.setError("Password is required!");
                    return;
                }
                if(TextUtils.isEmpty(conPass)){
                    newConPass.setError("Confirm Password is required!");
                    return;
                }
                if(pass.equals(conPass)){
                    DocumentReference docRef = db.collection("Users").document(email);
                    User user = new User(pass, name, contact, email);
                    docRef.set(user);
                    Toast.makeText(getActivity(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                }else{
                    Toast.makeText(getActivity(), "Password and confirm password is not match.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}