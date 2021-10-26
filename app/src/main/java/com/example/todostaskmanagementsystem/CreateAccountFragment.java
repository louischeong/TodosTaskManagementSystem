package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateAccountFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CreateAccountFragment() {
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
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        Button btn = view.findViewById(R.id.btn_register);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newName = view.findViewById(R.id.txt_name);
                String name = newName.getText().toString();
                EditText newEmail = view.findViewById(R.id.txt_email);
                String email = newEmail.getText().toString();
                EditText newContact = view.findViewById(R.id.txt_phone);
                String contact = newContact.getText().toString();
                EditText newPass = view.findViewById(R.id.txt_pass);
                String pass = newPass.getText().toString();
                EditText newConPass = view.findViewById(R.id.txt_confirmPass);
                String conPass = newConPass.getText().toString();

                if(pass.equals(conPass)){
                    DocumentReference docRef = db.collection("Users").document(email);
                    User user = new User(pass, name, contact, email);
                    docRef.set(user);
                    Toast.makeText(getActivity(), "Successfully registered!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }else{
                    Toast.makeText(getActivity(), "Password and confirm password is not match.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}