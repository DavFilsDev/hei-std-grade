package school.hei.stdgrade.handler;

import static java.lang.System.getenv;
import static java.lang.Thread.currentThread;
import static school.hei.stdgrade.concurrency.ThreadRenamer.renameWorkerThread;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import school.hei.stdgrade.PojaApplication;
import school.hei.stdgrade.PojaGenerated;
import school.hei.stdgrade.endpoint.EndpointConf;
import school.hei.stdgrade.endpoint.event.EventConf;
import school.hei.stdgrade.endpoint.event.consumer.EventConsumer;
import school.hei.stdgrade.endpoint.event.consumer.model.ConsumableEvent;
import school.hei.stdgrade.endpoint.event.consumer.model.ConsumableEventTyper;
import software.amazon.awssdk.regions.Region;

@Slf4j
@PojaGenerated
public class MailboxEventHandler implements RequestHandler<SQSEvent, String> {

  public static final String SPRING_SERVER_PORT_FOR_RANDOM_VALUE = "0";
  private final ConsumableEventTyper consumableEventTyper =
      new ConsumableEventTyper(
          new EndpointConf().objectMapper(), new EventConf(Region.of(getenv("AWS_REGION"))));

  @Override
  public String handleRequest(SQSEvent event, Context context) {
    renameWorkerThread(currentThread());
    log.info("Received: event={}, awsReqId={}", event, context.getAwsRequestId());
    List<SQSMessage> messages = event.getRecords();
    consumableEventTyper
        .apply(messages)
        .forEach(ConsumableEvent::newRandomVisibilityTimeout); // note(init-visibility)
    log.info("SQS messages: {}", messages);

    var applicationContext = applicationContext();
    var eventConsumer = applicationContext.getBean(EventConsumer.class);
    var messageConverter = applicationContext.getBean(ConsumableEventTyper.class);

    eventConsumer.accept(messageConverter.apply(messages));
    applicationContext.close();
    return "ok";
  }

  private ConfigurableApplicationContext applicationContext(String... args) {
    SpringApplication application = new SpringApplication(PojaApplication.class);
    application.setDefaultProperties(Map.of("server.port", SPRING_SERVER_PORT_FOR_RANDOM_VALUE));
    application.setAdditionalProfiles("worker");
    return application.run(args);
  }
}
