package com.example.todostaskmanagementsystem;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.ChangesLog;
import com.example.todostaskmanagementsystem.model.Reminder;
import com.example.todostaskmanagementsystem.model.Role;
import com.example.todostaskmanagementsystem.model.Section;
import com.example.todostaskmanagementsystem.model.TodoTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;


public class TaskDetailsFragment extends Fragment {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String todolistID;
    private String sectionID;
    private String todoTasksID;
    private String sectionName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button saveBtn, deleteBtn;
    private TextView txtSectionName;
    private CheckBox checkBoxComplete;
    private EditText editTaskName, editDesc, editDueDate;
    private String userEmail, userName;
    private boolean isMarkAllowed, isEditAllowed;
    private int reminderID;
    private CheckBox checkBoxRemindMe;
    private EditText remindMeDays;

    public TaskDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            todolistID = bundle.getString("todolistID");
            sectionID = bundle.getString("sectionID");
            todoTasksID = bundle.getString("todoTasksID");
            sectionName = bundle.getString("sectionName");
        }
        SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        userEmail = prefs.getString("pref_email", null);
        userName = prefs.getString("pref_username", null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_details, container, false);

        txtSectionName = view.findViewById(R.id.task_section_name);
        checkBoxComplete = view.findViewById(R.id.chkBox_complete);
        editTaskName = view.findViewById(R.id.task_name);
        editDesc = view.findViewById(R.id.task_desc);
        editDueDate = view.findViewById(R.id.task_duedate);
        deleteBtn = view.findViewById(R.id.btn_delete_task);
        saveBtn = view.findViewById(R.id.saveChangesBtn);


        TextView txtReminder = view.findViewById(R.id.reminderText);
        remindMeDays = view.findViewById(R.id.remindMe_days);
        checkBoxRemindMe = view.findViewById(R.id.checkboxReminder);
        checkBoxRemindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_blue));
                saveBtn.setEnabled(true);
                saveBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.lightblue_bg));
                if (checkBoxRemindMe.isChecked()) {
                    txtReminder.setVisibility(View.VISIBLE);
                    remindMeDays.setVisibility(View.VISIBLE);
                } else {
                    remindMeDays.setVisibility(View.INVISIBLE);
                    txtReminder.setVisibility(View.INVISIBLE);
                }
            }
        });


        db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("TodoTasks").document(todoTasksID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                TodoTask todoTask = documentSnapshot.toObject(TodoTask.class);
                reminderID = todoTask.getReminder();
                if (reminderID != 0) {
                    db.collection("Reminders").document(String.valueOf(reminderID)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Reminder reminder = documentSnapshot.toObject(Reminder.class);
                            remindMeDays.setText(String.valueOf(reminder.getDaysRepeat()));
                            checkBoxRemindMe.setChecked(true);
                            txtReminder.setVisibility(View.VISIBLE);
                            remindMeDays.setVisibility(View.VISIBLE);
                            remindMeDays.addTextChangedListener(new TextChanged());
                        }
                    });
                }

                checkBoxComplete.setChecked(todoTask.getComplete());
                txtSectionName.setText(sectionName);
                editTaskName.setText(todoTask.getName());
                editDesc.setText(todoTask.getDesc());
                editDueDate.setText(todoTask.getDueDate());
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                editTaskName.addTextChangedListener(new TextChanged());
                editDesc.addTextChangedListener(new TextChanged());
                editDueDate.addTextChangedListener(new TextChanged());


            }
        });

        db.collection("Todolists").document(todolistID).collection("Roles").whereArrayContains("members", userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String roleName = "";
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Role role = documentSnapshot.toObject(Role.class);
                    roleName = role.getRoleName();
                }
                String finalRoleName = roleName;
                db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Section section = documentSnapshot.toObject(Section.class);
                        isMarkAllowed = section.getAllowedMark().contains(finalRoleName);
                        isEditAllowed = section.getAllowedEdit().contains(finalRoleName);

                        if (!isEditAllowed) {

                            disableEditFields();

                            saveBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getActivity(), "You do not have permission to edit tasks in this section.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            deleteBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getActivity(), "You do not have permission to delete tasks in this section.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            editDueDate.setOnClickListener(new View.OnClickListener() {
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
                                    editDueDate.setText(due);
                                }
                            };

                            saveBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (checkBoxRemindMe.isChecked()) {
                                        EditText remindMeDays = view.findViewById(R.id.remindMe_days);
                                        if (remindMeDays.getText().toString() == null) {
                                            Toast.makeText(getActivity(), "Please set the reminder days.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        int days = Integer.parseInt(remindMeDays.getText().toString());
                                        if (!(days > 0 && days < 4)) {
                                            Toast.makeText(getActivity(), "The remind me days should be in between 1 to 3 only", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        Reminder reminder = new Reminder(reminderID, todolistID, sectionID, todoTasksID, days);
                                        db.collection("Reminders").document(String.valueOf(reminderID)).set(reminder);
                                    } else {
                                        db.collection("Reminders").document(String.valueOf(reminderID)).delete();
                                    }
                                    TodoTask todoTask = new TodoTask(todoTasksID, editTaskName.getText().toString(), editDesc.getText().toString(), editDueDate.getText().toString(), checkBoxComplete.isChecked(), reminderID);
                                    db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("TodoTasks").document(todoTasksID).set(todoTask);
                                    Toast.makeText(getActivity(), "Task Updated Successfully", Toast.LENGTH_SHORT).show();
                                    ChangesLog changesLog = new ChangesLog(Timestamp.now(), userName, "EditTask", sectionName, editTaskName.getText().toString());
                                    db.collection("Todolists").document(todolistID).collection("ChangesLog").add(changesLog);
                                    getActivity().onBackPressed();
                                }
                            });
                            deleteBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    createConfirmationDialog();
                                }
                            });


                        }

                        if (isMarkAllowed) {
                            checkBoxComplete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    createMarkConfirmationDialog();
                                }
                            });
                        } else {
                            checkBoxComplete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getActivity(), "You do not have permission to mark task as completed in this section.", Toast.LENGTH_SHORT).show();
                                    CheckBox chkBox = view.findViewById(R.id.chkBox_complete);
                                    chkBox.setChecked(!chkBox.isChecked());
                                }
                            });
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
        editDueDate.setKeyListener(null);
    }

    private class TextChanged implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            saveBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_blue));
            saveBtn.setEnabled(true);
            saveBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.lightblue_bg));
        }
    }

    private void createConfirmationDialog() {
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        TextView dialogConfirmMsg;
        Button dialogConfirmBtn, dialogCancelBtn;
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View confirmView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        dialogConfirmMsg = confirmView.findViewById(R.id.confirm_msg);
        dialogConfirmBtn = confirmView.findViewById(R.id.btnConfirm);
        dialogCancelBtn = confirmView.findViewById(R.id.btnCancel);
        dialogConfirmMsg.setText("Are you sure you want to delete this task?");
        dialogBuilder.setView(confirmView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("TodoTasks").document(todoTasksID).delete();
                dialog.dismiss();
                ChangesLog changesLog = new ChangesLog(Timestamp.now(), userName, "DeleteTask", sectionName, editTaskName.getText().toString());
                db.collection("Todolists").document(todolistID).collection("ChangesLog").add(changesLog);
                requireActivity().onBackPressed();
            }
        });

        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
    }

    private void createMarkConfirmationDialog() {
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        TextView dialogConfirmMsg;
        Button dialogConfirmBtn, dialogCancelBtn;
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View confirmView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        dialogConfirmMsg = confirmView.findViewById(R.id.confirm_msg);
        dialogConfirmBtn = confirmView.findViewById(R.id.btnConfirm);
        dialogCancelBtn = confirmView.findViewById(R.id.btnCancel);
        dialogConfirmMsg.setText("Are you sure you want to mark this task as complete?");
        dialogBuilder.setView(confirmView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                    }
                });

                db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("TodoTasks").document(todoTasksID).update("complete", checkBoxComplete.isChecked());
                String isMark = checkBoxComplete.isChecked() ? "MarkTask" : "UnmarkTask";
                ChangesLog changesLog = new ChangesLog(Timestamp.now(), userName, isMark, editTaskName.getText().toString(), sectionName);
                db.collection("Todolists").document(todolistID).collection("ChangesLog").add(changesLog);
                dialog.dismiss();
            }
        });

        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxComplete.setChecked(!checkBoxComplete.isChecked());
                dialog.dismiss();
            }
        });
    }

    private void disableEditFields() {
        txtSectionName.setInputType(InputType.TYPE_NULL);

        editTaskName.setInputType(InputType.TYPE_NULL);
        editDesc.setInputType(InputType.TYPE_NULL);
        editDueDate.setOnClickListener(null);
    }
}