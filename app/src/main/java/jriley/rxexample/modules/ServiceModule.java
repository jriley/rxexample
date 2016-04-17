package jriley.rxexample.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jriley.rxexample.services.ABCService;
import retrofit.RestAdapter;

@Module
public class ServiceModule {

    @Provides
    @Singleton
    RestAdapter getRestAdapter() {
        return new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://www.google.com/?gws_rd=ssl").build();
    }

    @Provides
    ABCService providesSBCService(RestAdapter restAdapter){
        return restAdapter.create(ABCService.class);
    }
}
