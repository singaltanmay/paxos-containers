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
  long highestIDSeenSoFar = -1;

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
    final long proposalId = proposal.getId();
    if (proposalId <= highestIDSeenSoFar) {
      RetrofitClient.sendPaxosMessage(router, proposal.setIgnore(true), Optional.of(() -> LOGGER.info("Ignored proposal message: " + proposal + " as its ID is lower than the highest seen so far: " + highestIDSeenSoFar)));
      return;
    }

    // Respond with a promiseMessage
    highestIDSeenSoFar = proposalId;
    PaxosMessage promiseMessage = new PaxosMessage(proposalId, PAXOS_MESSAGE_TYPE.PROMISE, proposal.getValue(), proposal.getMessageSource());
    RetrofitClient.sendPaxosMessage(router, promiseMessage, Optional.of(() -> LOGGER.info("Sent promiseMessage " + promiseMessage + " for proposal " + proposal)));
  }

  // Acceptor accepts a value
  private void accept(PaxosMessage acceptRequest) {
    // A proposal from a higher ID is being considered
    if (acceptRequest.getId() < highestIDSeenSoFar) {
      RetrofitClient.sendPaxosMessage(router, acceptRequest.setIgnore(true), Optional.of(() -> LOGGER.info("Ignored accept message: " + acceptRequest + " as its ID is lower than the highest seen so far: " + highestIDSeenSoFar)));
      return;
    }
    final PaxosMessage acceptMessage = PaxosMessage.respondTo(acceptRequest, PAXOS_MESSAGE_TYPE.ACCEPT);
    RetrofitClient.sendPaxosMessage(router, acceptMessage, Optional.of(() -> LOGGER.info("Sent acceptMessage " + acceptMessage + " for value " + acceptRequest.getValue())));
  }

}