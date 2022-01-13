package com.iqbal.codeku.retrofitapi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    ImageView iv;
    Button btnChange, btnAddFav,btnChooseFile, btnUpload;
    String image_id = "";
    String imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.cat_image);
        btnChange = findViewById(R.id.btn_chg);
        btnAddFav = findViewById(R.id.fav_btn);
        btnChooseFile = findViewById(R.id.btn_chs);
        btnUpload = findViewById(R.id.btn_upl);

        btnChooseFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 0);
        });

        getImage();
        Log.e("id", image_id);

        btnChange.setOnClickListener(v -> {
            getImage();
        });

        btnUpload.setOnClickListener(v -> {
            uploadImages();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(data == null) {
                Toast.makeText(this, "Unable to choose image", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri imageUrl = data.getData();
            imagePath = getRealPathFromUri(imageUrl);
        }
    }

    public String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        iv.setImageBitmap(BitmapFactory.decodeFile(result));
        cursor.close();

        return result;
    }

    public void getImage() {
        MyAPICall myAPICall = getAPIClient().create(MyAPICall.class);
        Call<List<DataModel>> call = myAPICall.getData();
        call.enqueue(new Callback<List<DataModel>>() {
            @Override
            public void onResponse(Call<List<DataModel>> call, Response<List<DataModel>> response) {
                if(!response.isSuccessful()) {
                    String message = "Gagal menkoneksikan API";
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<DataModel> ambilData = response.body();
                Glide.with(MainActivity.this).load(ambilData.get(0).getUrl()).into(iv);

                btnAddFav.setOnClickListener(v -> {
                    addFavorite(response.body().get(0).getId(), "");
                });
            }

            @Override
            public void onFailure(Call<List<DataModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addFavorite(String id, String subId) {
        MyAPICall myAPICall = getAPIClient().create(MyAPICall.class);
        Call<DataModelResponse> call = myAPICall.postFavorite(id,subId);
        call.enqueue(new Callback<DataModelResponse>() {
            @Override
            public void onResponse(Call<DataModelResponse> call, Response<DataModelResponse> response) {
                Log.e("Hasil", "Hasil Respon: " + response.body());
                Toast.makeText(MainActivity.this,"Message: berhasil", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<DataModelResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal Menambahkan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadImages() {
        File file = new File(imagePath);
        UploadAPI uploadAPI = NetworkClient.getRetrofit().create(UploadAPI.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =  MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        Call<FileInfo> call = uploadAPI.uploadImage(body);

        call.enqueue(new Callback<FileInfo>() {
            @Override
            public void onResponse(Call<FileInfo> call, Response<FileInfo> response) {
                Log.e("TAG", String.valueOf(response.code()));

                if(response.code() == 400) {
                    Toast.makeText(MainActivity.this, "Your input is not cat", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(response.code() == 201) {
                    Toast.makeText(MainActivity.this, "Image upload successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FileInfo> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Retrofit getAPIClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.thecatapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}