package com.example.todostaskmanagementsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todostaskmanagementsystem.adapter.SectionAdapter;
import com.example.todostaskmanagementsystem.interfaces.OnItemClicked;
import com.example.todostaskmanagementsystem.model.Section;
import com.example.todostaskmanagementsystem.model.TodoTask;
import com.example.todostaskmanagementsystem.model.Todolist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class TodoListDetailsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String todolistID;
    private String todolistName;
    private String quickAccessPos;
    private SectionAdapter sectionAdapter;
    private ArrayList<Section> sections = new ArrayList();


    public TodoListDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            todolistID = bundle.getString("todolistID");
            quickAccessPos = bundle.getString("quickAccess_pos");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todo_list_details, container, false);
        setHasOptionsMenu(true);
        //Setup Add Section Button
        Button btnAdd = view.findViewById(R.id.btn_addSection);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddSectionDialog();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycle_sections);
        sectionAdapter = new SectionAdapter(sections);
        sectionAdapter.setOnItemClickedListener(new OnItemClicked() {
            @Override
            public void onItemClicked(int position) {
                String sectionID = sections.get(position).getId();
                Bundle bundle = new Bundle();
                bundle.putString("todolistID", todolistID);
                bundle.putString("sectionID", sectionID);
                bundle.putString("sectionName", sections.get(position).getName());
                bundle.putString("todolistName", todolistName);

                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_todoListDetailsFragment_to_sectionDetailsFragment, bundle);
            }
        });
        recyclerView.setAdapter(sectionAdapter);
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
                loadData(view);
                pullToRefresh.setRefreshing(false);
            }
        });

    }

    private void loadData(View view) {
        //Get and Set Todolist Details
        db.collection("Todolists").document(todolistID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Todolist todolist = documentSnapshot.toObject(Todolist.class);
                    TextView txtViewTitle = view.findViewById(R.id.todolist_title);
                    TextView txtViewDesc = view.findViewById(R.id.todolist_desc);

                    txtViewTitle.setText(todolist.getName());
                    todolistName = todolist.getName();
                    txtViewDesc.setText(todolist.getDesc());
                    updateRecentAccess();
                } else {
                    SharedPreferences prefs = getActivity().getSharedPreferences("recent_accessed", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    switch(quickAccessPos){
                        case "1":
                            editor.putString("recentOne", null);break;
                        case "2":
                            editor.putString("recentTwo", null);break;
                        case "3":
                            editor.putString("recentThree", null);break;
                        default:
                    }
                    editor.commit();
                    Toast.makeText(getActivity(),"The requested todolist is no longer exists.",Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                    return;
                }
            }
        });
        sections.clear();
        db.collection("Todolists").document(todolistID).collection("Sections").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Section section = documentSnapshot.toObject(Section.class);
                    sections.add(section);
                }
                updateRecycleView();
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });
    }

    private void updateRecycleView() {

        TextView txt = getView().findViewById(R.id.empty_hint);
        if (sections.isEmpty()) {
            txt.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.INVISIBLE);
        }
        sectionAdapter.notifyDataSetChanged();
    }

    private void createAddSectionDialog() {
        //Declare variables
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        TextView dialogMsg;
        EditText dialogSectionName;
        Button dialogAddBtn, dialogCancelBtn;
        dialogBuilder = new AlertDialog.Builder(getContext());

        //inflate views to layout
        final View addSectionView = getLayoutInflater().inflate(R.layout.dialog_single_input, null);
        dialogSectionName = addSectionView.findViewById(R.id.dialog_input_field);
        dialogAddBtn = addSectionView.findViewById(R.id.dialog_btnConfirm);
        dialogCancelBtn = addSectionView.findViewById(R.id.dialog_btnCancel);
        dialogMsg = addSectionView.findViewById(R.id.dialog_msg);

        //change dialog message
        dialogMsg.setText("Enter new section name:");

        //set view for dialog builder
        dialogBuilder.setView(addSectionView);

        //create dialog
        dialog = dialogBuilder.create();

        //make dialog background transparent
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //set button listeners
        dialogAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sectionName = dialogSectionName.getText().toString();
                db.collection("Todolists").document(todolistID).collection("Data").document("Data").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int currSectionID = Integer.parseInt(documentSnapshot.get("currSectionID").toString()) + 1;
                        String secID = "S" + currSectionID;
                        Section sec = new Section(secID, sectionName);
                        sections.add(sec);
                        db.collection("Todolists").document(todolistID).collection("Sections").document(secID).set(sec);
                        db.collection("Todolists").document(todolistID).collection("Data").document("Data").update("currSectionID", currSectionID);
                        updateRecycleView();
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
        dialogConfirmMsg.setText("Are you sure you want to delete this todolist?");

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
                db.collection("Todolists").document(todolistID).delete();
                dialog.dismiss();
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

    private void createEditDialog() {
        //Declare variables
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        TextView dialogMsg;
        EditText dialogEditName;
        EditText dialogEditDesc;
        Button dialogConfirmBtn, dialogCancelBtn;
        dialogBuilder = new AlertDialog.Builder(getContext());

        //inflate views to layout
        final View editView = getLayoutInflater().inflate(R.layout.dialog_edit_todolist, null);
        dialogEditName = editView.findViewById(R.id.dialog_input_title);
        dialogEditDesc = editView.findViewById(R.id.dialog_input_desc);
        dialogConfirmBtn = editView.findViewById(R.id.dialog_btnConfirm);
        dialogCancelBtn = editView.findViewById(R.id.dialog_btnCancel);

        //set view for dialog builder
        dialogBuilder.setView(editView);
        //create dialog
        dialog = dialogBuilder.create();
        //make dialog background transparent
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //set button listeners
        dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editName = dialogEditName.getText().toString();
                String editDesc = dialogEditDesc.getText().toString();
                Map<String, Object> data = new HashMap<>();
                data.put("name", editName);
                data.put("desc", editDesc);
                db.collection("Todolists").document(todolistID).update(data);
                Toast.makeText(getActivity(), "Update successfully", Toast.LENGTH_SHORT);
                dialog.dismiss();
                loadData(getView());
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

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.todolist_item_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        Bundle bundle = new Bundle();
        bundle.putString("todolistID", todolistID);
        switch (item.getItemId()) {
            case R.id.edit_todolist:
                createEditDialog();
                break;
            case R.id.manage_member:
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_todoListDetailsFragment_to_manageMemberFragment, bundle);
                break;
            case R.id.manage_role:
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_todoListDetailsFragment_to_manageRoleFragment, bundle);
                break;
            case R.id.view_changes:
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_todoListDetailsFragment_to_changesLogFragment, bundle);
                break;
            case R.id.delete_todolist:
                //delete todolist
                createDeleteConfirmationDialog();
                break;
            default:
        }


        return super.onOptionsItemSelected(item);
    }


    public void updateRecentAccess() {
        SharedPreferences prefs = getActivity().getSharedPreferences("recent_accessed", Context.MODE_PRIVATE);
        String recentOne = prefs.getString("recentOne", null);
        String recentTwo = prefs.getString("recentTwo", null);
        String recentThree = prefs.getString("recentThree", null);
        String recentOneTitle = prefs.getString("recentOneTitle", null);
        String recentTwoTitle = prefs.getString("recentTwoTitle", null);
        String recentThreeTitle = prefs.getString("recentThreeTitle", null);
        Todolist todolistOne = new Todolist(recentOne, recentOneTitle);
        Todolist todolistTwo = new Todolist(recentTwo, recentTwoTitle);
        Todolist todolistThree = new Todolist(recentThree, recentThreeTitle);
        Todolist newTodolist = new Todolist(todolistID, todolistName);

        Stack<Todolist> stack = new Stack<Todolist>();
        stack.push(todolistThree);
        stack.push(todolistTwo);
        stack.push(todolistOne);
        int index = -1;
        for (int i = 0; i < stack.size(); i++) {
            if (stack.get(i).getId() != null) {
                if (stack.get(i).getId().equals(todolistID)) {
                    index = i;
                }
            }
        }
        if (index != -1)
            stack.removeElementAt(index);
        stack.push(newTodolist);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("recentOne", stack.get(stack.size() - 1).getId());
        editor.putString("recentTwo", stack.get(stack.size() - 2).getId());
        editor.putString("recentThree", stack.get(stack.size() - 3).getId());
        editor.putString("recentOneTitle", stack.get(stack.size() - 1).getName());
        editor.putString("recentTwoTitle", stack.get(stack.size() - 2).getName());
        editor.putString("recentThreeTitle", stack.get(stack.size() - 3).getName());
        editor.commit();
    }
}