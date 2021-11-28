package com.example.todostaskmanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {
    private String email;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button btn = view.findViewById(R.id.btn_editProf);
        ImageView profPic = (ImageView) view.findViewById(R.id.profPic);
        SharedPreferences prefs = getActivity().getSharedPreferences("user_details",Context.MODE_PRIVATE);
        email = prefs.getString("pref_email",null);
        db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                TextView txtName = view.findViewById(R.id.txt_userName);
                TextView txtEmail = view.findViewById(R.id.txt_userEmail);
                TextView txtPhone = view.findViewById(R.id.txt_userPhone);
                TextView txtPass = view.findViewById(R.id.txt_userPassword);
                txtName.setText(user.getName());
                txtEmail.setText(user.getEmail());
                txtPhone.setText(user.getContact());
                txtPass.setText(user.getPassword());

                if(user.getProfilePic() != null && user.getProfilePic() != ""){
                    byte[] decodedString = Base64.decode(user.getProfilePic(), Base64.URL_SAFE);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    profPic.setImageBitmap(decodedByte);
                }

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(getParentFragment()).navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment());
            }
        });

        return view;
    }
}