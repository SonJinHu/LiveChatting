package com.example.livechatting.function;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Time {
    // 채팅방 목록에 표시하는 마지막 메시지 시간을 현재 시간과 비교하여 다르게 표현
    public static String convertLastMsgTime(String time) {
        SimpleDateFormat sdf_year = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat sdf_year_date = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        SimpleDateFormat sdf_date = new SimpleDateFormat("M월 d일", Locale.getDefault());
        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm", Locale.getDefault());

        Date message = null;
        Date current = Calendar.getInstance().getTime();
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            message = inputFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert message != null;
        String messageYear = sdf_year.format(message);
        String currentYear = sdf_year.format(current);
        if (messageYear.equals(currentYear)) {
            String messageDate = sdf_date.format(message);
            String currentDate = sdf_date.format(current);
            if (messageDate.equals(currentDate)) {
                return sdf_time.format(message);
            } else {
                return sdf_date.format(message);
            }
        } else {
            return sdf_year_date.format(message);
        }
    }

    // 채팅방에 있는 채팅메시지에 표시되는 시간
    // HH:mm 표현
    public static String convertMsgTime(String time) {
        String convertTime = null;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(time);
            if (date != null)
                convertTime = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertTime;
    }
}
