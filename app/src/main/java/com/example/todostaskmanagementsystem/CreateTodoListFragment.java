package com.example.todostaskmanagementsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.todostaskmanagementsystem.adapter.AddMemberAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CreateTodoListFragment extends Fragment implements View.OnClickListener {

    private String m_Text = "";
    private ArrayList<String> emails = new ArrayList<>();
    private AddMemberAdapter addMemberAdapter;

    public CreateTodoListFragment() {
        // Required empty public constructor
    }

    public static CreateTodoListFragment newInstance(String param1, String param2) {
        CreateTodoListFragment fragment = new CreateTodoListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_todo_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = view.findViewById(R.id.btn_addMemberEmail);
        btn.setOnClickListener(this);
        Button btn2 = view.findViewById(R.id.btn_createNewTodolist);
        btn2.setOnClickListener(this);


        addMemberAdapter = new AddMemberAdapter(emails);
        addMemberAdapter.setOnItemClickedListener(new OnItemClicked() {
            @Override
            public void onItemClicked(int position) {
                emails.remove(position);
                updateRecycleView();
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycle_emails);
        recyclerView.setAdapter(addMemberAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addMemberEmail:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle("Enter new email");

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        emails.add(input.getText().toString());
                        updateRecycleView();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                builder.show();
                break;
            case R.id.btn_createNewTodolist:
                //To change
                break;
            default:
                break;
        }
    }

    private void updateRecycleView() {
        TextView txt = getView().findViewById(R.id.empty_hint);
        if (emails.isEmpty()) {
            txt.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.INVISIBLE);
        }
        addMemberAdapter.notifyDataSetChanged();
    }


}