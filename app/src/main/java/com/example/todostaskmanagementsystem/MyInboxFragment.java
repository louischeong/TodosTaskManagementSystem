package com.example.todostaskmanagementsystem;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.adapter.NotificationAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnActionClicked;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Notification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyInboxFragment extends Fragment {

    private ArrayList<Notification> notifications = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NotificationAdapter notificationAdapter;
    private View view;

    public MyInboxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_inbox, container, false);
        notificationAdapter = new NotificationAdapter(notifications, getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.recycle_notifications);
        notificationAdapter.setOnActionClickedListener(new OnActionClicked() {
            @Override
            public void onActionClicked(int position, String joinOrIgnore) {
                SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
                String email = prefs.getString("pref_email", null);
                if (joinOrIgnore.equals("join")) {

                    db.collection("Todolists").document(notifications.get(position).getTodolistID()).collection("Roles").document("R1").update("members",FieldValue.arrayUnion(email));
                    db.collection("Todolists").document(notifications.get(position).getTodolistID()).update("membersEmail", FieldValue.arrayUnion(email)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Join \"" + notifications.get(position).getTodolistTitle() + "\" successfully", Toast.LENGTH_SHORT).show();
                            deleteNotification(email, position);
                        }
                    });
                } else {
                    deleteNotification(email, position);
                    Toast.makeText(getActivity(), "Decline invitation to \"" + notifications.get(position).getTodolistTitle() + "\" successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadData();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                pullToRefresh.setRefreshing(false);
            }
        });

    }

    private void loadData() {
        notifications.clear();
        SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String email = prefs.getString("pref_email", null);
        db.collection("Notifications").whereArrayContains("recipientEmails", email).orderBy("dateTime", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Notification notification = documentSnapshot.toObject(Notification.class);
                    notifications.add(notification);
                }
                updateRecycleView();
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });
    }

    private void updateRecycleView() {
        TextView txt = view.findViewById(R.id.empty_hint);
        if (notifications.isEmpty()) {
            txt.setVisibility(View.VISIBLE);
            txt.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.INVISIBLE);
        }
        notificationAdapter.notifyDataSetChanged();
    }

    private void deleteNotification(String email, int position) {
        db.collection("Notifications").document(notifications.get(position).getTodolistID()).update("recipientEmails", FieldValue.arrayRemove(email));
        updateRecycleView();
        notifications.remove(position);
    }
}