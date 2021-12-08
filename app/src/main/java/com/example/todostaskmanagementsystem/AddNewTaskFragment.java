package com.example.todostaskmanagementsystem;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.ChangesLog;
import com.example.todostaskmanagementsystem.model.Reminder;
import com.example.todostaskmanagementsystem.model.TodoTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.Calendar;

public class AddNewTaskFragment extends Fragment {

    private String todolistID;
    private String sectionID;
    private String sectionName;
    private EditText dueDate, remindMeDays;
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

        TextView txtReminder = view.findViewById(R.id.reminderText);
        remindMeDays = view.findViewById(R.id.remindMe_days);

        TextView txtSectionName = view.findViewById(R.id.section_title);
        txtSectionName.setText(sectionName);

        CheckBox checkBox = view.findViewById(R.id.checkboxReminder);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    txtReminder.setVisibility(View.VISIBLE);
                    remindMeDays.setVisibility(View.VISIBLE);
                } else {
                    remindMeDays.setVisibility(View.INVISIBLE);
                    txtReminder.setVisibility(View.INVISIBLE);
                }
            }
        });


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
                dialog.getDatePicker().setMinDate(cal.getTimeInMillis() - 1000);
                dialog.setTitle("Select Date");

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
                EditText editTextName = view.findViewById(R.id.new_task_name);
                EditText editTextDesc = view.findViewById(R.id.new_task_desc);
                if (editTextName.getText().toString().isEmpty()) {
                    editTextName.setError("Name of the task is required.");
                    return;
                }
                if (editTextName.getText().toString().isEmpty()) {
                    editTextName.setError("Description of the task is required.");
                    return;
                }

                if (dueDate.getText().toString().equals("--/--/--")) {
                    dueDate.setError("Due Date of the task is required.");
                    return;
                }
                if (checkBox.isChecked()) {
                    if (remindMeDays.getText().toString() == null) {
                        remindMeDays.setError("Please set the reminder days.");
                        return;
                    }

                    int days = Integer.parseInt(remindMeDays.getText().toString());
                    if (!(days > 0 && days < 4)) {
                        remindMeDays.setError("The remind me days should be in between 1 to 3 only");
                        return;
                    }
                }

                db.collection("Todolists").document(todolistID)
                        .collection("Data").document("Data")
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //get current task id
                        int currTaskID = Integer.parseInt(documentSnapshot.get("currTaskID").toString()) + 1;
                        String taskID = "T" + currTaskID;
                        EditText editTextName = view.findViewById(R.id.new_task_name);
                        EditText editTextDesc = view.findViewById(R.id.new_task_desc);

                        //Check if the reminder for this task is on
                        if (checkBox.isChecked()) {
                            //get current reminder id
                            db.collection("Data").document("ReminderID")
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    //get current reminder id and increase by 1
                                    String id = documentSnapshot.get("currReminderID").toString();
                                    int currID = Integer.parseInt(id) + 1;
                                    int days = Integer.parseInt(remindMeDays.getText().toString());

                                    //create reminder object
                                    Reminder reminder =
                                            new Reminder(currID, todolistID, sectionID, taskID, days);

                                    //update current reminder id
                                    db.collection("Data").document("ReminderID")
                                            .update("currReminderID", currID);

                                    //save the reminder to the database
                                    db.collection("Reminders").document(String.valueOf(currID))
                                            .set(reminder);

                                    //create task object
                                    TodoTask todoTask = new TodoTask(taskID, editTextName.getText().toString(),
                                            editTextDesc.getText().toString(), dueDate.getText().toString(), currID);
                                    //save task to database
                                    db.collection("Todolists").document(todolistID)
                                            .collection("Sections").document(sectionID)
                                            .collection("TodoTasks").document(taskID).set(todoTask);
                                    //update current task id
                                    db.collection("Todolists").document(todolistID)
                                            .collection("Data").document("Data")
                                            .update("currTaskID", currTaskID);

                                    Toast.makeText(getActivity(), "Successfully Created Task", Toast.LENGTH_SHORT).show();
                                    addChangesLog(editTextName.getText().toString());
                                    requireActivity().onBackPressed();
                                }
                            });
                        } else {
                            //create task object
                            TodoTask todoTask = new TodoTask(taskID, editTextName.getText().toString(),
                                    editTextDesc.getText().toString(), dueDate.getText().toString());
                            //save task to database
                            db.collection("Todolists").document(todolistID)
                                    .collection("Sections").document(sectionID)
                                    .collection("TodoTasks").document(taskID).set(todoTask);
                            //update current task id
                            db.collection("Todolists").document(todolistID)
                                    .collection("Data").document("Data")
                                    .update("currTaskID", currTaskID);

                            Toast.makeText(getActivity(), "Successfully Created Task", Toast.LENGTH_SHORT).show();
                            addChangesLog(editTextName.getText().toString());
                            requireActivity().onBackPressed();
                        }


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

    public void addChangesLog(String editTextName) {
        //Create ChangesLog
        SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String userName = prefs.getString("pref_username", null);
        ChangesLog changesLog = new ChangesLog(Timestamp.now(), userName, "CreateTask", sectionName, editTextName);
        db.collection("Todolists").document(todolistID).collection("ChangesLog").add(changesLog);

    }

}