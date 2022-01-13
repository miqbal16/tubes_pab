package com.iqbal.codeku.retrofitapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MyAPICall {
    @GET("images/search")
    Call<List<DataModel>> getData();

    @FormUrlEncoded
    @POST("favourites")
    @Headers({"Accept:application/json", "Content-Type:application/json", "Authorization: Bearer 4dbad6e3-1e22-4565-be62-a3ac2bf21305"})
    Call<DataModelResponse> postFavorite(@Field("image_id") String image_id, @Field("sub_id") String sub_id);

}
