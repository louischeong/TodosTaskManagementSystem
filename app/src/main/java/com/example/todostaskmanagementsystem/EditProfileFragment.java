package com.example.todostaskmanagementsystem;

import android.app.AlertDialog;
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

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {
    private String email = "";
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
        Button btnChg = view.findViewById(R.id.btn_changePass);
        SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        email = prefs.getString("pref_email",null);
        db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                EditText txtName = view.findViewById(R.id.txt_editUserName);
                TextView txtEmail = view.findViewById(R.id.txt_userEmail);
                EditText txtPhone = view.findViewById(R.id.txt_editPhone);
                txtName.setText(user.getName());
                txtEmail.setText(user.getEmail());
                txtPhone.setText(user.getContact());
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

                if(TextUtils.isEmpty(name)){
                    newName.setError("Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(contact)){
                    newContact.setError("Contact number is required!");
                    return;
                }
                createConfirmSaveDialog(name,contact);

            }
        });

        btnChg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChangePasswordDialog();
            }
        });

        return view;
    }

    private void createChangePasswordDialog(){
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        EditText dialogOldPass;
        EditText dialogNewPass;
        EditText dialogConNewPass;
        Button dialogConfirmBtn, dialogCancelBtn;
        dialogBuilder = new AlertDialog.Builder(getContext());

        final View editView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        dialogOldPass = editView.findViewById(R.id.dialog_input_oldPass);
        dialogNewPass = editView.findViewById(R.id.dialog_input_newPass);
        dialogConNewPass = editView.findViewById(R.id.dialog_input_conNewPass);
        dialogConfirmBtn = editView.findViewById(R.id.dialog_btnConfirm);
        dialogCancelBtn = editView.findViewById(R.id.dialog_btnCancel);

        //set view for dialog builder
        dialogBuilder.setView(editView);
        //create dialog
        dialog = dialogBuilder.create();
        //make dialog background transparent
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //set button listener
        dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPass = dialogOldPass.getText().toString();
                String newPass = dialogNewPass.getText().toString();
                String conNewPass = dialogConNewPass.getText().toString();

                db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        String pass = user.getPassword();
                        if(oldPass.equals(pass) && newPass.equals(conNewPass)){
                            DocumentReference docRef = db.collection("Users").document(email);
                            docRef.update("password", newPass);
                            Toast.makeText(getActivity(), "Successfully updated password!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else{
                            Toast.makeText(getActivity(), "Incorrect password!", Toast.LENGTH_SHORT).show();
                        }
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

    private void createConfirmSaveDialog(String name, String contact){
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        EditText dialogPassword;
        Button dialogConfirmBtn, dialogCancelBtn;
        dialogBuilder = new AlertDialog.Builder(getContext());

        final View editView = getLayoutInflater().inflate(R.layout.dialog_save_profile, null);
        dialogPassword = editView.findViewById(R.id.dialog_input_password);
        dialogConfirmBtn = editView.findViewById(R.id.dialog_btnPassConfirm);
        dialogCancelBtn = editView.findViewById(R.id.dialog_btnCancel);

        //set view for dialog builder
        dialogBuilder.setView(editView);
        //create dialog
        dialog = dialogBuilder.create();
        //make dialog background transparent
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //set button listener
        dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = dialogPassword.getText().toString();

                db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        String pass = user.getPassword();
                        if(password.equals(pass)){
                            DocumentReference docRef = db.collection("Users").document(email);
                            user = new User(pass, name, contact, email, user.getToken());
                            docRef.set(user);
                            Toast.makeText(getActivity(), "Successfully updated profile!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            requireActivity().onBackPressed();
                        } else{
                            Toast.makeText(getActivity(), "Incorrect password!", Toast.LENGTH_SHORT).show();
                        }
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