package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.todostaskmanagementsystem.adapter.SectionAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Section;
import com.example.todostaskmanagementsystem.model.Task;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class TodoListDetailsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String todolistID;
    ArrayList<Section> sections = new ArrayList();
    ArrayList<Task> tasks = new ArrayList();

    public TodoListDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            todolistID = bundle.getString("todolistID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todo_list_details, container, false);

        db.collection("Todolists").document(todolistID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Todolist todolist = documentSnapshot.toObject(Todolist.class);
                TextView txtViewTitle = view.findViewById(R.id.todolist_title);
                TextView txtViewDesc = view.findViewById(R.id.todolist_desc);
                TextView txtViewIncomplete = view.findViewById(R.id.todolist_incomplete_count);
                txtViewTitle.setText(todolist.getName());
                txtViewDesc.setText(todolist.getDesc());
                txtViewIncomplete.setText(Integer.toString(todolist.getIncomplete()));
            }
        });

        db.collection("Todolists").document(todolistID).collection("Sections").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Section section = documentSnapshot.toObject(Section.class);
                    sections.add(section);
                }
                for (int i = 0; i < sections.size(); i++) {
                    int finalI = i;
                    readTasks(new FirestoreCallback() {
                       @Override
                       public void onCallback(ArrayList<Task> tasks) {
                           sections.get(finalI).setTasks(tasks);
                       }
                   },Integer.toString(sections.get(i).getId()));
                }
                RecyclerView recyclerView = view.findViewById(R.id.recycle_sections);
                SectionAdapter adapter = new SectionAdapter(sections);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void readTasks(FirestoreCallback firestoreCallback, String sectionID) {

        db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("Tasks").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                tasks.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Task task = documentSnapshot.toObject(Task.class);
                    tasks.add(task);
                }
                firestoreCallback.onCallback(tasks);
            }
        });
    }

    private interface FirestoreCallback {
        void onCallback(ArrayList<Task> tasks);
    }
}