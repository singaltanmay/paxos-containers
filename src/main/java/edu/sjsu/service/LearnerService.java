/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.service;

import edu.sjsu.entity.PaxosMessage;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class LearnerService {

  @Getter
  private String learned;

  public void incoming(PaxosMessage message) {
    learned = message.getValue();
  }
}
