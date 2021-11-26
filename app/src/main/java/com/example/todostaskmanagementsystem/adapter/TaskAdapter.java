package com.example.todostaskmanagementsystem.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.interfaces.OnActionClicked;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.ChangesLog;
import com.example.todostaskmanagementsystem.model.TodoTask;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private ArrayList<TodoTask> todoTasks;
    private Boolean isMarkAllowed;
    private OnActionClicked listener;


    public TaskAdapter(ArrayList<TodoTask> todoTasks, Boolean isMarkAllowed) {
        this.todoTasks = todoTasks;
        this.isMarkAllowed = isMarkAllowed;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_task_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TaskAdapter.MyViewHolder holder, int position) {
        holder.name.setText(todoTasks.get(position).getName());
        holder.due.setText(todoTasks.get(position).getDueDate());
        holder.complete.setChecked(todoTasks.get(position).getComplete());
        holder.complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    if(!isMarkAllowed){
                        holder.complete.setChecked(!holder.complete.isChecked());
                        listener.onActionClicked(position,"notAllowed");
                    }else{
                        if(holder.complete.isChecked()){
                            listener.onActionClicked(position,"mark");
                        }else{
                            listener.onActionClicked(position,"unmark");
                        }
                    }
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onActionClicked(position,"navigate");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoTasks.size();
    }

    public void setOnItemClickedListener(OnActionClicked listener) {
        this.listener = listener;
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
