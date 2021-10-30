package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SectionDetailsFragment extends Fragment {

    private ArrayList<TodoTask> todoTasks = new ArrayList();
    private String todolistID;
    private String sectionID;
    private String sectionName;
    private String todolistName;
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
            todolistName = bundle.getString("todolistName");
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

        taskAdapter = new TaskAdapter(todoTasks,todolistID,sectionID);
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
        RecyclerView recyclerView = view.findViewById(R.id.recycle_todoTasks);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadData(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SwipeRefreshLayout pullToRefresh = getView().findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(getView());
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void loadData(View view){
        db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Section sec = documentSnapshot.toObject(Section.class);
                TextView txtSecTitle = view.findViewById(R.id.section_title);
                TextView txtTodolistName = view.findViewById(R.id.todolist_title);
                sectionName = sec.getName();
                txtSecTitle.setText(sectionName);
                txtTodolistName.setText(todolistName);
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

                updateRecycleView();
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });
    }
    private void updateRecycleView() {
        TextView txt = getView().findViewById(R.id.empty_hint);
        if (todoTasks.isEmpty()) {
            txt.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.INVISIBLE);
        }
        taskAdapter.notifyDataSetChanged();
    }
}