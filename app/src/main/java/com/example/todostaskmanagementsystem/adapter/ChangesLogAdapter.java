package com.example.todostaskmanagementsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.model.ChangesLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChangesLogAdapter extends RecyclerView.Adapter<ChangesLogAdapter.MyViewHolder> {

    private ArrayList<ChangesLog> changeLogs;

    public ChangesLogAdapter(ArrayList<ChangesLog> changeLogs) {
        this.changeLogs = changeLogs;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_changeslog_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChangesLogAdapter.MyViewHolder holder, int position) {

        holder.msg.setText(changeLogs.get(position).getUsername());
        holder.dateTime.setText((changeLogs.get(position).getDateTime()));
    }

    @Override
    public int getItemCount() {
        return changeLogs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView msg;
        TextView dateTime;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.txt_msg);
            dateTime = itemView.findViewById(R.id.txt_dateTime);
        }
    }
}
