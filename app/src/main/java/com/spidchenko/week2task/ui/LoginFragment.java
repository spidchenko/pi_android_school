package com.spidchenko.week2task.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.helpers.ViewModelsFactory;
import com.spidchenko.week2task.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment.LOG_TAG";

    private OnFragmentInteractionListener mListener;
    private TextInputLayout mTlUsername;
    private EditText mEtUsername;
    private Button mBtnSignIn;
    private LoginViewModel mViewModel;
    private NavController mNavController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelsFactory factory = new ViewModelsFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(requireActivity(), factory).get(LoginViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + getResources().getString(R.string.exception_message));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getIsLoggedIn().observe(requireActivity(), isLoggedIn -> {
            if (isLoggedIn) {
                int startDestination = mNavController.getGraph().getStartDestination();
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build();
                mNavController.navigate(startDestination, null, navOptions);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mEtUsername = rootView.findViewById(R.id.username);
        mTlUsername = rootView.findViewById(R.id.username_input_layout);
        mBtnSignIn = rootView.findViewById(R.id.btn_sign_in);

        mNavController = NavHostFragment.findNavController(this);

        // Sign in action
        mBtnSignIn.setOnClickListener(view -> {
            mListener.hideKeyboard();
            String userName = mEtUsername.getText().toString().trim();
            if (!isLoginValid(userName)) {
                mTlUsername.setError(getString(R.string.login_failed));
            } else {
                mViewModel.logIn(userName);
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
//        void onLogIn();

        void hideKeyboard();
    }
}