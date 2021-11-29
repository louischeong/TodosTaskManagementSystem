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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    ImageView profPic;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //profPic = view.findViewById(R.id.edit_profPic);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Button btn = view.findViewById(R.id.btn_saveProf);
        Button btnChg = view.findViewById(R.id.btn_changePass);
        Button uploadPic = view.findViewById(R.id.btn_addProf);
        profPic = (ImageView) view.findViewById(R.id.edit_profPic);
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

                if(user.getProfilePic() != null && user.getProfilePic() != ""){
                    byte[] decodedString = Base64.decode(user.getProfilePic(), Base64.URL_SAFE);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    profPic.setImageBitmap(decodedByte);
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newName = view.findViewById(R.id.txt_editUserName);
                String name = newName.getText().toString();
                TextView newEmail = view.findViewById(R.id.txt_userEmail);
                String email = newEmail.getText().toString();
                EditText newContact = view.findViewById(R.id.txt_editPhone);
                String contact = newContact.getText().toString();

                ImageView pic = view.findViewById(R.id.edit_profPic);
                String imageFile = null;
                if(pic != null){
                    BitmapDrawable drawable = (BitmapDrawable) pic.getDrawable();
                    if(drawable != null) {
                        Bitmap bitmap = drawable.getBitmap();

                        //Resize
                        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                        resized.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
                        resized.recycle();
                        byte[] byteArray = bYtE.toByteArray();
                        imageFile = Base64.encodeToString(byteArray, Base64.URL_SAFE);
                    }
                }

                if(TextUtils.isEmpty(name)){
                    newName.setError("Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(contact)){
                    newContact.setError("Contact number is required!");
                    return;
                }

                createConfirmSaveDialog(name,contact,imageFile);

            }
        });

        btnChg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChangePasswordDialog();
            }
        });

        /*uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });*/

        // Open Gallery
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //open gallery
                //if(ActivityCompat.checkSelfPermission(getActivity(),
                //        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                //{
                //    System.out.println("#### No permission granted");
                //    requestPermissions(
                //            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                //            2000);
                //}
                //else {
                    startGallery();
                //}
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super method removed
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(bitmapImage != null) {
                    profPic.setImageBitmap(bitmapImage);
                }

            }
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            Uri imageUri = data.getData();
            profPic.setImageURI(imageUri);
        }
    }*/

    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
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
                        String pass = AESCrypt.decrypt(user.getPassword());
                        if(oldPass.equals(pass) && newPass.equals(conNewPass)){
                            DocumentReference docRef = db.collection("Users").document(email);
                            docRef.update("password", AESCrypt.encrypt(newPass));
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

    private void createConfirmSaveDialog(String name, String contact, String imageFile){
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
                        String pass = AESCrypt.decrypt(user.getPassword());
                        if(password.equals(pass)){
                            DocumentReference docRef = db.collection("Users").document(email);

                            user = new User(AESCrypt.encrypt(pass), name, contact, email, user.getToken(), imageFile);

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