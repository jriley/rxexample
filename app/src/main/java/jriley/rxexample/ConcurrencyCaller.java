package jriley.rxexample;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;


import jriley.rxexample.services.abcdService;
import retrofit.client.Response;
import rx.Observable;
import rx.Scheduler;

public class ConcurrencyCaller {

    private final abcdService abcdService;
    private final Scheduler subscribeOn;
    private final Scheduler observeOn;

    List<String> sortedStringList = new ArrayList<>();

    ConcurrencyCaller(abcdService abcdService, Scheduler subscribeOn, Scheduler observeOn) {

        this.abcdService = abcdService;
        this.subscribeOn = subscribeOn;
        this.observeOn = observeOn;
    }

    public void serviceCallStart() {

        Log.e("ServiceCallStart", "......");

        final Observable<Response> one = abcdService.getOne();
        one.subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .doOnNext(response -> {
                    System.out.println(response.getBody());
                    Log.e("One", response.getBody().toString());
                });

        final Observable<Response> two = abcdService.getTwo();
        two.subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .doOnNext(response -> {
                    System.out.println(response.getBody());
                    Log.e("Two", response.getBody().toString());
                });

        final Observable<Response> three = abcdService.getThree();
        three.subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .first()
                .doOnNext(response -> {
                    System.out.println(response.getBody());
                    Log.e("Three", response.getBody().toString());
                });

        final Observable<Response> four = abcdService.getFour();
        four.subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .doOnNext(response -> {
                    System.out.println(response.getBody());
                    Log.e("Four", response.getBody().toString());
                });

        Observable.combineLatest(one, two, three, four, (response, response2, response3, response4) -> {

            if (!sortedStringList.contains(response.getBody().toString())) {
                sortedStringList.add(response.getBody().toString());
            }

            return null;
        }).retry(1)
                .subscribe(o -> Log.e("onNext", sortedStringList.toString()),
                        throwable -> Log.e("onNext<Throwable>", "oops"),
                        () -> Log.e("onComplete call", sortedStringList.toString()));

    }
}
