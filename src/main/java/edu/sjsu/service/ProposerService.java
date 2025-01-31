/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.service;

import static edu.sjsu.entity.PaxosMessage.PAXOS_MESSAGE_TYPE.ACCEPT;
import static edu.sjsu.entity.PaxosMessage.PAXOS_MESSAGE_TYPE.PROMISE;
import static edu.sjsu.entity.PaxosMessage.PAXOS_MESSAGE_TYPE.PROPOSAL;

import edu.sjsu.api.RetrofitClient;
import edu.sjsu.api.Router;
import edu.sjsu.entity.PaxosMessage;
import edu.sjsu.entity.PaxosMessage.PAXOS_MESSAGE_TYPE;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

@Service
public class ProposerService {

  private static final Logger LOGGER = LogManager.getLogger(ProposerService.class);
  private static int valueAcceptedCount = 0;
  private final Router router;

  public ProposerService() {
    Retrofit retrofit = RetrofitClient.getRetrofitInstance();
    router = retrofit.create(Router.class);
  }

  public void propose(String value) {
    // Send prepare acceptMessage
    PaxosMessage acceptMessage = new PaxosMessage(PROPOSAL, value);
    RetrofitClient.sendPaxosMessage(router, acceptMessage, Optional.of(() -> LOGGER.info("Sent proposal for value " + value)));
  }

  private void acceptRequest(PaxosMessage promise) {
    final PaxosMessage acceptRequestMessage = PaxosMessage.respondTo(promise, PAXOS_MESSAGE_TYPE.ACCEPT_REQUEST);
    RetrofitClient.sendPaxosMessage(router, acceptRequestMessage, Optional.of(() -> LOGGER.info("Sent Accept-Request message to all proposers for proposed value " + promise.getValue())));
  }

  public void incoming(PaxosMessage message) {
    if (message.getMessageType() == PROMISE) {
      valueAcceptedCount++;
      // Only ACCEPT when PROPOSAL accepted by a majority of acceptors
      if (valueAcceptedCount > (message.getNumAcceptors() / 2)) {
        acceptRequest(message);
      }
    } else if (message.getMessageType() == ACCEPT) {
      LOGGER.info("Received ACCEPT for proposed value: " + message.getValue());
    }
  }
}
