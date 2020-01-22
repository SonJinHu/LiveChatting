package com.example.livechatting.api;

import com.example.livechatting.data.Constant;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST(Constant.URL + Constant.CHECK_ID)
    Call<ChargerList> checkId(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST(Constant.URL + Constant.CHECK_NICK)
    Call<ChargerList> checkNick(
            @Field("nick") String nick
    );

    @FormUrlEncoded
    @POST(Constant.URL + Constant.SIGN_IN)
    Call<ChargerList> signIn(
            @Field("id") String id,
            @Field("pw") String pw
    );
}