package com.example.todostaskmanagementsystem.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todostaskmanagementsystem.R;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Section;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.MyViewHolder> {

    private ArrayList<Section> sections;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private int mExpandedPosition = -1;

    public SectionAdapter(ArrayList<Section> sections) {
        this.sections = sections;
    }

    @NonNull
    @NotNull
    @Override
    public SectionAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_section_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SectionAdapter.MyViewHolder holder, int position) {
        Section section = sections.get(position);
        holder.name.setText(section.getName());

        TaskAdapter taskAdapter = new TaskAdapter(sections.get(position).getTasks());
        holder.recyclerviewTasks.setAdapter(taskAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.recyclerviewTasks.getContext());
        if (section.getTasks()!=null){
            layoutManager.setInitialPrefetchItemCount(section.getTasks().size());
        }
        holder.recyclerviewTasks.setLayoutManager(layoutManager);
        holder.recyclerviewTasks.setRecycledViewPool(viewPool);

        final boolean isExpanded = position == mExpandedPosition;
        if (isExpanded) {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.lightblue_bg));
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_blue));
            holder.expanded.setVisibility(View.VISIBLE);
            holder.itemView.setActivated(true);
        } else {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.blue_bg));
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.expanded.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private RecyclerView recyclerviewTasks;
        private ConstraintLayout expanded;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.section_title);
            recyclerviewTasks = itemView.findViewById(R.id.recycle_tasks);
            expanded = itemView.findViewById(R.id.expanded_section);
        }

    }
}
