package com.spidchenko.week2task.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.viewmodel.ImageViewerActivityViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageViewerFragment extends Fragment {

    private static final String TAG = "ImgViewFragment.LOG_TAG";

    private String mIntentExtraUrl;
    private Favourite mFavourite;
    private ImageViewerActivityViewModel mViewModel;
    private CheckBox cbToggleFavourite;

    ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d(TAG, "Permission callback! = " + isGranted);
                if (isGranted) {
                    mViewModel.saveImage(Glide.with(requireContext().getApplicationContext()),
                            requireContext().getApplicationContext().getContentResolver(),
                            mFavourite);
                } else {
                    ((MainActivity) requireActivity()).showSnackBarMessage(R.string.need_storage_permission);
                }
            });

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ImageViewerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageViewerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageViewerFragment newInstance(String param1, String param2) {
        ImageViewerFragment fragment = new ImageViewerFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false);

        CurrentUser currentUser = CurrentUser.getInstance();


//        Intent intent = getIntent();
//        mIntentExtraUrl = intent.getStringExtra(EXTRA_URL);
        mIntentExtraUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/RUEDA_DE_PRENSA_CONJUNTA_ENTRE_CANCILLER_RICARDO_PATI%C3%91O_Y_JULIAN_ASSANGE_-_14953880621.jpg/800px-RUEDA_DE_PRENSA_CONJUNTA_ENTRE_CANCILLER_RICARDO_PATI%C3%91O_Y_JULIAN_ASSANGE_-_14953880621.jpg";
        String intentExtraSearchString = "TODO";
//        intentExtraSearchString = intent.getStringExtra(EXTRA_SEARCH_STRING);

        WebView webView = rootView.findViewById(R.id.webView);
        TextView tvSearchString = rootView.findViewById(R.id.tv_search_string);
        cbToggleFavourite = rootView.findViewById(R.id.cb_toggle_favourite);
        tvSearchString.setText(intentExtraSearchString);

        initWebView(webView);


        mFavourite = new Favourite(currentUser.getUser().getId(),
                intentExtraSearchString, mIntentExtraUrl);

        mViewModel = new ViewModelProvider(this).get(ImageViewerActivityViewModel.class);

        subscribeToModel();

        Log.d(TAG, "Intent received. Image Url: " + mIntentExtraUrl +
                ". SearchString: " + intentExtraSearchString);

        CheckBox cbToggleFavourite = rootView.findViewById(R.id.cb_toggle_favourite);
        // Check/Uncheck current image as favourite and save choice to local Database
        cbToggleFavourite.setOnClickListener(view -> {
            mViewModel.toggleFavourite(mFavourite);
        });

        ImageView ivSaveImage = rootView.findViewById(R.id.img_save);
        // Save current image to Media folder
        ivSaveImage.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireContext().getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                mViewModel.saveImage(Glide.with(requireContext().getApplicationContext()),
                        requireContext().getApplicationContext().getContentResolver(),
                        mFavourite);
            } else {
                Log.d(TAG, "actionSaveImage: Permission not granted! Trying to ask for...");
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });


        return rootView;
    }

    private void subscribeToModel() {

        mViewModel.getInFavourites(mFavourite).observe(getViewLifecycleOwner(), inFavourites -> {
            if (inFavourites != null) {
                cbToggleFavourite.setChecked(inFavourites);
                Log.d(TAG, "onCreate: inFavourites = " + inFavourites);
            }
        });

        mViewModel.getSnackBarMessage().observe(this,
                message -> ((MainActivity) requireActivity()).showSnackBarMessage(message));
    }

    private void initWebView(WebView view) {
        //Add zoom controls:
        view.getSettings().setBuiltInZoomControls(true);
        //Resize image to screen width:
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUseWideViewPort(true);
        //This line will prevent random Fatal signal 11 (SIGSEGV) error on emulator:
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        view.loadUrl(mIntentExtraUrl);
    }


}