package com.example.livechatting.data.messages;

import com.example.livechatting.api.Charger;
import com.example.livechatting.data.UserInfo;

public class MessagesObject extends ListObject {

    private Charger.Messages item;

    public Charger.Messages getMessages() {
        return item;
    }

    public void setMessages(Charger.Messages item) {
        this.item = item;
    }

    @Override
    public int getType() {
        if (item.getUserNum().equals(UserInfo.num)) {
            return TYPE_CHAT_RIGHT;
        } else
            return TYPE_CHAT_LEFT;
    }
}