package com.example.todostaskmanagementsystem.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.model.ChangesLog;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ChangesLogAdapter extends RecyclerView.Adapter<ChangesLogAdapter.MyViewHolder> {

    private ArrayList<ChangesLog> changeLogs;
    private Context context;

    public ChangesLogAdapter(ArrayList<ChangesLog> changeLogs, Context context) {
        this.changeLogs = changeLogs;
        this.context = context;
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
        ChangesLog changesLog = changeLogs.get(position);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        String username = changesLog.getUsername();
        SpannableString usernameSpannable = new SpannableString(username);
        usernameSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary_blue)), 0, username.length(), 0);
        builder.append(usernameSpannable);
        String action = "";

        switch (changesLog.getChangesType()) {
            case "CreateTodolist":
                action = " has created the Todolist: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getParentName(), R.color.primary_blue);
                break;
            case "CreateSection":
                action = " has created the Section: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getParentName(), R.color.primary_blue);
                break;
            case "CreateTask":
                action = " has created the Task: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getChildName(), R.color.primary_blue);
                action = " inside the Section: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getParentName(), R.color.primary_blue);
                break;
            case "EditTodolist":
                action = " has updated the Todolist: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getParentName(), R.color.primary_blue);
                action = " details.";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                break;
            case "EditSection":
                action = " has updated the Section details: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getParentName(), R.color.primary_blue);
                break;
            case "EditTask":
                action = " has updated the Task: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getChildName(), R.color.primary_blue);
                action = " details inside the Section: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getParentName(), R.color.primary_blue);
                break;
            case "MarkTask":
                action = " has marked the Task: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getChildName(), R.color.primary_blue);
                action = " as completed inside the Section: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getParentName(), R.color.primary_blue);
                break;
            case "UnmarkTask":
                action = " has marked the Task: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getChildName(), R.color.primary_blue);
                action = " as incomplete inside the Section: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getParentName(), R.color.primary_blue);
                break;
            case "DeleteTask":
                action = " has deleted the Task: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getChildName(), R.color.primary_blue);
                action = " inside the Section: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getParentName(), R.color.primary_blue);
                break;
            case "DeleteSection":
                action = " has deleted the Section: ";
                appendTextWithColor(builder, action, R.color.dark_gray_text);
                appendTextWithColor(builder, changesLog.getParentName(), R.color.primary_blue);
                break;

        }


        holder.msg.setText(builder, TextView.BufferType.SPANNABLE);
        Date date = changeLogs.get(position).getDateTime().toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM\nHH:mm");
        String strDate = simpleDateFormat.format(date);
        Date curDate = Calendar.getInstance().getTime();
        String strCurDate = simpleDateFormat.format(curDate);
        if (strCurDate.substring(0, 5).equals(strDate.substring(0, 5))) {
            strDate = "Today\n" + strDate.substring(strDate.length() - 5);
        }
        holder.dateTime.setText(strDate);
    }

    public void appendTextWithColor(SpannableStringBuilder builder, String str, int colorID) {
        SpannableString spannable = new SpannableString(str);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorID)), 0, str.length(), 0);
        builder.append(spannable);
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
