/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.service;

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
public class AcceptorService {

  private static final Logger LOGGER = LogManager.getLogger(AcceptorService.class);
  private final Router router;
  long highestIdAcceptedSoFar = -1;

  public AcceptorService() {
    Retrofit retrofit = RetrofitClient.getRetrofitInstance();
    router = retrofit.create(Router.class);
  }

  public void incoming(PaxosMessage message) {
    LOGGER.info("Received new " + message.getMessageType().toString() + ": " + message);
    // Proposer has sent a proposal
    if (message.getMessageType() == PAXOS_MESSAGE_TYPE.PROPOSAL) {
      promise(message);
    } else if (message.getMessageType() == PAXOS_MESSAGE_TYPE.ACCEPT_REQUEST) {
      accept(message);
    }
  }

  private void promise(PaxosMessage proposal) {
    // Assume everything checks out
    // Send back a PROMISE
    final long proposalId = proposal.getId();
    if (proposalId > highestIdAcceptedSoFar) {
      // Respond with a promiseMessage
      highestIdAcceptedSoFar = proposalId;
      PaxosMessage promiseMessage = new PaxosMessage(proposalId, PAXOS_MESSAGE_TYPE.PROMISE, proposal.getValue(), proposal.getMessageSource());
      RetrofitClient.sendPaxosMessage(router, promiseMessage, Optional.of(() -> LOGGER.info("Sent promiseMessage " + promiseMessage + " for proposal " + proposal)));
    } // else ignore
  }

  // Acceptor accepts a value
  private void accept(PaxosMessage acceptRequest) {
    // TODO if everything checks out (IDvise)
    final PaxosMessage acceptMessage = PaxosMessage.respondTo(acceptRequest, PAXOS_MESSAGE_TYPE.ACCEPT);
    RetrofitClient.sendPaxosMessage(router, acceptMessage, Optional.of(() -> LOGGER.info("Sent acceptMessage " + acceptMessage + " for value " + acceptRequest.getValue())));
  }

}