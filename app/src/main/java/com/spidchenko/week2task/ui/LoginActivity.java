package com.spidchenko.week2task.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.SharedPreferencesRepository;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.FlickrRoomDatabase;
import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.User;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity.LOG_TAG";

    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    //    private DatabaseHelper mDb;
    private UserDao mUserDao;
    private String mUsername;
    private SharedPreferencesRepository mSharedPreferences;
    private EditText mEtUsername;
    private Button mBtnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPreferences = SharedPreferencesRepository.init(this);
        mEtUsername = findViewById(R.id.username);
        mBtnSignIn = findViewById(R.id.btn_sign_in);
        FlickrRoomDatabase mDb = FlickrRoomDatabase.getDatabase(this);
        mUserDao = mDb.userDao();
    }

    public void actionSignIn(View view) {

        mUsername = mEtUsername.getText().toString().trim();
        if (mUsername.isEmpty()) {
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
        } else {

            mBtnSignIn.setClickable(false);

            new Thread(() -> {
                Log.d(TAG, "actionSignIn: on Worker Thread." + Thread.currentThread().getName());
                User user = mUserDao.getUser(mUsername);
                if (user == null) {
                    mUserDao.addUser(new User(mUsername));
                    user = mUserDao.getUser(mUsername);
                }
                CurrentUser currentUser = CurrentUser.getInstance();
                currentUser.setUser(user);

                mUiHandler.post(() -> {
                    Log.d(TAG, "actionSignIn: on UI Thread." + Thread.currentThread().getName());
                    mSharedPreferences.saveLogin(mUsername);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });

            }).start();
        }
    }
}