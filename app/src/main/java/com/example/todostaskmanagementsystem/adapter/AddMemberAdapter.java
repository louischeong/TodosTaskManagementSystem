package com.example.todostaskmanagementsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.MyViewHolder> {


    private ArrayList<String> emails;
    OnItemClicked listener;

    public AddMemberAdapter(ArrayList<String> emails) {

        this.emails = emails;

    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_email_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AddMemberAdapter.MyViewHolder holder, int position) {
        holder.email.setText(emails.get(position));
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClicked(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public void setOnItemClickedListener(OnItemClicked listener) {
        this.listener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView email;
        Button deleteBtn;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.text_email);
            deleteBtn = itemView.findViewById(R.id.btn_delete_email);
        }
    }
}
