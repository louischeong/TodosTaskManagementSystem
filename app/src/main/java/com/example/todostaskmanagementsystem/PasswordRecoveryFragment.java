package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PasswordRecoveryFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnSendCode;
    private EditText editTextEmail;
    private Button btnContinue;
    private EditText editTextOTP;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public PasswordRecoveryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_password_recovery, container, false);

        btnSendCode = view.findViewById(R.id.btn_sendCode);
        editTextEmail = view.findViewById(R.id.txt_recoveryEmail);
        btnContinue = view.findViewById(R.id.btn_continue);
        editTextOTP = view.findViewById(R.id.txt_OTP);
        ImageButton back = view.findViewById(R.id.btn_back);

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //TODO Validate email in the email TextField
                String email = editTextEmail.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    editTextEmail.setError("Email is required!");
                }

                if(email.matches(emailPattern)){
                    db.collection("Users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                new Thread() {
                                    public void run() {
                                        try {
                                            String strRandomNum = getRandomNumberString();
                                            GMailSender sender = new GMailSender("todostaskmanagement@gmail.com", "Todos@123");
                                            sender.sendMail("Todos Task Management System: Password Recovery",
                                                    "Enter code below to reset your password.\nYour code: " + strRandomNum,
                                                    "TodosTaskManagementSupport",
                                                    email);

                                            Map<String, Object> docMap = new HashMap<>();
                                            docMap.put("OTP", strRandomNum);

                                            db.collection("OTP").document(email).set(docMap);
                                            Toast.makeText(getActivity(), "An email with OTP code has been sent to " + email, Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Log.d("MYDEBUG", e.getMessage(), e);
                                        }
                                    }
                                }.start();
                            } else {
                                Toast.makeText(getActivity(), "The email is not exist. Please create an account.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "Please make sure that the email is in correct format.", Toast.LENGTH_SHORT).show();
                }




            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = editTextOTP.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    editTextEmail.setError("Email is required!");
                    return;
                }

                if(TextUtils.isEmpty(otp)){
                    editTextOTP.setError("OTP is required!");
                    return;
                }

                db.collection("OTP").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String OTP = (String) documentSnapshot.get("OTP");

                            if(TextUtils.isEmpty(OTP)){
                                editTextOTP.setError("OTP is required!");
                                return;
                            }
                            if (OTP.equals(editTextOTP.getText().toString().trim())) {
                                Bundle bundle = new Bundle();
                                bundle.putString("email", email);
                                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_passwordRecoveryFragment_to_resetPasswordFragment, bundle);
                                db.collection("OTP").document(email).delete();
                            } else {
                                Toast.makeText(getActivity(), "Wrong OTP, please try again or resend the code.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Wrong OTP, please try again or resend the code.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(getParentFragment()).navigate(PasswordRecoveryFragmentDirections.actionPasswordRecoveryFragmentToLoginFormFragment());
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