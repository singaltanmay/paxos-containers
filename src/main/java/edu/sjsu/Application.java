/*
 * SJSU CS 218 FALL 2022 TEAM 5
 */

package edu.sjsu;

import edu.sjsu.api.RetrofitClient;
import edu.sjsu.api.Router;
import edu.sjsu.entity.Register;
import java.util.Random;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SpringBootApplication
public class Application {

  private static final Logger LOGGER = LogManager.getLogger(Application.class);
  public static PAXOS_ROLES APPLICATION_PAXOS_ROLE;

  public static String ROUTER_BASE_URL;

  public static String PAXOS_NODE_UUID = UUID.randomUUID().toString();

  public static void main(String[] args) {
    ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

    String paxosRoleArg = System.getProperty("role");
    PAXOS_ROLES[] paxosRoles = PAXOS_ROLES.values();
    if (paxosRoleArg != null && !paxosRoleArg.isBlank()) {
      for (PAXOS_ROLES paxosRole : paxosRoles) {
        if (paxosRole.name().equals(paxosRoleArg.toUpperCase())) {
          APPLICATION_PAXOS_ROLE = paxosRole;
          break;
        }
      }
    }
    if (APPLICATION_PAXOS_ROLE == null) {
      APPLICATION_PAXOS_ROLE = paxosRoles[new Random().nextInt(paxosRoles.length)];
    }
    LOGGER.info("Application started and assumed the role of " + APPLICATION_PAXOS_ROLE);

    // Register with router as soon as application is started
    registerWithRouter(RetrofitClient.getRetrofitInstance().create(Router.class), applicationContext, 3);
  }

  private static void registerWithRouter(Router router, ConfigurableApplicationContext applicationContext, int attempts) {
    if (attempts < 1) {
      LOGGER.debug("Unable to register with router. Exhausted registration attempts");
      SpringApplication.exit(applicationContext);
    }
    final Register registerCall = new Register(APPLICATION_PAXOS_ROLE, PAXOS_NODE_UUID);
    Call<Void> call = router.register(registerCall);
    call.enqueue(new Callback<>() {
      @Override
      public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.isSuccessful()) {
          LOGGER.info("Registered as " + PAXOS_NODE_UUID + " with code : " + response.code());
        } else {
          PAXOS_NODE_UUID = UUID.randomUUID().toString();
          LOGGER.debug("Failed registration with router. Retrying with new UUID: " + PAXOS_NODE_UUID);
          registerWithRouter(router, applicationContext, attempts - 1);
        }
      }

      @Override
      public void onFailure(Call<Void> call, Throwable throwable) {
        LOGGER.debug("Failed to register with the router at " + call.request().url() + ". Shutting down.");
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