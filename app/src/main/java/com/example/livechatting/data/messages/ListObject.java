package com.example.livechatting.data.messages;

public abstract class ListObject {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_CHAT_RIGHT = 1;
    public static final int TYPE_CHAT_LEFT = 2;

    public abstract int getType();
}