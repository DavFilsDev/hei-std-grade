package school.hei.stdgrade.conf;

import static java.lang.Runtime.getRuntime;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

// Copied as-is from cine-app: no structural adaptation needed.
@ActiveProfiles("dev")
public abstract class TestcontainersConfigurer extends FacadeIT {
  @ServiceConnection
  static final PostgreSQLContainer<?> PSQL_CONTAINER = new PostgreSQLContainer<>("postgres:16");

  @Autowired TestRestTemplate testRestTemplate;

  protected TestUtils utils;

  @BeforeEach
  void setup() {
    utils = new TestUtils(testRestTemplate);
  }

  static {
    PSQL_CONTAINER.start();
    getRuntime().addShutdownHook(new Thread(PSQL_CONTAINER::stop));
  }
}
