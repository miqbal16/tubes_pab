package com.iqbal.codeku.retrofitapi;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadAPI {
    @Multipart
    @POST("images/upload?api_key=744c3b1b-855e-4df2-b486-fdf3ac51cd5f")
    Call<FileInfo> uploadImage(@Part MultipartBody.Part file);
}
