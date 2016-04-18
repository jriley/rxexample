package jriley.rxexample.modules;

import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jriley.rxexample.services.abcdService;
import retrofit.RestAdapter;

@Module
public class SlowServiceTestingModule {

    URL baseUrl;

    public SlowServiceTestingModule(URL baseUrl){
        this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    RestAdapter getRestAdapter(){
        return new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(new ThreadPoolExecutor(6, 10, 6, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10)), Runnable::run)
                .setEndpoint(baseUrl.toString()).build();
    }

    @Provides
    @Singleton
    abcdService getAService(RestAdapter restAdapter) {
        return restAdapter.create(abcdService.class);
    }

}
