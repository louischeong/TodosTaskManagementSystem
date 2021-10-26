package com.example.todostaskmanagementsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.todostaskmanagementsystem.adapter.SectionAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Section;
import com.example.todostaskmanagementsystem.model.TodoTask;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoListDetailsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String todolistID;
    private SectionAdapter sectionAdapter;
    private ArrayList<Section> sections = new ArrayList();


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

        //Setup Add Section Button
        Button btnAdd = view.findViewById(R.id.btn_addSection);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle("Enter new section name");

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sectionName = input.getText().toString();
                        db.collection("Todolists").document(todolistID).collection("Data").document("Data").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                int currSectionID = Integer.parseInt(documentSnapshot.get("currSectionID").toString()) + 1;
                                String secID = "S" + currSectionID;
                                Section sec = new Section(secID, sectionName);
                                sections.add(sec);
                                db.collection("Todolists").document(todolistID).collection("Sections").document(secID).set(sec);
                                db.collection("Todolists").document(todolistID).collection("Data").document("Data").update("currSectionID", currSectionID);
                                updateRecycleView();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
                builder.show();
            }
        });

        //Get and Set Todolist Details
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

        loadSectionTaskData(view);


        //Get and Set Todolist Sections
//        db.collection("Todolists").document(todolistID).collection("Sections").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    Section section = documentSnapshot.toObject(Section.class);
//                    sections.add(section);
//                }
//                for (int i = 0; i < sections.size(); i++) {
//                    int finalI = i;
//
//                    readTasks(new FirestoreCallback() {
//                        @Override
//                        public void onCallback(ArrayList<Task> tasks) {
//                            sections.get(finalI).setTasks(tasks);
//                            Log.d("MYDEBUG", "Got " + tasks.toString() + " Into " + sections.get(finalI).toString());
//                            Log.d("MYDEBUG3", "1");
//                        }
//                    }, Integer.toString(sections.get(i).getId()));
//                }
//                RecyclerView recyclerView = view.findViewById(R.id.recycle_sections);
//                TextView hint = view.findViewById(R.id.empty_hint);
//                if (!sections.isEmpty())
//                    hint.setVisibility(View.GONE);
//                //Log.d("MYDEBUG2","PASSING ADAPTER SECTION " + sections.get(0).toString() + " With Tasks " + sections.get(0).getTasks().toString());
//                Log.d("MYDEBUG3", "2");
//                sectionAdapter = new SectionAdapter(sections);
//                sectionAdapter.setOnItemClickedListener(new OnItemClicked() {
//                    @Override
//                    public void onItemClicked(int position) {
//                        String sectionID = Integer.toString(sections.get(position).getId());
//                        Bundle bundle = new Bundle();
//                        bundle.putString("todolistID", todolistID);
//                        bundle.putString("sectionID", sectionID);
//                        NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_todoListDetailsFragment_to_addNewTaskFragment, bundle);
//                    }
//                });
//                recyclerView.setAdapter(sectionAdapter);
//                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//            }
//        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SwipeRefreshLayout pullToRefresh = getView().findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecycleView();
                pullToRefresh.setRefreshing(false);
            }
        });

    }

    private void loadSectionTaskData(View view) {
        sections.clear();
        db.collection("Todolists").document(todolistID).collection("Sections").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Section section = documentSnapshot.toObject(Section.class);
                    sections.add(section);
                }

                RecyclerView recyclerView = view.findViewById(R.id.recycle_sections);
                TextView hint = view.findViewById(R.id.empty_hint);
                if (!sections.isEmpty())
                    hint.setVisibility(View.GONE);
                sectionAdapter = new SectionAdapter(sections);
                sectionAdapter.setOnItemClickedListener(new OnItemClicked() {
                    @Override
                    public void onItemClicked(int position) {
                        String sectionID = sections.get(position).getId();
                        Bundle bundle = new Bundle();
                        bundle.putString("todolistID", todolistID);
                        bundle.putString("sectionID", sectionID);
                        bundle.putString("sectionName", sections.get(position).getName());
                        NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_todoListDetailsFragment_to_sectionDetailsFragment, bundle);
                    }
                });
                recyclerView.setAdapter(sectionAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        });
    }

    private void updateRecycleView() {

        TextView txt = getView().findViewById(R.id.empty_hint);
        if (sections.isEmpty()) {
            txt.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.INVISIBLE);
        }
        sectionAdapter.notifyDataSetChanged();
    }

}