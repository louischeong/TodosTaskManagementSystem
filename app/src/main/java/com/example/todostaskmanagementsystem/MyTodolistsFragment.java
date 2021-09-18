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

import com.example.todostaskmanagementsystem.adapter.TodolistAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Todolist;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyTodolistsFragment extends Fragment implements View.OnClickListener {

    ArrayList<Todolist> todolists = new ArrayList<Todolist>();
    private EditText searchBar;
    private View rootView;

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
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_my_todolists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view.findViewById(R.id.rootLayout);
        rootView.requestFocus();
        //setup Button
        view.findViewById(R.id.button_create_new).setOnClickListener(this);
        view.findViewById(R.id.btn_search).setOnClickListener(this);

        //Setup search view
        searchBar = view.findViewById(R.id.search_bar);

        todolists.clear();
        Todolist todo = new Todolist("ttile", "descrip", 0);
        todolists.add(todo);
        todolists.add(todo);
        todolists.add(todo);
        todolists.add(todo);
        todolists.add(todo);
        todolists.add(todo);

        RecyclerView recyclerView = view.findViewById(R.id.recycle_todolists);

        TodolistAdapter adapter = new TodolistAdapter(todolists);
        adapter.setOnItemClickedListener(new OnItemClicked() {
            @Override
            public void onItemClicked(int position) {
                String todolistID = "1111"; //TO CHANGE
                Bundle bundle = new Bundle();
                bundle.putString("todolistID", todolistID);
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_myTodolistsFragment_to_todoListDetailsFragment, bundle);
                closeKeyboard();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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