package com.example.livechatting.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Charger {
    public class SignIn {
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("num")
        @Expose
        private String num;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("nick")
        @Expose
        private String nick;
        @SerializedName("img")
        @Expose
        private String img;

        public String getNum() {
            return num;
        }

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

    public class Friends {
        @SerializedName("num")
        @Expose
        private String num;
        @SerializedName("nick")
        @Expose
        private String nick;
        @SerializedName("img")
        @Expose
        private String img;

        public String getNum() {
            return num;
        }

        public String getNick() {
            return nick;
        }

        public String getImg() {
            return img;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public class Rooms {
        @SerializedName("roomNum")
        @Expose
        private String roomNum;
        @SerializedName("roomName")
        @Expose
        private String roomName;
        @SerializedName("userCount")
        @Expose
        private String userCount;
        @SerializedName("notReadMsgCount")
        @Expose
        private String notReadMsgCount;
        @SerializedName("lastMsg")
        @Expose
        private String lastMsg;
        @SerializedName("lastMsgTime")
        @Expose
        private String lastMsgTime;

        public String getRoomNum() {
            return roomNum;
        }

        public String getRoomName() {
            return roomName;
        }

        public String getUserCount() {
            return userCount;
        }

        public String getNotReadMsgCount() {
            return notReadMsgCount;
        }

        public String getLastMsg() {
            return lastMsg;
        }

        public String getLastMsgTime() {
            return lastMsgTime;
        }
    }

    public class Messages {
        @SerializedName("countToRead")
        @Expose
        private String countToRead;
        @SerializedName("userNum")
        @Expose
        private String userNum;
        @SerializedName("userNick")
        @Expose
        private String userNick;
        @SerializedName("userImg")
        @Expose
        private String userImg;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("msgTime")
        @Expose
        private String msgTime;

        public String getCountToRead() {
            return countToRead;
        }

        public String getUserNum() {
            return userNum;
        }

        public String getUserNick() {
            return userNick;
        }

        public String getUserImg() {
            return userImg;
        }

        public String getMessage() {
            return message;
        }

        public String getMsgTime() {
            return msgTime;
        }
    }

    public class Broadcast {
        @SerializedName("num")
        @Expose
        private String num;
        @SerializedName("nick")
        @Expose
        private String nick;
        @SerializedName("img")
        @Expose
        private String img;
        @SerializedName("rtmp")
        @Expose
        private String rtmp;

        public String getNum() {
            return num;
        }

        public String getNick() {
            return nick;
        }

        public String getImg() {
            return img;
        }

        public String getRtmp() {
            return rtmp;
        }
    }
}
