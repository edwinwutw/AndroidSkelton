package com.edwin.androidskelton.utilities;

import android.content.Context;

import com.edwin.androidskelton.AppExecutors;
import com.edwin.androidskelton.data.SunshineRepository;
import com.edwin.androidskelton.data.database.SunshineDatabase;
import com.edwin.androidskelton.data.network.WeatherNetworkDataSource;
import com.edwin.androidskelton.ui.detail.DetailViewModelFactory;
import com.edwin.androidskelton.ui.list.MainViewModelFactory;

import java.util.Date;

/**
 * Provides static methods to inject the various classes needed for Sunshine
 */
public class InjectorUtils {

    public static SunshineRepository provideRepository(Context context) {
        SunshineDatabase database = SunshineDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        WeatherNetworkDataSource networkDataSource =
                WeatherNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return SunshineRepository.getInstance(database.weatherDao(), networkDataSource, executors);
    }

    public static WeatherNetworkDataSource provideNetworkDataSource(Context context) {
        // This call to provide repository is necessary if the app starts from a service - in this
        // case the repository will not exist unless it is specifically created.
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return WeatherNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }

    public static DetailViewModelFactory provideDetailViewModelFactory(Context context, Date date) {
        SunshineRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, date);
    }

    public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        SunshineRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }

}