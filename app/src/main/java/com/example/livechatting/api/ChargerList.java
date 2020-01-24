package com.example.livechatting.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ChargerList {
    @SerializedName("sign_in")
    private ArrayList<Charger.SignIn> signIn;

    public ArrayList<Charger.SignIn> getSignIn() {
        return signIn;
    }
}