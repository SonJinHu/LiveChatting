package com.example.livechatting.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.livechatting.R;

public class ChatFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.c_fragment_chat, container, false);
        ChatViewModel chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.getText().observe(getViewLifecycleOwner(), s -> {
            TextView textView = root.findViewById(R.id.text_home);
            textView.setText(s);
        });
        return root;
    }
}