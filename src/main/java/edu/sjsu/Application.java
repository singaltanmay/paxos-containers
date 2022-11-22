/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu;

import edu.sjsu.api.RetrofitClient;
import edu.sjsu.api.Router;
import edu.sjsu.entity.Register;
import edu.sjsu.role.Proposer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.Random;

@SpringBootApplication
public class Application {

  public static PAXOS_ROLES applicationPaxosRole;

  public static String ROUTER_BASE_URL;

  public static void main(String[] args) {
    ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

    String paxosRoleArg = System.getProperty("role");
    PAXOS_ROLES[] paxosRoles = PAXOS_ROLES.values();
    if (paxosRoleArg != null && !paxosRoleArg.isBlank()) {
      for (PAXOS_ROLES paxosRole : paxosRoles) {
        if (paxosRole.name().equals(paxosRoleArg.toUpperCase())) {
          applicationPaxosRole = paxosRole;
          break;
        }
      }
    }
    if (applicationPaxosRole == null) {
      applicationPaxosRole = paxosRoles[new Random().nextInt(paxosRoles.length)];
    }
    System.out.println("Application started and assumed the role of " + applicationPaxosRole);

    // Register with router as soon as application is started
    Retrofit retrofit = RetrofitClient.getRetrofitInstance();
    Router router = retrofit.create(Router.class);
    Call<Void> call = router.register(new Register(applicationPaxosRole));
    call.enqueue(new Callback<>() {
      @Override
      public void onResponse(Call<Void> call, Response<Void> response) {
        System.out.println("Registered with code : " + response.code());
        Proposer proposer = new Proposer();
        proposer.sendPrepareMessage("Dog");
      }

      @Override
      public void onFailure(Call<Void> call, Throwable throwable) {
        System.err.println("Failed to register with the router at " + call.request().url() + ". Shutting down.");
        SpringApplication.exit(applicationContext);
      }
    });
  }

  @Value("${router.baseurl}")
  private void setRouterBaseUrl(String baseUrl) {
    Application.ROUTER_BASE_URL = baseUrl;
  }

  public enum PAXOS_ROLES {PROPOSER, ACCEPTOR, LEARNER}
}