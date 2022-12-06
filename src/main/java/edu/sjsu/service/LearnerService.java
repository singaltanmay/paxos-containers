/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.service;

import edu.sjsu.entity.PaxosMessage;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class LearnerService {

  private static final Logger LOGGER = LogManager.getLogger(LearnerService.class);
  @Getter
  private static String learned;

  public void incoming(PaxosMessage message) {
    LOGGER.info("Received ACCEPT for proposed value: " + message.getValue());
    learned = message.getValue();
  }
}
