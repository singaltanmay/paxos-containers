/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.controller;

import edu.sjsu.entity.PaxosMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaxosController {

    @PostMapping("message")
    public ResponseEntity<Void> incoming(@RequestBody PaxosMessage message) {
        System.out.println("Incoming message " + message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
