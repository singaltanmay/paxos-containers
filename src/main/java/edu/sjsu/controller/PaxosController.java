/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.controller;

import edu.sjsu.Application;
import edu.sjsu.Application.PAXOS_ROLES;
import edu.sjsu.entity.PaxosMessage;
import edu.sjsu.entity.PaxosMessage.PAXOS_MESSAGE_TYPE;
import edu.sjsu.service.AcceptorService;
import edu.sjsu.service.LearnerService;
import edu.sjsu.service.ProposerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaxosController {

  private final ProposerService proposerService;
  private final AcceptorService acceptorService;
  private final LearnerService learnerService;

  @Autowired
  public PaxosController(ProposerService proposerService, AcceptorService acceptorService, LearnerService learnerService) {
    this.proposerService = proposerService;
    this.acceptorService = acceptorService;
    this.learnerService = learnerService;
  }

  @PostMapping("propose")
  public void proposeValue(@RequestBody String value) {
    proposerService.propose(value);
  }

  @PostMapping("message")
  public ResponseEntity<Void> incoming(@RequestBody PaxosMessage message) {
    if (message.getMessageType() == PAXOS_MESSAGE_TYPE.PROPOSAL) {
      acceptorService.incoming(message);
    } else if (message.getMessageType() == PAXOS_MESSAGE_TYPE.PROMISE) {
      proposerService.incoming(message);
    } else if (message.getMessageType() == PAXOS_MESSAGE_TYPE.ACCEPT_REQUEST) {
      acceptorService.incoming(message);
    } else if (message.getMessageType() == PAXOS_MESSAGE_TYPE.ACCEPT) {
      if (Application.APPLICATION_PAXOS_ROLE == PAXOS_ROLES.PROPOSER) {
        proposerService.incoming(message);
      } else if (Application.APPLICATION_PAXOS_ROLE == PAXOS_ROLES.LEARNER) {
        learnerService.incoming(message);
      }
    }
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("value")
  public String getLearnedValue() {
    if (Application.APPLICATION_PAXOS_ROLE.equals(PAXOS_ROLES.LEARNER)) {
      return LearnerService.getLearned();
    }
    return null;
  }

}
