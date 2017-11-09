package com.edwin.androidskelton.ui.list;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.edwin.androidskelton.data.SunshineRepository;
import com.edwin.androidskelton.data.database.ListWeatherEntry;

import java.util.List;

/**
 * {@link ViewModel} for {@link MainActivity}
 */
class MainActivityViewModel extends ViewModel {

    private final SunshineRepository mRepository;
    private final LiveData<List<ListWeatherEntry>> mForecast;

    public MainActivityViewModel(SunshineRepository repository) {
        mRepository = repository;
        mForecast = mRepository.getCurrentWeatherForecasts();
    }

    public LiveData<List<ListWeatherEntry>> getForecast() {
        return mForecast;
    }


}
