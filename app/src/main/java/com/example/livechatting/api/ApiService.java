package com.example.livechatting.api;

import com.example.livechatting.data.Constants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST(Constants.URL + Constants.SIGN_IN)
    Call<ChargerList> signIn(
            @Field("id") String id,
            @Field("pw") String pw
    );

    @FormUrlEncoded
    @POST(Constants.URL + Constants.FRIENDS)
    Call<ChargerList> friends(
            @Field("userNum") String userNum
    );

    @FormUrlEncoded
    @POST(Constants.URL + Constants.ROOMS)
    Call<ChargerList> rooms(
            @Field("userNum") String userNum
    );

    @FormUrlEncoded
    @POST(Constants.URL + Constants.MESSAGES)
    Call<ChargerList> messages(
            @Field("roomNum") String roomNum,
            @Field("userNum") String userNum
    );

    @FormUrlEncoded
    @POST(Constants.URL + Constants.BROADCAST_LIST)
    Call<ChargerList> broadcast(
            @Field("userNum") String userNum
    );
}