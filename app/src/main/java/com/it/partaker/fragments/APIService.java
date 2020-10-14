package com.it.partaker.fragments;

import com.it.partaker.notifications.MyResponse;
import com.it.partaker.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key = AAAA_F3JjEQ:APA91bF-HSz2H5m4LgCytbcwh8eJovpjMco1CFaGExq-Q08p7g9NZ2cmYueVOJD362hSZFFH6FM20mBbK0AeiYkW88kDDBOrdAr5F3li6VzVlrgdEs3gqn2cDqyDFl-XdoLGrdoTk85-"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
