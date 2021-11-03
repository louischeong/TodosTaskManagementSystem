package com.example.todostaskmanagementsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Role;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.MyViewHolder> {

    private ArrayList<Role> roles = new ArrayList<>();
    private OnItemClicked listener;

    public RoleAdapter(ArrayList<Role> roles) {
        this.roles = roles;
    }

    @NonNull
    @NotNull
    @Override
    public RoleAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_role_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RoleAdapter.MyViewHolder holder, int position) {
        Role role = roles.get(position);
        holder.roleName.setText(role.getRoleName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onItemClicked(position);
                }
            }
        });
    }

    public void setOnItemClickedListener(OnItemClicked listener){
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView roleName;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            roleName = itemView.findViewById(R.id.role_name);
        }
    }
}
