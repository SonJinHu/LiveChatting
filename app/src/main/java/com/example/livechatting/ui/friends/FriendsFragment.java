package com.example.livechatting.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.livechatting.R;

public class FriendsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.c_fragment_friends, container, false);
        FriendsViewModel FriendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        FriendsViewModel.getText().observe(getViewLifecycleOwner(), s -> {
            TextView textView = root.findViewById(R.id.text_dashboard);
            textView.setText(s);
        });
        return root;
    }
}