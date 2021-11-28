package com.example.todostaskmanagementsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.interfaces.OnActionClicked;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Role;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.MyViewHolder> {

    private ArrayList<Role> roles = new ArrayList<>();
    private OnItemClicked listener;
    private OnActionClicked listenerAction;

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

        if (role.getId().equals("R1"))
            holder.delete.setVisibility(View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClicked(position);
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenerAction != null) {
                    listenerAction.onActionClicked(position, "delete");
                }
            }
        });
    }

    public void setOnItemClickedListener(OnItemClicked listener) {
        this.listener = listener;
    }

    public void setOnActionClickedListener(OnActionClicked listener) {
        this.listenerAction = listener;
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView roleName;
        private Button delete;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            roleName = itemView.findViewById(R.id.role_name);
            delete = itemView.findViewById(R.id.btn_delete_role);
        }
    }
}
