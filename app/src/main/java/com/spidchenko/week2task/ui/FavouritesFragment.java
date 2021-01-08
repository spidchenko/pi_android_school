package com.spidchenko.week2task.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.SwipeHelper;
import com.spidchenko.week2task.adapter.FavouritesListAdapter;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.viewmodel.FavouritesViewModel;

import static com.spidchenko.week2task.adapter.FavouritesListAdapter.ACTION_DELETE;
import static com.spidchenko.week2task.adapter.FavouritesListAdapter.ACTION_OPEN_IMAGE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavouritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouritesFragment extends Fragment implements FavouritesListAdapter.OnFavouritesListAdapterListener {

    private static final String TAG = "FavFragment.LOG_TAG";

    OnFragmentInteractionListener mListener;

    private FavouritesViewModel mViewModel;
    private RecyclerView mRvFavouriteImages;
    private FavouritesListAdapter mRecyclerAdapter;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavouritesFragment.
     */
    public static FavouritesFragment newInstance(String param1, String param2) {
        FavouritesFragment fragment = new FavouritesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        final View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        mRvFavouriteImages = rootView.findViewById(R.id.rv_favourite_images);
        initRecyclerView();
        mViewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);
        subscribeToModel();
        return rootView;
    }

    @Override
    public void onItemClick(int action, int position) {
        Favourite favourite = mRecyclerAdapter.getFavouriteAtPosition(position);
        switch (action) {
            case ACTION_DELETE: {
                mViewModel.deleteFavourite(favourite);
                break;
            }
            case ACTION_OPEN_IMAGE: {
                mListener.onOpenFavouriteAction(favourite);
                break;
            }
        }
    }

    private void subscribeToModel() {
        mViewModel.getAllFavourites().observe(getViewLifecycleOwner(),
                favourites -> mRecyclerAdapter.setFavourites(favourites));

        mViewModel.getSnackBarMessage().observe(this,
                message -> ((MainActivity) requireActivity()).showSnackBarMessage(message));
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new FavouritesListAdapter(null, this);
        mRvFavouriteImages.setAdapter(mRecyclerAdapter);
        mRvFavouriteImages.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ItemTouchHelper helper = SwipeHelper.getSwipeToDismissTouchHelper(position -> {
            Favourite favourite = mRecyclerAdapter.getFavouriteAtPosition(position);
            mViewModel.deleteFavourite(favourite);
        });

        helper.attachToRecyclerView(mRvFavouriteImages);
    }


    interface OnFragmentInteractionListener {
        void onOpenFavouriteAction(Favourite favourite);
    }

}