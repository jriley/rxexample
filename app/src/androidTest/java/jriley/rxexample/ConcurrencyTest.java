package jriley.rxexample;


import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.base.MainThread;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import jriley.rxexample.modules.TestingServiceModule;
import jriley.rxexample.services.ABCService;
import retrofit.android.MainThreadExecutor;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ConcurrencyTest {

    MockWebServer mockWebServer;

    private final String serviceOne = "aeimqu";
    private final String serviceTwo = "bfjnrv";
    private final String serviceThree = "cgkosw";
    private final String serviceFour = "dhlptxyz";
    private ConcurrencyCaller testObject;

    @Inject
    ABCService abcService;

    @Before
    public void setUp() throws Exception {

        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(new MyDispatcher());
        mockWebServer.start();

        final TestingServiceModule testingServiceModule =
                new TestingServiceModule(mockWebServer.getUrl(""));
        final Instrumentation instrumentation =
                InstrumentationRegistry.getInstrumentation();
        final TestComponent testComponent = DaggerConcurrencyTest_TestComponent.builder()
                .testingServiceModule(testingServiceModule).build();
        final RxJavaExampleApplication exampleApplication =
                (RxJavaExampleApplication) instrumentation.getTargetContext().getApplicationContext();
        exampleApplication.setComponent(testComponent);

        testComponent.inject(this);
//        final Scheduler testScheduler = Schedulers.from(new MainThreadExecutor());
        final Scheduler testScheduler = Schedulers.from(Runnable::run);

        testObject = new ConcurrencyCaller(abcService, testScheduler, testScheduler);

    }

    @Test
    public void testOne() throws Exception {
        testObject.serviceCallStart();

        List<char[]> collection = Arrays.asList(serviceOne.toCharArray());

//        testObject.sortedStringList.containsAll(Arrays.asList(serviceTwo.toCharArray()));
//        testObject.sortedStringList.containsAll(Arrays.asList(serviceThree.toCharArray()));
//        testObject.sortedStringList.containsAll(Arrays.asList(serviceFour.toCharArray()));

//        System.out.print(testObject.sortedStringList.toString());
        for (String s :testObject.sortedStringList ) {

            Log.e("Damn",s);
        }
        assertTrue(testObject.sortedStringList.get(0), testObject.sortedStringList.containsAll(collection));
    }

    @Test
    public void testTwo() throws Exception {
        testObject.serviceCallStart();

        fail();
    }

    private class MyDispatcher extends Dispatcher {
        int counterOne, counterTwo, counterThree, counterFour = 0;
        Random networkDelay = new Random();

        @Override
        public MockResponse dispatch(final RecordedRequest recordedRequest) throws InterruptedException {

            Thread.sleep(networkDelay.nextInt(3) * 100);

            if (recordedRequest.getPath().contains("/service/one")) {

                final MockResponse mockResponse = getMockResponse(counterOne, serviceOne);
                counterOne++;
                return mockResponse;

            } else if (recordedRequest.getPath().contains("/service/two")) {

                final MockResponse mockResponse = getMockResponse(counterTwo, serviceTwo);
                counterTwo++;
                return mockResponse;

            } else if (recordedRequest.getPath().contains("/service/three")) {

                final MockResponse mockResponse = getMockResponse(counterThree, serviceThree);
                counterThree++;
                return mockResponse;

            } else if (recordedRequest.getPath().contains("/service/four")) {

                final MockResponse mockResponse = getMockResponse(counterFour, serviceFour);
                counterFour++;
                return mockResponse;

            } else {
                return null;
            }
        }

        private MockResponse getMockResponse(int counter, final String serviceResource) {
            if (counter > serviceResource.length()) {
                counter = 0;
            }

            return new MockResponse().setResponseCode(200).setBody(serviceResource);
        }

    }

    @Singleton
    @Component(modules = TestingServiceModule.class)
    public interface TestComponent extends MainComponent {

        void inject(ConcurrencyTest concurrencyCaller);
    }
}
