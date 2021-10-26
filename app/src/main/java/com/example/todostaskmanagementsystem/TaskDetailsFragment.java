package com.example.todostaskmanagementsystem;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.model.Section;
import com.example.todostaskmanagementsystem.model.TodoTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class TaskDetailsFragment extends Fragment {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String todolistID;
    private String sectionID;
    private String todoTasksID;
    private String sectionName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView txtSectionName;
    private CheckBox checkBox;
    private EditText editTaskName, editDesc, editDueDate;

    public TaskDetailsFragment() {
        // Required empty public constructor
    }

    public static TaskDetailsFragment newInstance(String param1, String param2) {
        TaskDetailsFragment fragment = new TaskDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_details, container, false);

        txtSectionName = view.findViewById(R.id.task_section_name);
        checkBox = view.findViewById(R.id.chkBox_complete);
        editTaskName = view.findViewById(R.id.task_name);
        editDesc = view.findViewById(R.id.task_desc);
        editDueDate = view.findViewById(R.id.task_duedate);

        db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("TodoTasks").document(todoTasksID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                TodoTask todoTask = documentSnapshot.toObject(TodoTask.class);

                checkBox.setChecked(todoTask.getComplete());
                txtSectionName.setText(sectionName);
                editTaskName.setText(todoTask.getName());
                editDesc.setText(todoTask.getDesc());
                editDueDate.setText(todoTask.getDueDate());

            }
        });


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
                String due = dayOfMonth + "/" + month + "/" + year;
                editDueDate.setText(due);
            }
        };

        Button btn = view.findViewById(R.id.saveChangesBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoTask todoTask = new TodoTask(todoTasksID, editTaskName.getText().toString(), editDesc.getText().toString(), editDueDate.getText().toString(), checkBox.isChecked(), "");
                db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("TodoTasks").document(todoTasksID).set(todoTask);
                Toast.makeText(getActivity(),"Task Updated Successfully",Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });

        return view;
    }
}