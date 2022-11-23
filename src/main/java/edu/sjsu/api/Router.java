/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.api;

import edu.sjsu.entity.PaxosMessage;
import edu.sjsu.entity.Register;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Router {


  @POST("message")
  Call<Void> sendMessage(@Body PaxosMessage message);

  /**
   * Two-way handshake with router to register the coming online of a new node
   */
  @POST("register")
  Call<Void> register(@Body Register register);


}
