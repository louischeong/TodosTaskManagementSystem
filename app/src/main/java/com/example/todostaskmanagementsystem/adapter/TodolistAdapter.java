package com.example.todostaskmanagementsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.model.Todolist;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TodolistAdapter extends RecyclerView.Adapter<TodolistAdapter.MyViewHolder> {

    private ArrayList<Todolist> todolists;
    private Context cont;

    public TodolistAdapter(Context cont, ArrayList<Todolist> todolists){
        this.cont = cont;
        this.todolists = todolists;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_todolist_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TodolistAdapter.MyViewHolder holder, int position) {
        holder.title.setText(todolists.get(position).getName());
        holder.desc.setText(todolists.get(position).getDesc());
        holder.incomplete.setText(Integer.toString(todolists.get(position).getIncomplete()));
    }

    @Override
    public int getItemCount() {
        return todolists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title, desc, incomplete;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.todolist_title);
            desc = itemView.findViewById(R.id.todolist_desc);
            incomplete = itemView.findViewById(R.id.todolist_incomplete);
        }
    }
}
