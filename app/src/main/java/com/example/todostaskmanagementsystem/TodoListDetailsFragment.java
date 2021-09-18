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

import com.example.todostaskmanagementsystem.adapter.SectionAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Section;
import com.example.todostaskmanagementsystem.model.Task;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TodoListDetailsFragment extends Fragment {

    public TodoListDetailsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_todo_list_details, container, false);
        Task task = new Task("test1", "descrip", true, "yes1212");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task);
        tasks.add(task);
        Section sec = new Section("title1", tasks);
        ArrayList<Section> sections = new ArrayList<>();
        sections.add(sec);
        sections.add(sec);

        RecyclerView recyclerView = view.findViewById(R.id.recycle_sections);

        SectionAdapter adapter = new SectionAdapter(sections);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}