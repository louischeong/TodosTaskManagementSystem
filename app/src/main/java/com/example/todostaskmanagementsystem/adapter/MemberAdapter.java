package com.example.todostaskmanagementsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.model.Member;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {

    private ArrayList<Member> memberRoles;
    private Context context;
    private List<String> spinnerItems;

    public MemberAdapter(ArrayList<Member> memberRoles, Context context, List<String> spinnerItems) {
        this.memberRoles = memberRoles;
        this.context = context;
        this.spinnerItems = spinnerItems;
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
        holder.name.setText(memberRoles.get(position).getName());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();
        holder.spinner.setSelection(spinnerAdapter.getPosition(memberRoles.get(position).getRole()));
    }

    @Override
    public int getItemCount() {
        return memberRoles.size();
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
