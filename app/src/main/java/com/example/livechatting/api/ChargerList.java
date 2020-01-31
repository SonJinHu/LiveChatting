package com.example.livechatting.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ChargerList {
    @SerializedName("sign_in")
    private ArrayList<Charger.SignIn> signIn;

    public ArrayList<Charger.SignIn> getSignIn() {
        return signIn;
    }

    @SerializedName("friends")
    private ArrayList<Charger.Friends> friends;

    public ArrayList<Charger.Friends> getFriends() {
        return friends;
    }

    @SerializedName("rooms")
    private ArrayList<Charger.Rooms> rooms;

    public ArrayList<Charger.Rooms> getRooms() {
        return rooms;
    }

    @SerializedName("messages")
    private ArrayList<Charger.Messages> messages;

    public ArrayList<Charger.Messages> getMessages() {
        return messages;
    }
}