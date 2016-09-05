package com.onegini.mobile.exampleapp.network.client;

import com.onegini.mobile.exampleapp.model.ApplicationDetails;
import retrofit.http.GET;
import rx.Observable;

public interface ApplicationDetailsClient {

  @GET("/application-details")
  Observable<ApplicationDetails> getApplicationDetails();
}
