package com.example.livechatting.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Charger {
    public class Server {
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("nick")
        @Expose
        private String nick;
        @SerializedName("img")
        @Expose
        private String img;

        public String getStatus() {
            return status;
        }

        public String getId() {
            return id;
        }

        public String getNick() {
            return nick;
        }

        public String getImg() {
            return img;
        }
    }
}
