/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.service;

import static edu.sjsu.entity.PaxosMessage.PAXOS_MESSAGE_TYPE.PROMISE;
import static edu.sjsu.entity.PaxosMessage.PAXOS_MESSAGE_TYPE.PROPOSAL;

import edu.sjsu.api.RetrofitClient;
import edu.sjsu.api.Router;
import edu.sjsu.entity.PaxosMessage;
import edu.sjsu.entity.PaxosMessage.PAXOS_MESSAGE_TYPE;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@Service
public class ProposerService {

  private final Router router;

  public ProposerService() {
    Retrofit retrofit = RetrofitClient.getRetrofitInstance();
    router = retrofit.create(Router.class);
  }

  /**
   * First step in Paxos. Send a new PREPARE message to {@link AcceptorService}
   */
  public PaxosMessage sendPrepareMessage(String value) {
    PaxosMessage message = new PaxosMessage(System.currentTimeMillis(), PROPOSAL, value);
    Call<Void> call = router.sendMessage(message);
    call.enqueue(new Callback<>() {
      @Override
      public void onResponse(Call<Void> call, Response<Void> response) {
        System.out.println("Proposed value " + message + " and got code : " + response.code());
      }

      @Override
      public void onFailure(Call<Void> call, Throwable throwable) {
        System.err.println("Failed to send message " + message);
      }
    });
    return message;
  }

  private void sendAcceptRequestMessage(PaxosMessage message) {

  }

  public void incoming(PaxosMessage message) {
    if (message.getMessageType() == PROMISE) {
      final PaxosMessage response = PaxosMessage.respondTo(message, PAXOS_MESSAGE_TYPE.ACCEPT_REQUEST);
      sendAcceptRequestMessage(response);
    }
  }
}
