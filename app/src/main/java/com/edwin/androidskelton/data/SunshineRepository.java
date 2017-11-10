package com.edwin.androidskelton.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.edwin.androidskelton.AppExecutors;
import com.edwin.androidskelton.data.database.ListWeatherEntry;
import com.edwin.androidskelton.data.database.WeatherDao;
import com.edwin.androidskelton.data.database.WeatherEntry;
import com.edwin.androidskelton.data.network.WeatherNetworkDataSource;
import com.edwin.androidskelton.utilities.SunshineDateUtils;

import java.util.Date;
import java.util.List;

/**
 * Handles data operations in Sunshine. Acts as a mediator between {@link WeatherNetworkDataSource}
 * and {@link WeatherDao}
 */
public class SunshineRepository {
    private static final String LOG_TAG = SunshineRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static SunshineRepository sInstance;
    private final WeatherDao mWeatherDao;
    private final WeatherNetworkDataSource mWeatherNetworkDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private SunshineRepository(WeatherDao weatherDao,
                               WeatherNetworkDataSource weatherNetworkDataSource,
                               AppExecutors executors) {
        mWeatherDao = weatherDao;
        mWeatherNetworkDataSource = weatherNetworkDataSource;
        mExecutors = executors;

        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        LiveData<WeatherEntry[]> networkData = mWeatherNetworkDataSource.getCurrentWeatherForecasts();
        networkData.observeForever(new Observer<WeatherEntry[]>() {
            @Override
            public void onChanged(@Nullable WeatherEntry[] newForecastsFromNetwork) {
                mExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Deletes old historical data
                        deleteOldData();
                        Log.d(LOG_TAG, "Old weather deleted");
                        // Insert our new weather data into Sunshine's database
                        mWeatherDao.bulkInsert(newForecastsFromNetwork);
                        Log.d(LOG_TAG, "New values inserted");
                    }
                });

            }
        });
// lambda
//        networkData.observeForever(newForecastsFromNetwork -> {
//            mExecutors.diskIO().execute(() -> {
//                // Deletes old historical data
//                deleteOldData();
//                Log.d(LOG_TAG, "Old weather deleted");
//                // Insert our new weather data into Sunshine's database
//                mWeatherDao.bulkInsert(newForecastsFromNetwork);
//                Log.d(LOG_TAG, "New values inserted");
//            });
//        });
    }

    public synchronized static SunshineRepository getInstance(
            WeatherDao weatherDao, WeatherNetworkDataSource weatherNetworkDataSource,
            AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new SunshineRepository(weatherDao, weatherNetworkDataSource,
                        executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    private synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;

        // This method call triggers Sunshine to create its task to synchronize weather data
        // periodically.
        mWeatherNetworkDataSource.scheduleRecurringFetchWeatherSync();

        mExecutors.diskIO().execute(() -> {
            if (isFetchNeeded()) {
                startFetchWeatherService();
            }
        });
    }

    /**
     * Database related operations
     **/

    public LiveData<List<ListWeatherEntry>> getCurrentWeatherForecasts() {
        initializeData();
        Date today = SunshineDateUtils.getNormalizedUtcDateForToday();
        return mWeatherDao.getCurrentWeatherForecasts(today);
    }

    public LiveData<WeatherEntry> getWeatherByDate(Date date) {
        initializeData();
        return mWeatherDao.getWeatherByDate(date);
    }

    /**
     * Deletes old weather data because we don't need to keep multiple days' data
     */
    private void deleteOldData() {
        Date today = SunshineDateUtils.getNormalizedUtcDateForToday();
        mWeatherDao.deleteOldWeather(today);
    }

    /**
     * Checks if there are enough days of future weather for the app to display all the needed data.
     *
     * @return Whether a fetch is needed
     */
    private boolean isFetchNeeded() {
        Date today = SunshineDateUtils.getNormalizedUtcDateForToday();
        int count = mWeatherDao.countAllFutureWeather(today);
        return (count < WeatherNetworkDataSource.NUM_DAYS);
    }

    /**
     * Network related operation
     */

    private void startFetchWeatherService() {
        mWeatherNetworkDataSource.startFetchWeatherService();
    }

}