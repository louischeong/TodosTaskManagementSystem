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

import com.example.todostaskmanagementsystem.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ResetPasswordFragment extends Fragment {
    private String email;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ResetPasswordFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            email = bundle.getString("email");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        Button savePass = view.findViewById(R.id.btn_saveNewPass);

        savePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetPass = getView().findViewById(R.id.txt_resetPass);
                String newPass = resetPass.getText().toString();
                EditText conResetPass = getView().findViewById(R.id.txt_resetConPass);
                String conPass = conResetPass.getText().toString();

                if(TextUtils.isEmpty(newPass)){
                    resetPass.setError("Password is required!");
                    return;
                }
                if(TextUtils.isEmpty(conPass)){
                    conResetPass.setError("Confirm Password is required!");
                    return;
                }

                db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>(){
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if(newPass.equals(conPass)){
                            DocumentReference docRef = db.collection("Users").document(email);
                            docRef.update(AESCrypt.encrypt("password"), newPass);
                            Toast.makeText(getActivity(), "Successfully updated password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }

}