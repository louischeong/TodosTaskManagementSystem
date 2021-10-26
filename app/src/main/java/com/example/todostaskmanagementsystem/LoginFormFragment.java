package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.todostaskmanagementsystem.model.Todolist;
import com.example.todostaskmanagementsystem.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class LoginFormFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LoginFormFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login_form, container, false);

        Button btn = view.findViewById(R.id.btn_login2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText = view.findViewById(R.id.txt_loginEmail);
                EditText editText2 = view.findViewById(R.id.txt_loginPassword);

                db.collection("Users").document(editText.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        user.getPassword(); // user password from database
                        // proceed to homepage if password correct
                        // else prompt login fail

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //prompt login fail
                    }
                });
            }
        });




        return view;
    }
}