package com.spidchenko.week2task.ui;

import android.content.Intent;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment.LOG_TAG";

    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private UserDao mUserDao;
    private String mUsername;
    private SharedPreferencesRepository mSharedPreferences;
    private TextInputLayout mTlUsername;
    private EditText mEtUsername;
    private Button mBtnSignIn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
//                        startActivity(new Intent(this, MainActivity.class));
//                        finish();
                    });

                }).start();
            }
        });

        mEtUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
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

}