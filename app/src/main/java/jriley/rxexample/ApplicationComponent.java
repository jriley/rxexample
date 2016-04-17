package jriley.rxexample;

import javax.inject.Singleton;

import dagger.Component;
import jriley.rxexample.modules.ServiceModule;

@Singleton
@Component(modules = ServiceModule.class)
public interface ApplicationComponent extends MainComponent {
}
