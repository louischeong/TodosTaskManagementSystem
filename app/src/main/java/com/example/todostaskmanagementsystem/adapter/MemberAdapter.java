package com.example.todostaskmanagementsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.interfaces.OnActionClicked;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.interfaces.OnItemSelected;
import com.example.todostaskmanagementsystem.interfaces.OnSpinnerItemSelect;
import com.example.todostaskmanagementsystem.model.Member;
import com.example.todostaskmanagementsystem.model.Role;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {

    private List<Role> roles;
    private List<String> members;
    private Context context;
    private OnSpinnerItemSelect listener;

    public MemberAdapter(List<Role> roles, List<String> members, Context context) {
        this.roles = roles;
        this.members = members;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_member_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.name.setText(members.get(position));
        //set spinner items
        List<String> spinnerItems = new ArrayList<>();
        for (Role r : roles) {
            spinnerItems.add(r.getRoleName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, spinnerItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getMembers() != null)
                if (roles.get(i).getMembers().contains(members.get(position)))
                    holder.spinner.setSelection(i);
        }
        holder.spinner.setTag(position);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (listener != null && ((int) holder.spinner.getTag() != pos)) {
                    listener.onSpinnerItemSelect(position, (int) holder.spinner.getTag(), holder.spinner.getSelectedItemPosition());
                    holder.spinner.setTag(pos);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setOnSpinnerItemSelectListener(OnSpinnerItemSelect listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Spinner spinner;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_manageMemberName);
            spinner = itemView.findViewById(R.id.roleSpinner);
        }
    }
}
