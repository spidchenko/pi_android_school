package com.spidchenko.week2task.ui

import android.content.Context
import com.google.android.material.textfield.TextInputLayout
import android.widget.EditText
import com.spidchenko.week2task.viewmodel.LoginViewModel
import androidx.navigation.NavController
import androidx.lifecycle.SavedStateHandle
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.spidchenko.week2task.helpers.ViewModelsFactory
import androidx.lifecycle.ViewModelProvider
import com.spidchenko.week2task.R
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavOptions
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import android.widget.TextView
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import java.lang.ClassCastException
import java.util.*

class LoginFragment : Fragment() {
    private var mListener: OnFragmentInteractionListener? = null
    private var mTlUsername: TextInputLayout? = null
    private var mEtUsername: EditText? = null
    private var mBtnSignIn: Button? = null
    private var mViewModel: LoginViewModel? = null
    private var mNavController: NavController? = null
    private var mSavedStateHandle: SavedStateHandle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ViewModelsFactory(requireActivity().application)
        mViewModel = ViewModelProvider(requireActivity(), factory).get(
            LoginViewModel::class.java
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw ClassCastException(
                context.toString()
                        + resources.getString(R.string.exception_message)
            )
        }
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: ")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSavedStateHandle = Objects.requireNonNull(
            Navigation.findNavController(view)
                .previousBackStackEntry
        )
            ?.savedStateHandle
        mSavedStateHandle!!.set(LOGIN_SUCCESSFUL, false)
        mViewModel!!.isLoggedIn.observe(requireActivity(), { isLoggedIn: Boolean ->
            if (isLoggedIn) {
                mSavedStateHandle!!.set(LOGIN_SUCCESSFUL, true)
                val startDestination = mNavController!!.graph.startDestination
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(startDestination, true)
                    .build()
                mNavController!!.navigate(startDestination, null, navOptions)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        mEtUsername = rootView.findViewById(R.id.username)
        mTlUsername = rootView.findViewById(R.id.username_input_layout)
        mBtnSignIn = rootView.findViewById(R.id.btn_sign_in)
        mNavController = NavHostFragment.findNavController(this)

        // Sign in action
        mBtnSignIn?.setOnClickListener {
            mListener!!.hideKeyboard()
            val userName = mEtUsername?.text.toString().trim { it <= ' ' }
            if (!isLoginValid(userName)) {
                mTlUsername?.error = getString(R.string.login_failed)
            } else {
                mViewModel!!.logIn(userName)
            }
        }
        mEtUsername?.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d(TAG, "onCreateView: IME_ACTION_DONE")
                mBtnSignIn?.callOnClick()
                return@setOnEditorActionListener true
            }
            false
        }
        return rootView
    }

    private fun isLoginValid(text: String?): Boolean {
        return text != null && text.isNotEmpty()
    }

    internal interface OnFragmentInteractionListener {
        fun hideKeyboard()
    }

    companion object {
        private const val TAG = "LoginFragment.LOG_TAG"
        const val LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL"
    }
}