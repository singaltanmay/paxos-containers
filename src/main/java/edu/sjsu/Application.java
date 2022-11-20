/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu;

import edu.sjsu.api.RetrofitClient;
import edu.sjsu.api.Router;
import edu.sjsu.entity.Register;
import edu.sjsu.role.Proposer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        // Register with router as soon as application is started
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        Router router = retrofit.create(Router.class);
        Call<Void> call = router.register(new Register(PAXOS_ROLES.PROPOSER));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println("Registered with code : " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                System.err.println("Failed to send register");
            }
        });

        Proposer proposer = new Proposer();
        proposer.sendPrepareMessage("Dog");
    }

    public enum PAXOS_ROLES {PROPOSER, ACCEPTOR, LEARNER}
}