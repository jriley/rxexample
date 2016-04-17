package jriley.rxexample.services;


import retrofit.client.Response;
import retrofit.http.GET;
import rx.Observable;

public interface ABCService {

    @GET("/service/one")
    Observable<Response> getOne();

    @GET("/service/two")
    Observable<Response> getTwo();

    @GET("/service/three")
    Observable<Response> getThree();

    @GET("/service/four")
    Observable<Response> getFour();
}
