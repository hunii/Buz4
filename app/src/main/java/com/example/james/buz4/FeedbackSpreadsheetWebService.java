package com.example.james.buz4;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by Hailiey Lee on 2016-10-15.
 */

public interface FeedbackSpreadsheetWebService {
    @POST("1FAIpQLSeauWZ-Mh4EbPfw-0RgcGcoDbjCoJiHYLgySrKdja-mRTuy7g/formResponse")
    @FormUrlEncoded
    Call<Void> completeFeedback(
            @Field("entry.665154783") String name,
            @Field("entry.822762615") String userFeedback
    );

}
