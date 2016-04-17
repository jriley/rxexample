package jriley.rxexample;

import android.app.Application;

import jriley.rxexample.modules.ServiceModule;

public class RxJavaExampleApplication extends Application {

    MainComponent mainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if(mainComponent == null){
            mainComponent = DaggerApplicationComponent.builder().serviceModule(new ServiceModule()).build();
        }
    }

    public void setComponent(MainComponent component) {
        this.mainComponent = component;
    }
}
