package jriley.rxexample;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;


import jriley.rxexample.services.AbcdResponse;
import jriley.rxexample.services.Tuple;
import jriley.rxexample.services.abcdService;
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

        final Observable<AbcdResponse> one = abcdService.getOne()
                .subscribeOn(subscribeOn)
                .observeOn(observeOn);

        final Observable<AbcdResponse> two = abcdService.getTwo()
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .doOnNext(abcdResponse -> Log.e("Two b", abcdResponse.getType()));

        final Observable<AbcdResponse> three = abcdService.getThree()
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .map(abcdResponse -> new AbcdResponse(abcdResponse.getType().toUpperCase()));

        final Observable<Tuple> four = abcdService.getFour().subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .map(abcdResponse -> new Tuple(abcdResponse.getType()));

        Observable.combineLatest(one, two, three, four, (response, response2, response3, response4) -> {

            sortedStringList.add(response.getType());
            sortedStringList.add(response2.getType());
            sortedStringList.add(response3.getType());
            sortedStringList.add(response4.getValue() + response2.getType());

            return null;
        }).retry(1)
                .subscribe(o -> Log.e("onNext", sortedStringList.toString()),
                        throwable -> Log.e("onNext<Throwable>", "oops"),
                        () -> Log.e("onComplete call", sortedStringList.toString()));

    }
}
