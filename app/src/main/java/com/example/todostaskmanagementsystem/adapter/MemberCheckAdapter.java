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

import java.util.List;

public class MemberCheckAdapter extends RecyclerView.Adapter<MemberCheckAdapter.MyViewHolder> {

    private List<String> emails;
    private OnActionClicked listener;

    public MemberCheckAdapter(List<String> emails) {
        this.emails = emails;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_member_check_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        String email = emails.get(position);
        holder.txtEmail.setText(email);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onActionClicked(position, String.valueOf(holder.checkBox.isChecked()));
                }
            }
        });


    }

    public void setOnActionClickedListener(OnActionClicked listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmail;
        CheckBox checkBox;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtEmail = itemView.findViewById(R.id.txt_email);
            checkBox = itemView.findViewById(R.id.chkBox_member);
        }
    }
}
