package jriley.rxexample;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;


import jriley.rxexample.services.ABCService;
import retrofit.client.Response;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.functions.Func4;

public class ConcurrencyCaller {

    private final ABCService abcService;
    private final Scheduler subscribeOn;
    private final Scheduler observeOn;

    List<String> sortedStringList = new ArrayList<>();

    ConcurrencyCaller(ABCService abcService, Scheduler subscribeOn, Scheduler observeOn) {

        this.abcService = abcService;
        this.subscribeOn = subscribeOn;
        this.observeOn = observeOn;
    }

    public void serviceCallStart() {

        Log.e("ServiceCallStart", "......");

        final Observable<Response> one = abcService.getOne();
        one.subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(final Response response) {
                        System.out.println(response.getBody());
                        Log.e("One", response.getBody().toString());
                    }
                });

        final Observable<Response> two = abcService.getTwo();
        two.subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(final Response response) {
                        System.out.println(response.getBody());
                        Log.e("Two", response.getBody().toString());
                    }
                });

        final Observable<Response> three = abcService.getThree();
        three.subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .first()
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(final Response response) {
                        System.out.println(response.getBody());
                        Log.e("Three", response.getBody().toString());
                    }
                });

        final Observable<Response> four = abcService.getFour();
        four.subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(final Response response) {
                        System.out.println(response.getBody());
                        Log.e("Four", response.getBody().toString());
                    }
                });

        Observable.combineLatest(one, two, three, four, new Func4<Response, Response, Response, Response, Object>() {
            @Override
            public Object call(final Response response, final Response response2, final Response response3, final Response response4) {

                if (!sortedStringList.contains(response.getBody().toString())) {
                    sortedStringList.add(response.getBody().toString());
                }

                return null;
            }
        }).subscribe(o -> {
            Log.e("End", "Blah");
        }, throwable -> {
            Log.e("Err", "oops");
        } );

    }
}
