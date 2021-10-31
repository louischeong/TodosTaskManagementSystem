package com.example.todostaskmanagementsystem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment implements View.OnClickListener{

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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mytodolist:
                NavHostFragment.findNavController(getParentFragment()).navigate(HomeFragmentDirections.actionHomeFragmentToMyTodolistsFragment());
                break;
            case R.id.btn_myprof:
                NavHostFragment.findNavController(getParentFragment()).navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment()); //To change
                break;
            default:
                break;
        }
    }
}