package com.example.james.buz4;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * This interface is used to post and call to google spreadsheet/form api.
 * Also has google form's ID so that the feedback can be written in the exact form.
 * Created by Jiwon Lee on 2016-10-15.
 */

public interface FeedbackSpreadsheetWebService {
    @POST("1FAIpQLSeauWZ-Mh4EbPfw-0RgcGcoDbjCoJiHYLgySrKdja-mRTuy7g/formResponse")
    @FormUrlEncoded
    Call<Void> completeFeedback(
            // The ID of the 'Name' field in the google form
            @Field("entry.665154783") String name,
            // The ID of the 'Content' field in the google form
            @Field("entry.822762615") String userFeedback
    );

}
