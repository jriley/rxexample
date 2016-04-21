package jriley.rxexample;


import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import jriley.rxexample.modules.SlowServiceTestingModule;
import jriley.rxexample.services.AbcdResponse;
import jriley.rxexample.services.abcdService;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ConcurrencyTest {

    @Inject
    abcdService abcdService;

    private int oneSleep;
    private int twoSleep;
    private int threeSleep;
    private int fourSleep;

    private ConcurrencyCaller testObject;
    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws Exception {

        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(new MyDispatcher());
        mockWebServer.start();

        initServiceWaitTimes();

        final SlowServiceTestingModule slowServiceTestingModule = new SlowServiceTestingModule(mockWebServer.getUrl(""));
        final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        final TestComponent testComponent = DaggerConcurrencyTest_TestComponent.builder().slowServiceTestingModule(slowServiceTestingModule).build();
        ((RxJavaExampleApplication) instrumentation.getTargetContext().getApplicationContext()).setComponent(testComponent);

        testComponent.inject(this);


        final Scheduler testScheduler = Schedulers.from(Runnable::run);
        testObject = new ConcurrencyCaller(abcdService, testScheduler, testScheduler);
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void testOne() throws Exception {
        testObject.serviceCallStart();

        int sumOfAll = oneSleep + twoSleep + threeSleep + fourSleep + 500;
        Log.e("Total Sleep: ", sumOfAll + "ms");
        Thread.sleep(sumOfAll);

        assertTrue(testObject.sortedStringList.contains("a"));
        assertTrue(testObject.sortedStringList.contains("b"));
        assertTrue(testObject.sortedStringList.contains("C"));
        assertTrue(testObject.sortedStringList.contains("db"));
    }

    private void initServiceWaitTimes() {
        Random networkDelay = new Random();
        oneSleep = getWaitTimeInSeconds(networkDelay);
        twoSleep = getWaitTimeInSeconds(networkDelay);
        threeSleep = getWaitTimeInSeconds(networkDelay);
        fourSleep = getWaitTimeInSeconds(networkDelay);
    }

    private int getWaitTimeInSeconds(Random networkDelay) {
        return networkDelay.nextInt(8) * 1000;
    }

    private class MyDispatcher extends Dispatcher {

        @Override
        public MockResponse dispatch(final RecordedRequest recordedRequest) throws InterruptedException {

            if (recordedRequest.getPath().contains("/service/one")) {
                Thread.sleep(oneSleep);
                return getMockResponse("a");

            } else if (recordedRequest.getPath().contains("/service/two")) {
                Thread.sleep(twoSleep);
                return getMockResponse("b");

            } else if (recordedRequest.getPath().contains("/service/three")) {
                Thread.sleep(threeSleep);
                return getMockResponse("c");

            } else if (recordedRequest.getPath().contains("/service/four")) {
                Thread.sleep(fourSleep);
                return getMockResponse("d");

            } else {
                return null;
            }
        }

        private MockResponse getMockResponse(final String serviceResource) throws InterruptedException {
            String s = new Gson().toJson(new AbcdResponse(serviceResource));
            return new MockResponse().setResponseCode(200).setBody(s);
        }
    }

    @Singleton
    @Component(modules = SlowServiceTestingModule.class)
    public interface TestComponent extends MainComponent {

        void inject(ConcurrencyTest concurrencyCaller);
    }
}
