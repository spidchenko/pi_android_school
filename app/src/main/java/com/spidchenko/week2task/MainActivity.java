package com.spidchenko.week2task;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.spidchenko.week2task.models.Image;
import com.spidchenko.week2task.models.ImgSearchResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BASE_URL = "https://www.flickr.com/";
    private static final String API_KEY = "02692fb0a64b6b1b77f7f689c7f050c7";
    private static final String PIC_URL_TEMPLATE = "https://live.staticflickr.com/%s/%s_%s_%s.jpg"; ///{server-id}/{id}_{secret}_{size-suffix}.jpg
    private static final String PIC_SIZE_PARAM = "z"; //z = medium 640 Longest edge (px)
    private static final String BUNDLE_IMAGE_LINKS = "SAVED_IMAGE_LINKS";
    static final String EXTRA_URL = "com.spidchenko.week2task.extras.EXTRA_URL";

    // TODO: 11/23/20 [INFO] Read about Butterknife for simple view injections
    private TextView mTvImageLinks;
    private EditText mEtSearchQuery;

    // TODO: 11/23/20 Move request logic into separate class. Because this class will become some GOD class containing all the logic
    //  Move retrofit usage into repository OR some "requester" class. You can also start investigating MVVM or MVP architectures to simplify component separation.
    //  E.g. ViewModel/Presenter will interact with repository to fetch data the remote API.
    //  In this way your activity will only be responsible for displaying result.
    //  Try to keep activities/fragments as simple as possible.
    private Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvImageLinks = findViewById(R.id.tv_image_links);
        mEtSearchQuery = findViewById(R.id.et_search_query);

        //Restore text on recreate
        if (savedInstanceState != null) {
            mTvImageLinks.setText(savedInstanceState.getString(BUNDLE_IMAGE_LINKS));
        }

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_IMAGE_LINKS, mTvImageLinks.getText().toString());
    }

    @Override
    public void startActivity(Intent intent) {
        boolean handled = false;

        //Check url click intent
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            String app_id = intent.getStringExtra(Browser.EXTRA_APPLICATION_ID);
            if (getApplicationContext().getPackageName().equals(app_id)) {
                Intent newIntent = new Intent(this, ImageViewerActivity.class);
                newIntent.putExtra(EXTRA_URL, intent.getData().toString());
                super.startActivity(newIntent);
                handled = true;
            }
        }
        if (!handled) {
            super.startActivity(intent);
        }
    }

    public void searchImages(View view) {
        mTvImageLinks.setText(R.string.msg_wait);

        //Close soft keyboard
        mEtSearchQuery.setEnabled(false);
        mEtSearchQuery.setEnabled(true);

        JsonPlaceHolderApi jsonPlaceHolderApi = mRetrofit.create(JsonPlaceHolderApi.class);
        Call<ImgSearchResult> call = jsonPlaceHolderApi.searchImages(API_KEY,
                mEtSearchQuery.getText().toString());

        call.enqueue(new Callback<ImgSearchResult>() {
            @Override
            public void onResponse(Call<ImgSearchResult> call, Response<ImgSearchResult> response) {

                if (!response.isSuccessful()) {
                    mTvImageLinks.setText(String.format("%s: %s", getString(R.string.error_text), response.code()));
                    return;
                }

                if (response.body() != null) {
                    List<Image> imageList = response.body().getImageContainer().getImage();

                    if (!imageList.isEmpty()) {
                        mTvImageLinks.setText("");
                        for (Image image : imageList) {
                            mTvImageLinks.append(
                                    String.format(PIC_URL_TEMPLATE,
                                            image.getServer(),
                                            image.getId(),
                                            image.getSecret(),
                                            PIC_SIZE_PARAM) + "\n"
                            );
                        }
                    } else {
                        mTvImageLinks.setText(R.string.error_nothing_found);
                    }
                }
            }

            @Override
            public void onFailure(Call<ImgSearchResult> call, Throwable t) {
                mTvImageLinks.setText(t.getMessage());
            }
        });
    }
}