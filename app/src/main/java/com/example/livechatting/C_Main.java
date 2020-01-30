package com.example.livechatting;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class C_Main extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_main);

        BottomNavigationView navView = findViewById(R.id.c_navigation);
        NavController navController = Navigation.findNavController(this, R.id.c_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }
}