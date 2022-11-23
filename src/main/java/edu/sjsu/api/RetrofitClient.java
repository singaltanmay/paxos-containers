/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.api;

import edu.sjsu.Application;
import edu.sjsu.entity.PaxosMessage;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

  private static final Logger LOGGER = LogManager.getLogger(Router.class);
  private static Retrofit retrofit;

  public static Retrofit getRetrofitInstance() {
    if (retrofit == null) {
      retrofit = new Retrofit.Builder().baseUrl(Application.ROUTER_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }
    return retrofit;
  }

  public static PaxosMessage sendPaxosMessage(Router router, PaxosMessage message, Optional<Runnable> onResponse) {
    Call<Void> call = router.sendMessage(message);
    call.enqueue(new Callback<>() {
      @Override
      public void onResponse(Call<Void> call, Response<Void> response) {
        LOGGER.info("Sent message " + message + " and got code : " + response.code());
        onResponse.ifPresent(Runnable::run);
      }

      @Override
      public void onFailure(Call<Void> call, Throwable throwable) {
        LOGGER.debug("Failed to send message " + message);
      }
    });
    return message;
  }

}
