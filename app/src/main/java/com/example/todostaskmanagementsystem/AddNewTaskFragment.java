package com.example.todostaskmanagementsystem;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.ChangesLog;
import com.example.todostaskmanagementsystem.model.TodoTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class AddNewTaskFragment extends Fragment {

    private String todolistID;
    private String sectionID;
    private String sectionName;
    private EditText dueDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AddNewTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            todolistID = bundle.getString("todolistID");
            sectionID = bundle.getString("sectionID");
            sectionName = bundle.getString("sectionName");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_task, container, false);

        dueDate = view.findViewById(R.id.datepicker_duedate);
        dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String due = dayOfMonth + "/" + (month + 1) + "/" + year;
                dueDate.setText(due);
            }
        };

        Button btn = view.findViewById(R.id.btn_addNewTask);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Todolists").document(todolistID).collection("Data").document("Data").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int currTaskID = Integer.parseInt(documentSnapshot.get("currTaskID").toString()) + 1;
                        String taskID = "T" + currTaskID;
                        EditText editTextName = view.findViewById(R.id.new_task_name);
                        EditText editTextDesc = view.findViewById(R.id.new_task_desc);
                        TodoTask todoTask = new TodoTask(taskID, editTextName.getText().toString(), editTextDesc.getText().toString(), dueDate.getText().toString(), "1/1/1111");
                        db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("TodoTasks").document(taskID).set(todoTask);
                        db.collection("Todolists").document(todolistID).collection("Data").document("Data").update("currTaskID", currTaskID);
                        Toast.makeText(getContext(), "Successfully Created Task", Toast.LENGTH_SHORT).show();
                        SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
                        String userName = prefs.getString("pref_username", null);
                        ChangesLog changesLog = new ChangesLog(Timestamp.now(), userName, "CreateTask", sectionName, editTextName.getText().toString());
                        db.collection("Todolists").document(todolistID).collection("ChangesLog").add(changesLog);
                        requireActivity().onBackPressed();
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dueDate.setKeyListener(null);
    }
}