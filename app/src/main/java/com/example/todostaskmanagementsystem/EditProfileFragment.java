package com.example.todostaskmanagementsystem;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {
    private String email = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private View view;
    private ImageView imgViewProfPic;
    private int SELECT_PICTURE = 200;
    private Uri returnUri;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Button btn = view.findViewById(R.id.btn_saveProf);
        Button btnChg = view.findViewById(R.id.btn_changePass);

        imgViewProfPic = view.findViewById(R.id.edit_profPic);
        SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        email = prefs.getString("pref_email", null);

        //loadData
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

                StorageReference imageRef = storageRef.child("profpic/" + email);

                final long ONE_MEGABYTE = 1024 * 1024 * 5;
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imgViewProfPic.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Toast.makeText(getActivity(), "The file is too big, please reduce the size.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //save changes
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newName = view.findViewById(R.id.txt_editUserName);
                String name = newName.getText().toString().trim();
                TextView newEmail = view.findViewById(R.id.txt_userEmail);
                String email = newEmail.getText().toString().trim();
                EditText newContact = view.findViewById(R.id.txt_editPhone);
                String contact = newContact.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    newName.setError("Name is required!");
                    return;
                }
                if (TextUtils.isEmpty(contact)) {
                    newContact.setError("Contact number is required!");
                    return;
                }

                createConfirmSaveDialog(name, contact);

            }
        });

        //create password dialog password changes
        btnChg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChangePasswordDialog();
            }
        });

        // Open Gallery
        imgViewProfPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super method removed
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                returnUri = data.getData();
                if (returnUri != null) {
                    imgViewProfPic.setImageURI(returnUri);
                }
            }
        }
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }


    private void createChangePasswordDialog() {
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
                String oldPass = dialogOldPass.getText().toString().trim();
                String newPass = dialogNewPass.getText().toString().trim();
                String conNewPass = dialogConNewPass.getText().toString().trim();

                db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        String pass = AESCrypt.decrypt(user.getPassword());
                        if (oldPass.equals(pass) && newPass.equals(conNewPass)) {
                            DocumentReference docRef = db.collection("Users").document(email);
                            docRef.update("password", AESCrypt.encrypt(newPass));
                            Toast.makeText(getActivity(), "Successfully updated password!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
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

    private void createConfirmSaveDialog(String name, String contact) {
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
                String password = dialogPassword.getText().toString().trim();

                db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        String pass = AESCrypt.decrypt(user.getPassword());
                        if (password.equals(pass)) {
                            DocumentReference docRef = db.collection("Users").document(email);

                            user = new User(AESCrypt.encrypt(pass), name, contact, email, user.getToken());

                            docRef.set(user);
                            uploadToFirebaseStorage();
                            Toast.makeText(getActivity(), "Successfully updated profile!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            requireActivity().onBackPressed();
                        } else {
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

    public void uploadToFirebaseStorage() {
        StorageReference imageRef = storageRef.child("profpic/" + email);
        UploadTask uploadTask = imageRef.putFile(returnUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("MYDEBUG", "Upload failed" + e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("MYDEBUG", "Upload successfully");
            }
        });
    }

}