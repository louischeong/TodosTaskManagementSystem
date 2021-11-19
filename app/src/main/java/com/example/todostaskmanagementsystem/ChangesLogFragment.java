package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todostaskmanagementsystem.adapter.ChangesLogAdapter;
import com.example.todostaskmanagementsystem.model.ChangesLog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChangesLogFragment extends Fragment {

    private ChangesLogAdapter changesLogAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String todolistID;
    private ArrayList<ChangesLog> changesLogs = new ArrayList<>();

    public ChangesLogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            todolistID = bundle.getString("todolistID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_changes_log, container, false);
        changesLogAdapter = new ChangesLogAdapter(changesLogs);
        RecyclerView recyclerView = view.findViewById(R.id.recycle_changeslog);
        recyclerView.setAdapter(changesLogAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadData(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SwipeRefreshLayout pullToRefresh = getView().findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(getView());
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void loadData(View view) {
        changesLogs.clear();
        db.collection("Todolists").document(todolistID).collection("ChangesLog").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ChangesLog changesLog = documentSnapshot.toObject(ChangesLog.class);
                    changesLogs.add(changesLog);
                }
                changesLogAdapter.notifyDataSetChanged();
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });
    }
}