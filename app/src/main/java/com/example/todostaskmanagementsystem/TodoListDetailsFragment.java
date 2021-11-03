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
import android.view.Menu;
import android.view.MenuInflater;
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
    private String todolistName;
    private SectionAdapter sectionAdapter;
    private ArrayList<Section> sections = new ArrayList();

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText dialogSectionName;
    private Button dialogAddBtn, dialogCancelBtn;

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
        setHasOptionsMenu(true);
        //Setup Add Section Button
        Button btnAdd = view.findViewById(R.id.btn_addSection);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddSectionDialog();
            }
        });


        RecyclerView recyclerView = view.findViewById(R.id.recycle_sections);
        sectionAdapter = new SectionAdapter(sections);
        sectionAdapter.setOnItemClickedListener(new OnItemClicked() {
            @Override
            public void onItemClicked(int position) {
                String sectionID = sections.get(position).getId();
                Bundle bundle = new Bundle();
                bundle.putString("todolistID", todolistID);
                bundle.putString("sectionID", sectionID);
                bundle.putString("sectionName", sections.get(position).getName());
                bundle.putString("todolistName", todolistName);

                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_todoListDetailsFragment_to_sectionDetailsFragment, bundle);
            }
        });
        recyclerView.setAdapter(sectionAdapter);
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
                loadData(view);
                pullToRefresh.setRefreshing(false);
            }
        });

    }

    private void loadData(View view) {
        //Get and Set Todolist Details
        db.collection("Todolists").document(todolistID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Todolist todolist = documentSnapshot.toObject(Todolist.class);
                TextView txtViewTitle = view.findViewById(R.id.todolist_title);
                TextView txtViewDesc = view.findViewById(R.id.todolist_desc);

                txtViewTitle.setText(todolist.getName());
                todolistName = todolist.getName();
                txtViewDesc.setText(todolist.getDesc());
            }
        });
        sections.clear();
        db.collection("Todolists").document(todolistID).collection("Sections").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Section section = documentSnapshot.toObject(Section.class);
                    sections.add(section);
                }
                updateRecycleView();
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
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

    private void createAddSectionDialog(){
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View addSectionView = getLayoutInflater().inflate(R.layout.dialog_add_section,null);
        dialogSectionName = addSectionView.findViewById(R.id.section_name);
        dialogAddBtn = addSectionView.findViewById(R.id.btnConfirm);
        dialogCancelBtn = addSectionView.findViewById(R.id.btnCancel);

        dialogBuilder.setView(addSectionView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        dialogAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sectionName = dialogSectionName.getText().toString();
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
                        dialog.dismiss();
                    }
                });
            }
        });

        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.todolist_item_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}