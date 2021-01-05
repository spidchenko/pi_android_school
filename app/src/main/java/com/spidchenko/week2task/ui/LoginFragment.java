package com.spidchenko.week2task.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.SharedPreferencesRepository;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.FlickrRoomDatabase;
import com.spidchenko.week2task.db.dao.UserDao;
import com.spidchenko.week2task.db.models.User;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment.LOG_TAG";

    OnFragmentInteractionListener mListener;

    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private UserDao mUserDao;
    private String mUsername;
    private SharedPreferencesRepository mSharedPreferences;
    private TextInputLayout mTlUsername;
    private EditText mEtUsername;
    private Button mBtnSignIn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + getResources().getString(R.string.exception_message));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mSharedPreferences = SharedPreferencesRepository.init(requireContext());
        mEtUsername = rootView.findViewById(R.id.username);
        mTlUsername = rootView.findViewById(R.id.username_input_layout);
        mBtnSignIn = rootView.findViewById(R.id.btn_sign_in);
        FlickrRoomDatabase mDb = FlickrRoomDatabase.getDatabase(requireContext());
        mUserDao = mDb.userDao();

        // Sign in action
        mBtnSignIn.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).hideKeyboard();
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
                        mListener.onLogIn();
//                        startActivity(new Intent(this, MainActivity.class));
//                        finish();
                    });

                }).start();
            }
        });

        mEtUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d(TAG, "onCreateView: IME_ACTION_DONE");
                mBtnSignIn.callOnClick();
                return true;
            }
            return false;
        });

        return rootView;
    }


    private boolean isLoginValid(@Nullable String text) {
        return text != null && text.length() > 0;
    }


    interface OnFragmentInteractionListener {
        void onLogIn();
    }

}