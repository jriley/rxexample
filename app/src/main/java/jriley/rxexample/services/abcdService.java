package jriley.rxexample.services;


import retrofit.http.GET;
import rx.Observable;

public interface abcdService {

    @GET("/service/one")
    Observable<AbcdResponse> getOne();

    @GET("/service/two")
    Observable<AbcdResponse> getTwo();

    @GET("/service/three")
    Observable<AbcdResponse> getThree();

    @GET("/service/four")
    Observable<AbcdResponse> getFour();
}
