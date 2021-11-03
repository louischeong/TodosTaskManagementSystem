package com.example.todostaskmanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

                EditText editText = getView().findViewById(R.id.txt_loginEmail);
                String loginEmail = editText.getText().toString();
                EditText editText2 = getView().findViewById(R.id.txt_loginPassword);
                String loginPass = editText2.getText().toString();
                CheckBox cbRememberMe = getView().findViewById(R.id.checkBox_rememberMe);

                if (TextUtils.isEmpty(loginEmail)) {
                    editText.setError("Email is required!");
                    return;
                }

                if (TextUtils.isEmpty(loginPass)) {
                    editText2.setError("Password is required!");
                    return;
                }

                db.collection("Users").document(loginEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        String pass = user.getPassword(); // user password from database
                        if (pass.equals(loginPass)) {
                            SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("pref_email", loginEmail);
                            if (cbRememberMe.isChecked())
                                editor.putString("pref_rememberMe", "true");
                            editor.commit();
                            Intent myIntent = new Intent(getActivity(), MainActivity.class);
                            startActivity(myIntent);
                            getActivity().finish();
                        }
                        //proceed to homepage if password correct
                        // else prompt login fail

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to log in due to incorrect password.", Toast.LENGTH_SHORT).show();//prompt login fail

                    }
                });
            }
        });


        return view;
    }
}