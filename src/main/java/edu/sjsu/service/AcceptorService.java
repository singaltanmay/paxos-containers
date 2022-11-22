/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.service;

import edu.sjsu.api.RetrofitClient;
import edu.sjsu.api.Router;
import edu.sjsu.entity.PaxosMessage;
import edu.sjsu.entity.PaxosMessage.PAXOS_MESSAGE_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@Service
public class AcceptorService {

  private static final Logger logger = LogManager.getLogger(AcceptorService.class);

  long highestIdAcceptedSoFar = -1;

  private final Router router;

  public AcceptorService() {
    Retrofit retrofit = RetrofitClient.getRetrofitInstance();
    router = retrofit.create(Router.class);
  }


  public void incoming(PaxosMessage message) {
    // Proposer has sent a proposal
    if (message.getMessageType() == PAXOS_MESSAGE_TYPE.PROPOSAL) {
      handleProposal(message);
    }
  }

  private void handleProposal(PaxosMessage proposal) {
    // Assume everything checks out
    // Send back a PROMISE
    highestIdAcceptedSoFar = proposal.getId();
    PaxosMessage promise = new PaxosMessage(proposal.getId(), PAXOS_MESSAGE_TYPE.PROMISE, proposal.getValue(), proposal.getMessageSource());
    sendPromiseMessage(promise);
  }

  private void sendPromiseMessage(PaxosMessage promise) {
    Call<Void> call = router.sendMessage(promise);
    call.enqueue(new Callback<>() {
      @Override
      public void onResponse(Call<Void> call, Response<Void> response) {
        logger.info("Sent Promise: " + promise + "\n Received response code: " + response.code());
      }

      @Override
      public void onFailure(Call<Void> call, Throwable throwable) {
        logger.info("Failed to send Promise: " + promise);
      }
    });
  }

}