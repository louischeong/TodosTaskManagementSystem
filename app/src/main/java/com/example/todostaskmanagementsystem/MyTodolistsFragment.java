package com.example.todostaskmanagementsystem;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.adapter.TodolistAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class MyTodolistsFragment extends Fragment implements View.OnClickListener {


    private EditText searchBar;
    private View rootView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyTodolistsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_todolists, container, false);
        rootView = view.findViewById(R.id.rootLayout);
        rootView.requestFocus();
        //setup Button
        view.findViewById(R.id.button_create_new).setOnClickListener(this);
        view.findViewById(R.id.btn_search).setOnClickListener(this);

        //Setup search view
        searchBar = view.findViewById(R.id.search_bar);

        db.collection("Todolists").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Todolist> todolists = new ArrayList();
                ArrayList<String> todolistIDs = new ArrayList();
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    Todolist todolist = documentSnapshot.toObject(Todolist.class);
                    todolists.add(todolist);
                    todolistIDs.add(documentSnapshot.getId());
                }
                RecyclerView recyclerView = view.findViewById(R.id.recycle_todolists);

                TodolistAdapter adapter = new TodolistAdapter(todolists);
                adapter.setOnItemClickedListener(new OnItemClicked() {
                    @Override
                    public void onItemClicked(int position) {
                        String todolistID = todolistIDs.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("todolistID", todolistID);
                        NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_myTodolistsFragment_to_todoListDetailsFragment, bundle);
                        closeKeyboard();
                    }
                });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_create_new:
                NavHostFragment.findNavController(getParentFragment()).navigate(MyTodolistsFragmentDirections.actionMyTodolistsFragmentToCreateTodoList());
                break;
            default:
        }
        closeKeyboard();
    }

    @Override
    public void onResume() {
        super.onResume();
        searchBar.setText("");
        rootView.requestFocus();
        closeKeyboard();
    }

    public void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}