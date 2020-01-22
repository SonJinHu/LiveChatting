package com.example.livechatting.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ChargerList {
    @SerializedName("sign_in")
    private ArrayList<Charger.Server> signIn;

    public ArrayList<Charger.Server> getSignIn() {
        return signIn;
    }
}