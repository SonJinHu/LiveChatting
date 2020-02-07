package com.example.livechatting;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.livechatting.function.ServiceBind;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class C_Main extends ServiceBind {

    private boolean exit = false;

    @Override
    public void receiveMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_main);

        Toolbar toolbar = findViewById(R.id.c_toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView nav = findViewById(R.id.c_bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.c_fragment);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            switch (id) {
                case R.id.navigation_friends:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("친구");
                    break;
                case R.id.navigation_chat:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("채팅");
                    break;
                case R.id.navigation_broadcast:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("방송보기");
                    break;
            }
        });
        NavigationUI.setupWithNavController(nav, navController);
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "'뒤로' 버튼 한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(() -> exit = false, 3000);
        }
    }
}