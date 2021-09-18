package com.example.todostaskmanagementsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.model.Task;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private ArrayList<Task> tasks;


    public TaskAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_task_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TaskAdapter.MyViewHolder holder, int position) {
        holder.name.setText(tasks.get(position).getName());
        //holder.due.setText(tasks.get(position).getDueDate().toString());
        holder.complete.setChecked(tasks.get(position).getComplete());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, due;
        CheckBox complete;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.task_name);
            due = itemView.findViewById(R.id.task_due);
            complete = itemView.findViewById(R.id.task_complete);
        }
    }
}
