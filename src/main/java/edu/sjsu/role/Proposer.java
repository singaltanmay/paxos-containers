/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu.role;

import edu.sjsu.api.RetrofitClient;
import edu.sjsu.api.Router;
import edu.sjsu.entity.PaxosMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static edu.sjsu.entity.PaxosMessage.PAXOS_MESSAGE_TYPE.PROPOSAL;

public class Proposer {

    private final Router router;

    public Proposer() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        router = retrofit.create(Router.class);
    }

    /**
     * First step in Paxos. Send a new PREPARE message to {@link Acceptor}
     */
    public PaxosMessage sendPrepareMessage(String value) {
        PaxosMessage message = new PaxosMessage(PROPOSAL, value);
        Call<Void> call = router.sendMessage(message);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println("Sent message " + message + " with code : " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                System.err.println("Failed to send message " + message);
            }
        });
        return message;
    }

}
