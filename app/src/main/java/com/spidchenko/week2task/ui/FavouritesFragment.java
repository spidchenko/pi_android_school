package com.spidchenko.week2task.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.spidchenko.week2task.adapter.FavouritesListAdapter;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.viewmodel.FavouritesViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavouritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouritesFragment extends Fragment implements FavouritesListAdapter.OnCardListener, FavouritesListAdapter.OnDeleteClickListener {

    private static final String TAG = "FavFragment.LOG_TAG";

    OnFragmentInteractionListener mListener;

    private FavouritesViewModel mViewModel;
    private RecyclerView mRvFavouriteImages;
    private FavouritesListAdapter mRecyclerAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
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
    // TODO: Rename and change types and number of parameters
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

    ItemTouchHelper getSwipeToDismissTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof FavouritesListAdapter.CategoryViewHolder)
                    return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                Log.d(TAG, "ViewHolder Swiped! Position= " + position);
                Favourite favourite = mRecyclerAdapter.getFavouriteAtPosition(position);
                mViewModel.deleteFavourite(favourite);
            }
        });
    }

    @Override
    public void onCardClick(int position) {
        Log.d(TAG, "ViewHolder clicked! Position = " + position);
        Favourite favourite = mRecyclerAdapter.getFavouriteAtPosition(position);
        mListener.onOpenFavouriteAction(favourite);
    }

    @Override
    public void onDeleteClick(int position) {
        Log.d(TAG, "Activity - onDeleteClick: " + position);
        Favourite favourite = mRecyclerAdapter.getFavouriteAtPosition(position);
        mViewModel.deleteFavourite(favourite);
    }

    private void subscribeToModel() {
        mViewModel.getAllFavourites().observe(getViewLifecycleOwner(), favourites -> {
            // TODO: 12/22/20 `notifyDataSetChanged` can be moved to `setFavourites`
            mRecyclerAdapter.setFavourites(favourites);
            mRecyclerAdapter.notifyDataSetChanged();
        });

        mViewModel.getSnackBarMessage().observe(this,
                message -> ((MainActivity)requireActivity()).showSnackBarMessage(message));
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new FavouritesListAdapter(null, this, this);
        mRvFavouriteImages.setAdapter(mRecyclerAdapter);
        mRvFavouriteImages.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ItemTouchHelper helper = getSwipeToDismissTouchHelper();
        helper.attachToRecyclerView(mRvFavouriteImages);
    }

    interface OnFragmentInteractionListener {
        void onOpenFavouriteAction(Favourite favourite);
    }

}