package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.todostaskmanagementsystem.adapter.SectionAdapter;
import com.example.todostaskmanagementsystem.adapter.TaskAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Section;
import com.example.todostaskmanagementsystem.model.TodoTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SectionDetailsFragment extends Fragment {

    private ArrayList<TodoTask> todoTasks = new ArrayList();
    private String todolistID;
    private String sectionID;
    private String sectionName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TaskAdapter taskAdapter;

    public SectionDetailsFragment() {
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

        View view = inflater.inflate(R.layout.fragment_section_details, container, false);

        Button btnAdd = view.findViewById(R.id.btn_addNewTask);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("todolistID", todolistID);
                bundle.putString("sectionID", sectionID);
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_sectionDetailsFragment_to_addNewTaskFragment, bundle);
            }
        });

        todoTasks.clear();
        db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("TodoTasks").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    TodoTask task = documentSnapshot.toObject(TodoTask.class);
                    todoTasks.add(task);
                }

                RecyclerView recyclerView = view.findViewById(R.id.recycle_todoTasks);
                TextView hint = view.findViewById(R.id.empty_hint);
                if (!todoTasks.isEmpty())
                    hint.setVisibility(View.GONE);
                taskAdapter = new TaskAdapter(todoTasks);
                taskAdapter.setOnItemClickedListener(new OnItemClicked() {
                    @Override
                    public void onItemClicked(int position) {
                        String todoTasksID = todoTasks.get(position).getId();
                        Bundle bundle = new Bundle();
                        bundle.putString("todolistID", todolistID);
                        bundle.putString("sectionID", sectionID);
                        bundle.putString("todoTasksID", todoTasksID);
                        bundle.putString("sectionName",sectionName);
                        NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_sectionDetailsFragment_to_taskDetailsFragment, bundle);
                    }
                });

                recyclerView.setAdapter(taskAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


            }
        });
        return view;
    }
}