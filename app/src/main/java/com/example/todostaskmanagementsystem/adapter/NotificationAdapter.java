package com.example.todostaskmanagementsystem.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.interfaces.OnActionClicked;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Notification;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private ArrayList<Notification> notifications;
    private int mExpandedPosition = -1;
    private int previousExpandedPosition = -1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OnActionClicked listener;
    private Context context;

    public NotificationAdapter(ArrayList<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_notification_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NotificationAdapter.MyViewHolder holder, int position) {
        final boolean isExpanded = position == mExpandedPosition;
        holder.expandable.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        String msg = "\"" + notifications.get(position).getTodolistTitle() + "\" " + " created by " + notifications.get(position).getOwner();
        holder.msg.setText(msg);
        String title = "Invitation: " + notifications.get(position).getTodolistTitle();
        holder.invitationTitle.setText(title);

        if (isExpanded) {
            previousExpandedPosition = position;
            holder.beforeExpand.setBackground(ContextCompat.getDrawable(context, R.drawable.lightblue_notification_upper));
            holder.invitationTitle.setTextColor(ContextCompat.getColor(context, R.color.primary_blue));
        } else {
            holder.beforeExpand.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_btn));
            holder.invitationTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.beforeExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);
            }
        });

        holder.btnIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onActionClicked(position, "ignore");
                }
            }
        });

        holder.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onActionClicked(position, "join");
                }
            }
        });
    }

    public void setOnActionClickedListener(OnActionClicked listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTime, msg, invitationTitle;
        Button btnIgnore, btnJoin;
        ConstraintLayout beforeExpand, expandable;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dateTime = itemView.findViewById(R.id.dateTime);
            msg = itemView.findViewById(R.id.invitation_msg);
            btnIgnore = itemView.findViewById(R.id.ignore_button);
            btnJoin = itemView.findViewById(R.id.join_button);
            beforeExpand = itemView.findViewById(R.id.expand_notification);
            expandable = itemView.findViewById(R.id.expandable_notification);
            invitationTitle = itemView.findViewById(R.id.invitation_title);
        }
    }
}
