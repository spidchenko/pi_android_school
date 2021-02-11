package com.spidchenko.week2task;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.repositories.ImageRepository;
import com.spidchenko.week2task.repositories.SearchRequestRepository;
import com.spidchenko.week2task.repositories.SharedPrefRepository;
import com.spidchenko.week2task.viewmodel.SearchViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SearchViewModelTest {
    private static final int MOCK_USER_ID = 142;
    private static final String MOCK_LAST_SEARCH = "Last Search";

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule mExecutorRule = new InstantTaskExecutorRule();

    private SearchViewModel mSearchViewModel;

    @Mock
    private ImageRepository mImageRepoMock;
    @Mock
    private SharedPrefRepository sharedPrefRepoMock;
    @Mock
    private SearchRequestRepository searchRequestRepoMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(sharedPrefRepoMock.getUserId()).thenReturn(MOCK_USER_ID);
        Mockito.when(sharedPrefRepoMock.getLastSearch()).thenReturn(MOCK_LAST_SEARCH);

        mSearchViewModel = new SearchViewModel(mImageRepoMock, sharedPrefRepoMock, searchRequestRepoMock);
    }

    @Test
    public void searchImages_lastSearchSavedInSharedPref() {
        String searchString = "Test Search String";
        mSearchViewModel.searchImages(searchString);
        Mockito.verify(sharedPrefRepoMock).saveLastSearch(searchString);
    }

    @Test
    public void searchImages_lastSearchSavedInDb() {
        String searchString = "Test Search String To Be Saved In Db";
        mSearchViewModel.searchImages(searchString);
        Mockito.verify(searchRequestRepoMock).saveCurrentSearchInDb(Mockito.argThat(
                (SearchRequest s) -> s.getUserId() == MOCK_USER_ID &&
                        s.getSearchRequest().equals(searchString)
        ));
    }

    @Test
    public void searchImages_callRepoUpdate() {
        String searchString = "Test String";
        mSearchViewModel.searchImages(searchString);
        Mockito.verify(mImageRepoMock).updateImages(Mockito.eq(searchString), Mockito.any());
    }

    @Test
    public void searchImagesByCoordinates_hasValidSearchString() {
        mSearchViewModel.searchImagesByCoordinates("-12.12323234", "45.23412334");
        Mockito.verify(searchRequestRepoMock).saveCurrentSearchInDb(Mockito.argThat(
                (SearchRequest s) -> s.getUserId() == MOCK_USER_ID &&
                        s.getSearchRequest().equals("GeoSearch. Lat:-12.1 Lon:45.23")
        ));
    }

    @Test
    public void searchImages_callRepoUpdateByCoords() {
        String lat = "33.3333";
        String lon = "44.4444";
        mSearchViewModel.searchImagesByCoordinates(lat, lon);
        Mockito.verify(mImageRepoMock).updateImagesByCoordinates(Mockito.eq(lat), Mockito.eq(lon),
                Mockito.any());
    }

    @Test
    public void getSearchString_returnValidValue() {
        Assert.assertEquals(mSearchViewModel.getSearchString().getValue(), MOCK_LAST_SEARCH);
    }

    @Test
    public void cgetAllImages_returnNotNull() {
        Assert.assertNotNull(mSearchViewModel.getAllImages());
    }

    @Test
    public void getLoadingState_returnNotNull() {
        Assert.assertNotNull(mSearchViewModel.getLoadingState());
    }
}
