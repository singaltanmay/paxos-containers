/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.entity;

import edu.sjsu.Application;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class PaxosMessage {

  @Getter
  @Setter
  long id;

  @Getter
  @Setter
  PAXOS_MESSAGE_TYPE messageType;

  @Getter
  @Setter
  String value;

  @Getter
  String messageSource = Application.PAXOS_NODE_UUID;

  @Getter
  @Setter
  String messageDestination;

  @Getter
  @Setter
  int numProposers;

  @Getter
  @Setter
  int numAcceptors;

  @Getter
  @Setter
  int numLearners;

  @Getter
  boolean ignore;

  public PaxosMessage setIgnore(boolean ignore) {
    this.ignore = ignore;
    return this;
  }

  public PaxosMessage(long id, PAXOS_MESSAGE_TYPE messageType, String value, String messageDestination) {
    this.id = id;
    this.messageType = messageType;
    this.value = value;
    this.messageDestination = messageDestination;
  }

  public PaxosMessage(PAXOS_MESSAGE_TYPE messageType, String value) {
    this.id = System.currentTimeMillis();
    this.messageType = messageType;
    this.value = value;
  }

  public static PaxosMessage respondTo(PaxosMessage incoming, PAXOS_MESSAGE_TYPE messageType) {
    return new PaxosMessage(incoming.getId(), messageType, incoming.getValue(), incoming.getMessageSource());
  }

  public enum PAXOS_MESSAGE_TYPE {PROMISE, ACCEPT_REQUEST, ACCEPT, PROPOSAL}

}
