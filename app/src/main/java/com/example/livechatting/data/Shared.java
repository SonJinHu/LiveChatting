package com.example.livechatting.data;

import android.content.Context;
import android.content.SharedPreferences;

public class Shared {
    public static void setIsAuto(Context context, boolean isAuto) {
        SharedPreferences pref = context.getSharedPreferences("Auto", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("auto", isAuto);
        editor.apply();
    }

    public static boolean getIsAuto(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Auto", Context.MODE_PRIVATE);
        return (pref != null && pref.getBoolean("auto", false));
    }
}
