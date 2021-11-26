package com.example.todostaskmanagementsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.interfaces.OnActionClicked;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RoleCheckAdapter extends RecyclerView.Adapter<RoleCheckAdapter.MyViewHolder> {

    private List<String> roles;
    private List<String> checkedRoles;
    private OnActionClicked listener;

    public RoleCheckAdapter(List<String> roles) {
        this.roles = roles;
        checkedRoles = new ArrayList<>();
    }

    public RoleCheckAdapter(List<String> roles, List<String> checkedRoles) {
        this.roles = roles;
        this.checkedRoles = checkedRoles;
    }

    @NonNull
    @NotNull
    @Override
    public RoleCheckAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_role_check_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RoleCheckAdapter.MyViewHolder holder, int position) {
        String role = roles.get(position);
        holder.txtRole.setText(role);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onActionClicked(position, String.valueOf(holder.checkBox.isChecked()));
                }
            }
        });

        if (checkedRoles.contains(roles.get(position))){
            holder.checkBox.setChecked(true);
        }
    }

    public void setOnActionClickedListener(OnActionClicked listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtRole;
        CheckBox checkBox;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtRole = itemView.findViewById(R.id.txt_role);
            checkBox = itemView.findViewById(R.id.chkBox_role);
        }
    }
}
