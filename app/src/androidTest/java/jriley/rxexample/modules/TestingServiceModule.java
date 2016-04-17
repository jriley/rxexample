package jriley.rxexample.modules;

import android.os.AsyncTask;

import java.net.URL;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jriley.rxexample.services.ABCService;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;

@Module
public class TestingServiceModule {

    URL baseUrl;

    public TestingServiceModule(URL baseUrl){
        this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    RestAdapter getRestAdapter(){
        return new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(Runnable::run, Runnable::run)
                .setEndpoint(baseUrl.toString()).build();
    }

    @Provides
    @Singleton
    ABCService getAlphabetService(RestAdapter restAdapter) {
        return restAdapter.create(ABCService.class);
    }
}
