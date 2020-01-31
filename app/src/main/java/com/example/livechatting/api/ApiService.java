package com.example.livechatting.api;

import com.example.livechatting.data.Constant;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST(Constant.URL + Constant.SIGN_IN)
    Call<ChargerList> signIn(
            @Field("id") String id,
            @Field("pw") String pw
    );

    @FormUrlEncoded
    @POST(Constant.URL + Constant.FRIENDS)
    Call<ChargerList> friends(
            @Field("userNum") String userNum
    );

    @FormUrlEncoded
    @POST(Constant.URL + Constant.ROOMS)
    Call<ChargerList> rooms(
            @Field("userNum") String userNum
    );

    @FormUrlEncoded
    @POST(Constant.URL + Constant.MESSAGES)
    Call<ChargerList> messages(
            @Field("roomNum") String roomNum
    );
}