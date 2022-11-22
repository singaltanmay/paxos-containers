/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.controller;

import edu.sjsu.entity.PaxosMessage;
import edu.sjsu.service.AcceptorService;
import edu.sjsu.service.LearnerService;
import edu.sjsu.service.ProposerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaxosController {

  private ProposerService proposerService;
  private AcceptorService acceptorService;
  private LearnerService learnerService;

  @Autowired
  public PaxosController(ProposerService proposerService, AcceptorService acceptorService, LearnerService learnerService) {
    this.proposerService = proposerService;
    this.acceptorService = acceptorService;
    this.learnerService = learnerService;
  }

  @PostMapping("message")
  public ResponseEntity<Void> incoming(@RequestBody PaxosMessage message) {
    switch (message.getMessageType()) {
      case PROPOSAL -> acceptorService.incoming(message);
      case PROMISE -> proposerService.incoming(message);
    }
    return ResponseEntity.status(HttpStatus.OK).build();
  }

}
