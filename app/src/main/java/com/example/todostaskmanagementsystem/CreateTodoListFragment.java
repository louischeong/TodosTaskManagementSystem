package com.example.todostaskmanagementsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.adapter.AddMemberAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.ChangesLog;
import com.example.todostaskmanagementsystem.model.Member;
import com.example.todostaskmanagementsystem.model.Notification;
import com.example.todostaskmanagementsystem.model.Role;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.example.todostaskmanagementsystem.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateTodoListFragment extends Fragment implements View.OnClickListener {

    private List<String> memberEmails = new ArrayList<>();
    private AddMemberAdapter addMemberAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userEmail, userName;
    private EditText editTextTitle, editTextDesc;
    private View view;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public CreateTodoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        userEmail = prefs.getString("pref_email", null);
        userName = prefs.getString("pref_username", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_todo_list, container, false);
        editTextTitle = view.findViewById(R.id.todolist_title);
        editTextDesc = view.findViewById(R.id.todolist_desc);
        Button btn = view.findViewById(R.id.btn_addMemberEmail);
        btn.setOnClickListener(this);
        Button btn2 = view.findViewById(R.id.btn_createNewTodolist);
        btn2.setOnClickListener(this);

        addMemberAdapter = new AddMemberAdapter(memberEmails);
        addMemberAdapter.setOnItemClickedListener(new OnItemClicked() {
            @Override
            public void onItemClicked(int position) {
                memberEmails.remove(position);
                updateRecycleView();
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycle_emails);
        recyclerView.setAdapter(addMemberAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addMemberEmail:
                createAddMemberEmailDialog();
                break;
            case R.id.btn_createNewTodolist:
                String todolistTitle = editTextTitle.getText().toString().trim();
                String todolistDesc = editTextDesc.getText().toString().trim();
                if (TextUtils.isEmpty(todolistTitle)) {
                    editTextTitle.setError("Title of a to-do list is required!");
                    return;
                }

                if (TextUtils.isEmpty(todolistDesc)) {
                    editTextDesc.setError("Description is required!");
                    return;
                }

                String ownerName = userName;

                DocumentReference docRef = db.collection("Data").document("todolistID");
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //get current Todolist ID from database
                        int currTodolistID = Integer.parseInt(documentSnapshot.get("currTodolistID").toString());
                        currTodolistID += 1;
                        String strCurrTodolistID = Integer.toString(currTodolistID);

                        //add creator's email into the todolist
                        List<String> emails = new ArrayList<>();
                        emails.add(userEmail);

                        //creating todolist object and save to database
                        Todolist todolist = new Todolist(strCurrTodolistID, todolistTitle, todolistDesc, userEmail, emails);
                        db.collection("Todolists").document(Integer.toString(currTodolistID)).set(todolist);

                        //create the Default Role of this todolist and save it to the database
                        Role role = new Role("R1", "Default", "This is the dafault role of the todolist", emails);
                        db.collection("Todolists").document(Integer.toString(currTodolistID))
                                .collection("Roles").document("R1").set(role);

                        //create a data collection and save it to the todolist
                        Map<String, Object> docData = new HashMap<>();
                        docData.put("currSectionID", 0);
                        docData.put("currTaskID", 0);
                        docData.put("currRoleID", 1);
                        db.collection("Todolists").document(strCurrTodolistID)
                                .collection("Data").document("Data").set(docData);

                        //update the current todolist ID
                        db.collection("Data").document("todolistID").update("currTodolistID", currTodolistID);

                        //create Notification for invited members and save to database
                        Notification notification = new Notification(strCurrTodolistID, todolistTitle, ownerName, Timestamp.now(), memberEmails);
                        db.collection("Notifications").document(strCurrTodolistID).set(notification);

                        //update ChangesLog of the todolist
                        ChangesLog changesLog = new ChangesLog(Timestamp.now(), userName, "CreateTodolist", todolistTitle);
                        db.collection("Todolists").document(Integer.toString(currTodolistID))
                                .collection("ChangesLog").add(changesLog);

                        Toast.makeText(getActivity(), "Todolist Created Successfully.", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    }
                });

                break;
            default:
                break;
        }
    }

    private void updateRecycleView() {
        TextView txt = view.findViewById(R.id.empty_hint);
        if (memberEmails.isEmpty()) {
            txt.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.INVISIBLE);
        }
        addMemberAdapter.notifyDataSetChanged();
    }

    private void createAddMemberEmailDialog() {

        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        EditText dialogEmail;
        TextView dialogMsg;
        Button dialogAddBtn, dialogCancelBtn;

        dialogBuilder = new AlertDialog.Builder(getContext());
        final View addEmailView = getLayoutInflater().inflate(R.layout.dialog_single_input, null);
        dialogEmail = addEmailView.findViewById(R.id.dialog_input_field);
        dialogAddBtn = addEmailView.findViewById(R.id.dialog_btnConfirm);
        dialogCancelBtn = addEmailView.findViewById(R.id.dialog_btnCancel);
        dialogMsg = addEmailView.findViewById(R.id.dialog_msg);
        dialogMsg.setText("Enter member email to invite:");

        dialogBuilder.setView(addEmailView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        dialogAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validation email
                String email = dialogEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email) || !email.matches(emailPattern)){
                    dialogEmail.setError("Please enter an valid email.");
                    return;
                }
                if (!memberEmails.contains(email)) {
                    memberEmails.add(email);
                    updateRecycleView();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Duplicated email are not allowed.", Toast.LENGTH_SHORT).show();
                    dialogEmail.setText("");
                }
            }
        });

        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}