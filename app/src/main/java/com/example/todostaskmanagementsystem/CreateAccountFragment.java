package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateAccountFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private View view;

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
        view = inflater.inflate(R.layout.fragment_create_account, container, false);
        Button btn = view.findViewById(R.id.btn_register);
        ImageButton back = view.findViewById(R.id.btn_back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newName = view.findViewById(R.id.txt_name);
                String name = newName.getText().toString().trim();
                EditText newEmail = view.findViewById(R.id.txt_email);
                String email = newEmail.getText().toString().trim();
                EditText newContact = view.findViewById(R.id.txt_phone);
                String contact = newContact.getText().toString().trim();
                EditText newPass = view.findViewById(R.id.txt_pass);
                String pass = newPass.getText().toString().trim();
                EditText newConPass = view.findViewById(R.id.txt_confirmPass);
                String conPass = newConPass.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    newName.setError("Name is required!");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    newEmail.setError("Email is required!");
                }
                if (TextUtils.isEmpty(contact)) {
                    newContact.setError("Contact number is required!");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    newPass.setError("Password is required!");
                    return;
                }
                if (TextUtils.isEmpty(conPass)) {
                    newConPass.setError("Confirm Password is required!");
                    return;
                }
                db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            //prompt email exists
                            newEmail.setError("This email is existed. Please try a new email.");
                        }else{
                            if (email.matches(emailPattern) && android.util.Patterns.PHONE.matcher(contact).matches()) {
                                if (pass.equals(conPass)) {
                                    DocumentReference docRef = db.collection("Users").document(email);
                                    User user = new User(AESCrypt.encrypt(pass), name, contact, email, "");
                                    docRef.set(user);
                                    Toast.makeText(getActivity(), "Successfully registered!", Toast.LENGTH_SHORT).show();
                                    NavHostFragment.findNavController(getParentFragment()).navigate(CreateAccountFragmentDirections.actionCreateAccountToStartPageFragment());
                                } else {
                                    Toast.makeText(getActivity(), "Password and confirm password is not match.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Please make sure that the email and contact number are correct.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(getParentFragment()).navigate(CreateAccountFragmentDirections.actionCreateAccountToStartPageFragment());
            }
        });
        return view;
    }
}