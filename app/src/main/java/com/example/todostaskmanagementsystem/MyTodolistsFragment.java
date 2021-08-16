package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todostaskmanagementsystem.adapter.TodolistAdapter;
import com.example.todostaskmanagementsystem.model.Todolist;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyTodolistsFragment extends Fragment {

    ArrayList<Todolist> todolists = new ArrayList<Todolist>();

    public MyTodolistsFragment() {
        // Required empty public constructor
    }
    public static MyTodolistsFragment newInstance(String param1, String param2) {
        MyTodolistsFragment fragment = new MyTodolistsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
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
        Todolist todo = new Todolist("ttile","descrip",0);
        todolists.add(todo);
        todolists.add(todo);
        todolists.add(todo);

        RecyclerView recyclerView = view.findViewById(R.id.recycle_todolists);

        TodolistAdapter adapter = new TodolistAdapter(getActivity(),todolists);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}