package com.example.todostaskmanagementsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.todostaskmanagementsystem.adapter.TodolistAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> recentArray = new ArrayList<>();
    private ArrayList<Todolist> recentTodolists = new ArrayList<>();
    private String recentOne, recentTwo, recentThree;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull @org.jetbrains.annotations.NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = view.findViewById(R.id.btn_mytodolist);
        Button btn2 = view.findViewById(R.id.btn_myprof);

        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMMM yyyy ");

        String welcomeMsg= formatter.format(new Date()) + "\n\nWelcome Back!";
        TextView txtWelcome = view.findViewById(R.id.dateTimeText);
        txtWelcome.setText(welcomeMsg);


        SharedPreferences prefs = getActivity().getSharedPreferences("recent_accessed", Context.MODE_PRIVATE);

        recentOne = prefs.getString("recentOne", null);
        recentTwo = prefs.getString("recentTwo", null);
        recentThree = prefs.getString("recentThree", null);
        String recentOneTitle = prefs.getString("recentOneTitle", null);
        String recentTwoTitle = prefs.getString("recentTwoTitle", null);
        String recentThreeTitle = prefs.getString("recentThreeTitle", null);
        boolean emptyFlag = true;
        if (recentOne != null) {
            Button btn = view.findViewById(R.id.btn_recentAccessOne);
            btn.setText(recentOneTitle);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(this);
            emptyFlag = false;
        }
        if (recentTwo != null) {
            Button btn = view.findViewById(R.id.btn_recentAccessTwo);
            btn.setText(recentTwoTitle);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(this);
            emptyFlag = false;
        }
        if (recentThree != null) {
            Button btn = view.findViewById(R.id.btn_recentAccessThree);
            btn.setText(recentThreeTitle);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(this);
            emptyFlag = false;
        }

        if (emptyFlag) {
            TextView hint = view.findViewById(R.id.empty_hint);
            hint.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        String todolistID = null;
        String position = null;
        switch (v.getId()) {
            case R.id.btn_mytodolist:
                NavHostFragment.findNavController(getParentFragment()).navigate(HomeFragmentDirections.actionHomeFragmentToMyTodolistsFragment());
                break;
            case R.id.btn_myprof:
                NavHostFragment.findNavController(getParentFragment()).navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment()); //To change
                break;
            case R.id.btn_recentAccessOne:
                todolistID = recentOne;
                position = "1";
                break;
            case R.id.btn_recentAccessTwo:
                todolistID = recentTwo;
                position = "2";
                break;
            case R.id.btn_recentAccessThree:
                todolistID = recentThree;
                position = "3";
                break;
            default:
                break;
        }
        if (todolistID != null) {
            Bundle bundle = new Bundle();
            bundle.putString("todolistID", todolistID);
            bundle.putString("quickAccess_pos", position);
            NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_homeFragment_to_todoListDetailsFragment, bundle);
        }

    }
}