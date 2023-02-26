package com.app.vekadelivery.Network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServices {

    @FormUrlEncoded
    @POST("deliveryboy")
    Call<JsonObject> deliveryBoyLogin(
      @Field("mobile") String mobile,
      @Field("password") String password
    );


    @FormUrlEncoded
    @POST("change_password_boy")
    Call<JsonObject> changePasword(
            @Field("boy_id") String customerid,
            @Field("npass") String newpassword,
            @Field("cpass") String cpass
    );



    @GET("demand?boy_id=")
    Call<JsonObject> getDeliveryUserData(
        @Query("boy_id") String boyid,
        @Query("page") String page,
        @Query("status") String deliver,
        @Query("shift") String shift
    );


    @GET("deliveryboy")
    Call<JsonObject> getBoyData(
      @Query("boy_id") String boyid
    );


    @FormUrlEncoded
    @POST("update_status")
    Call<JsonObject> updateStatus(
            @Field("id") String customerid,
            @Field("status") String status,
            @Field("ltr") String ltr
    );


    @GET("boy_notification?boy_id=")
    Call<JsonObject> getNotfication(
            @Query("boy_id") String boyid
    );


    @FormUrlEncoded
    @POST("add_notification")
    Call<JsonObject> addNotificatin(
            @Field("cus_id") String id,
            @Field("msg") String message,
            @Field("status") String status

    );




}
