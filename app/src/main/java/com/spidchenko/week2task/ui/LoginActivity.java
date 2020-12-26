package com.spidchenko.week2task.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.SharedPreferencesRepository;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.FlickrRoomDatabase;
import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.User;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity.LOG_TAG";

    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private UserDao mUserDao;
    private String mUsername;
    private SharedPreferencesRepository mSharedPreferences;
    private TextInputLayout mTlUsername;
    private EditText mEtUsername;
    private Button mBtnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPreferences = SharedPreferencesRepository.init(this);
        mEtUsername = findViewById(R.id.username);
        mTlUsername = findViewById(R.id.username_input_layout);
        mBtnSignIn = findViewById(R.id.btn_sign_in);
        FlickrRoomDatabase mDb = FlickrRoomDatabase.getDatabase(this);
        mUserDao = mDb.userDao();

        mEtUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                actionSignIn(null);
                return true;
            }
            return false;
        });
    }

    public void actionSignIn(View view) {

        mUsername = mEtUsername.getText().toString().trim();
        if (!isLoginValid(mUsername)) {
            mTlUsername.setError(getString(R.string.login_failed));
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

    private boolean isLoginValid(@Nullable String text) {
        return text != null && text.length() > 0;
    }

}