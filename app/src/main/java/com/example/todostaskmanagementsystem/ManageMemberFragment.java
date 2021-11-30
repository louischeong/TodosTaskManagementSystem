package com.example.todostaskmanagementsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.adapter.AddMemberAdapter;
import com.example.todostaskmanagementsystem.adapter.MemberAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.interfaces.OnItemSelected;
import com.example.todostaskmanagementsystem.interfaces.OnSpinnerItemSelect;
import com.example.todostaskmanagementsystem.model.ChangesLog;
import com.example.todostaskmanagementsystem.model.Member;
import com.example.todostaskmanagementsystem.model.Notification;
import com.example.todostaskmanagementsystem.model.Role;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ManageMemberFragment extends Fragment {

    private List<String> joinedMemberEmails = new ArrayList<>();
    private List<String> invitedMemberEmails = new ArrayList<>();
    private MemberAdapter memberAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String todolistID;
    private List<String> members = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
    private View view;

    public ManageMemberFragment() {
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
        view = inflater.inflate(R.layout.fragment_manage_member, container, false);

        Button addBtn = view.findViewById(R.id.btn_addmember);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View addEmailView = createAddMemberEmailDialog();

                db.collection("Notifications").document(todolistID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Notification notification = documentSnapshot.toObject(Notification.class);
                        invitedMemberEmails = notification.getRecipientEmails();
                        db.collection("Todolists").document(todolistID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Todolist todolist = documentSnapshot.toObject(Todolist.class);
                                joinedMemberEmails = todolist.getMembersEmail();
                                addEmailView.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });

                    }
                });
            }
        });


        memberAdapter = new MemberAdapter(roles, members, getContext());
        memberAdapter.setOnSpinnerItemSelectListener(new OnSpinnerItemSelect() {
            @Override
            public void onSpinnerItemSelect(int position, int oldSpinnerItemPosition, int newSpinnerItemPosition) {
                String member = members.get(position);
                db.collection("Todolists").document(todolistID).collection("Roles").document(roles.get(oldSpinnerItemPosition).getId()).update("members", FieldValue.arrayRemove(member));
                db.collection("Todolists").document(todolistID).collection("Roles").document(roles.get(newSpinnerItemPosition).getId()).update("members", FieldValue.arrayUnion(member));
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.memberRecycler);
        recyclerView.setAdapter(memberAdapter);
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

    public void loadData() {
        roles.clear();
        members.clear();
        db.collection("Todolists").document(todolistID).collection("Roles").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Role role = documentSnapshot.toObject(Role.class);
                    roles.add(role);
                    if (role.getMembers() != null) {
                        for (String s : role.getMembers()) {
                            members.add(s);

                        }
                    }
                }
                memberAdapter.notifyDataSetChanged();
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });
    }

    private View createAddMemberEmailDialog() {

        List<String> memberEmails = new ArrayList<>();
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        EditText dialogEmail;
        RecyclerView dialogRecyclerView;
        Button dialogAddBtn, dialogSendBtn, dialogCancelBtn;

        dialogBuilder = new AlertDialog.Builder(getContext());
        final View addEmailView = getLayoutInflater().inflate(R.layout.dialog_add_members, null);
        dialogEmail = addEmailView.findViewById(R.id.dialog_input_member_email);
        dialogAddBtn = addEmailView.findViewById(R.id.btn_addEmail);
        dialogSendBtn = addEmailView.findViewById(R.id.dialog_btnSend);
        dialogCancelBtn = addEmailView.findViewById(R.id.dialog_btnCancel);
        dialogRecyclerView = addEmailView.findViewById(R.id.recycle_emails);

        AddMemberAdapter addMemberAdapter = new AddMemberAdapter(memberEmails);
        addMemberAdapter.setOnItemClickedListener(new OnItemClicked() {
            @Override
            public void onItemClicked(int position) {
                memberEmails.remove(position);
                updateRecycleView(addEmailView, addMemberAdapter, memberEmails);
            }
        });
        dialogRecyclerView.setAdapter(addMemberAdapter);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dialogBuilder.setView(addEmailView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        dialogAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validation email
                String email = dialogEmail.getText().toString().trim();
                if (dialogEmail.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a email to add.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (joinedMemberEmails.contains(email)) {
                    Toast.makeText(getActivity(), "The user is already a member of this todolist.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (invitedMemberEmails.contains(email)) {
                    Toast.makeText(getActivity(), "Unable to add due to an existing invitation has already been sent to " + email, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!memberEmails.contains(email)) {
                    memberEmails.add(email);
                    dialogEmail.setText("");
                    updateRecycleView(addEmailView, addMemberAdapter, memberEmails);
                } else {
                    Toast.makeText(getActivity(), "Duplicated email are not allowed.", Toast.LENGTH_SHORT).show();
                    dialogEmail.setText("");
                }
            }
        });

        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!memberEmails.isEmpty()) {
                    memberEmails.addAll(invitedMemberEmails);
                    db.collection("Notifications").document(todolistID).update("recipientEmails", memberEmails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Invitation sent successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Please enter new user email to invite.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return addEmailView;
    }

    private void updateRecycleView(View view, AddMemberAdapter addMemberAdapter, List<String> memberEmails) {
        TextView txt = view.findViewById(R.id.empty_hint);
        if (memberEmails.isEmpty()) {
            txt.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.INVISIBLE);
        }
        addMemberAdapter.notifyDataSetChanged();
    }
}