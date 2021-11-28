package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PasswordRecoveryFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnSendCode;
    private EditText editTextEmail;

    public PasswordRecoveryFragment() {
        // Required empty public constructor
    }

    public static PasswordRecoveryFragment newInstance(String param1, String param2) {
        PasswordRecoveryFragment fragment = new PasswordRecoveryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_recovery, container, false);

        btnSendCode = view.findViewById(R.id.btn_sendCode);
        editTextEmail = view.findViewById(R.id.txt_recoveryEmail);

        //String email = editTextEmail.getText().toString();
        String email = "louischeong10500@gmail.com";

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strRandomNum = getRandomNumberString();
                Map<String, Object> docMap = new HashMap<>();
                docMap.put("OTP", strRandomNum);
                docMap.put("email", email);

                db.collection("OTP").document(email).set(docMap);

                try {
                    GMailSender sender = new GMailSender("todostaskmanagement@gmail.com", "Todos@123");
                    sender.sendMail("Todos Task Management System: Password Recovery",
                            "Enter code below to reset your password.\nYour code: " + strRandomNum,
                            "todostaskmanagement@gmail.com",
                            email);
                } catch (Exception e) {
                    Log.d("MYDEBUG", e.getMessage(), e);
                }
            }
        });

        return view;
    }

    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        return String.format("%06d", number);
    }
}