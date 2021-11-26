package com.example.todostaskmanagementsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.adapter.RoleCheckAdapter;
import com.example.todostaskmanagementsystem.adapter.SectionAdapter;
import com.example.todostaskmanagementsystem.adapter.TaskAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnActionClicked;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.ChangesLog;
import com.example.todostaskmanagementsystem.model.Role;
import com.example.todostaskmanagementsystem.model.Section;
import com.example.todostaskmanagementsystem.model.TodoTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SectionDetailsFragment extends Fragment {

    private ArrayList<TodoTask> todoTasks = new ArrayList();
    private String todolistID;
    private String sectionID;
    private String sectionName;
    private String todolistName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TaskAdapter taskAdapter;
    private List<String> checkedEditRole = new ArrayList();
    private List<String> checkedMarkRole = new ArrayList();
    private List<String> roles = new ArrayList();
    private RoleCheckAdapter roleCheckEditAdapter;
    private RoleCheckAdapter roleCheckMarkAdapter;
    private List<String> allowedEdit = new ArrayList<>();
    private List<String> allowedMark = new ArrayList<>();
    private String userEmail;
    private String userRoleName;
    private boolean isOwner;
    private boolean isMarkAllowed;

    public SectionDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            todolistID = bundle.getString("todolistID");
            sectionID = bundle.getString("sectionID");
            todolistName = bundle.getString("todolistName");
            isOwner = bundle.getBoolean("owner");
        }
        SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        userEmail = prefs.getString("pref_email", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_section_details, container, false);
        setHasOptionsMenu(true);
        Button btnAdd = view.findViewById(R.id.btn_addNewTask);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allowedEdit.contains(userRoleName)) {
                    Toast.makeText(getActivity(), "You do not have permission to add new task in this section.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("todolistID", todolistID);
                bundle.putString("sectionID", sectionID);
                bundle.putString("sectionName", sectionName);
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_sectionDetailsFragment_to_addNewTaskFragment, bundle);
            }
        });

        taskAdapter = new TaskAdapter(todoTasks, isMarkAllowed);
        taskAdapter.setOnItemClickedListener(new OnActionClicked() {
            @Override
            public void onActionClicked(int position, String action) {
                if (action.equals("notAllowed")) {
                    Toast.makeText(getActivity(), "You do not have permission to mark tasks as completed in this section", Toast.LENGTH_SHORT).show();
                }
                if (action.equals("navigate")) {
                    String todoTasksID = todoTasks.get(position).getId();
                    Bundle bundle = new Bundle();
                    bundle.putString("todolistID", todolistID);
                    bundle.putString("sectionID", sectionID);
                    bundle.putString("todoTasksID", todoTasksID);
                    bundle.putString("sectionName", sectionName);
                    NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_sectionDetailsFragment_to_taskDetailsFragment, bundle);
                } else {
                    boolean isMark = action.equals("mark");
                    db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("TodoTasks").document(todoTasks.get(position).getId()).update("complete", isMark);
                    String strIsMark = isMark ? "MarkTask" : "UnmarkTask";
                    ChangesLog changesLog = new ChangesLog(Timestamp.now(), userEmail, strIsMark, todoTasks.get(position).getName(), sectionName);
                    db.collection("Todolists").document(todolistID).collection("ChangesLog").add(changesLog);
                }

            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycle_todoTasks);
        recyclerView.setAdapter(taskAdapter);
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
        allowedMark.clear();
        allowedEdit.clear();

        db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Section sec = documentSnapshot.toObject(Section.class);
                allowedMark.addAll(sec.getAllowedMark());
                allowedEdit.addAll(sec.getAllowedEdit());
                db.collection("Todolists").document(todolistID).collection("Roles").whereArrayContains("members", userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Role role = documentSnapshot.toObject(Role.class);
                            userRoleName = role.getRoleName();
                        }
                        isMarkAllowed = allowedMark.contains(userRoleName);
                        getActivity().invalidateOptionsMenu();
                    }
                });


                TextView txtSecTitle = view.findViewById(R.id.section_title);
                TextView txtTodolistName = view.findViewById(R.id.todolist_title);
                sectionName = sec.getName();
                txtSecTitle.setText(sectionName);
                txtTodolistName.setText(todolistName);

            }
        });

        todoTasks.clear();
        db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).collection("TodoTasks").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    TodoTask task = documentSnapshot.toObject(TodoTask.class);
                    todoTasks.add(task);
                }

                updateRecycleView();
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });

    }

    private void updateRecycleView() {
        TextView txt = getView().findViewById(R.id.empty_hint);
        if (todoTasks.isEmpty()) {
            txt.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.INVISIBLE);
        }
        taskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!isOwner)
            menu.clear();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.section_item_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_Section:
                checkedEditRole.clear();
                checkedMarkRole.clear();
                roles.clear();
                db.collection("Todolists").document(todolistID).collection("Roles").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Role role = documentSnapshot.toObject(Role.class);
                            roles.add(role.getRoleName());
                        }
                        db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                createEditSectionDialog();
                                Section section = documentSnapshot.toObject(Section.class);
                                //if (!section.getAllowedEdit().isEmpty())
                                checkedEditRole.addAll(section.getAllowedEdit());
                                //if (!section.getAllowedMark().isEmpty())
                                checkedMarkRole.addAll(section.getAllowedMark());
                                roleCheckEditAdapter.notifyDataSetChanged();
                                roleCheckMarkAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                break;
            case R.id.delete_Section:
                createDeleteConfirmationDialog();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void createDeleteConfirmationDialog() {
        //Declare variables
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        TextView dialogConfirmMsg;
        Button dialogConfirmBtn, dialogCancelBtn;
        dialogBuilder = new AlertDialog.Builder(getContext());

        //inflate views to layout
        final View confirmView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        dialogConfirmMsg = confirmView.findViewById(R.id.confirm_msg);
        dialogConfirmBtn = confirmView.findViewById(R.id.btnConfirm);
        dialogCancelBtn = confirmView.findViewById(R.id.btnCancel);


        //change dialog message
        dialogConfirmMsg.setText("Are you sure you want to delete this section?");

        //set view for dialog builder
        dialogBuilder.setView(confirmView);

        //create dialog
        dialog = dialogBuilder.create();

        //make dialog background transparent
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //set button listeners
        dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).delete();
                Toast.makeText(getActivity(), "Successfully deleted the section.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                SharedPreferences prefs = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
                String userName = prefs.getString("pref_username", null);
                ChangesLog changesLog = new ChangesLog(Timestamp.now(), userName, "DeleteSection", sectionName);
                db.collection("Todolists").document(todolistID).collection("ChangesLog").add(changesLog);
                requireActivity().onBackPressed();
            }
        });

        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //display dialog
        dialog.show();
    }

    private void createEditSectionDialog() {
        //Declare variables
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        TextView dialogMsg;
        EditText dialogSectionName;
        Button dialogAddBtn, dialogCancelBtn;
        RecyclerView dialogRecyclerViewEdit, dialogRecyclerViewMark;
        dialogBuilder = new AlertDialog.Builder(getContext());

        //inflate views to layout
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_section, null);
        dialogSectionName = dialogView.findViewById(R.id.dialog_input_field);
        dialogAddBtn = dialogView.findViewById(R.id.dialog_btnConfirm);
        dialogCancelBtn = dialogView.findViewById(R.id.dialog_btnCancel);
        dialogRecyclerViewEdit = dialogView.findViewById(R.id.recycler_roleListEdit);
        dialogRecyclerViewMark = dialogView.findViewById(R.id.recycler_roleListMark);
        dialogMsg = dialogView.findViewById(R.id.dialog_msg);

        dialogSectionName.setText(sectionName);
        dialogMsg.setText("Edit Section");
        //Link Adapter
        roleCheckEditAdapter = new RoleCheckAdapter(roles, checkedEditRole);
        roleCheckEditAdapter.setOnActionClickedListener(new OnActionClicked() {
            @Override
            public void onActionClicked(int position, String action) {
                if (action == "true")
                    checkedEditRole.add(roles.get(position));
                else
                    checkedEditRole.remove(roles.get(position));
            }
        });
        dialogRecyclerViewEdit.setAdapter(roleCheckEditAdapter);
        dialogRecyclerViewEdit.setLayoutManager(new LinearLayoutManager(getActivity()));

        roleCheckMarkAdapter = new RoleCheckAdapter(roles, checkedMarkRole);
        roleCheckMarkAdapter.setOnActionClickedListener(new OnActionClicked() {
            @Override
            public void onActionClicked(int position, String action) {
                if (action == "true")
                    checkedMarkRole.add(roles.get(position));
                else
                    checkedMarkRole.remove(roles.get(position));
            }
        });
        dialogRecyclerViewMark.setAdapter(roleCheckMarkAdapter);
        dialogRecyclerViewMark.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set view for dialog builder
        dialogBuilder.setView(dialogView);

        //create dialog
        dialog = dialogBuilder.create();

        //make dialog background transparent
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //set button listeners
        dialogAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sectionName = dialogSectionName.getText().toString();
                Section sec = new Section(sectionID, sectionName, checkedEditRole, checkedMarkRole);
                db.collection("Todolists").document(todolistID).collection("Sections").document(sectionID).set(sec).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Successfully update section details.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //display dialog
        dialog.show();
    }
}