package school.hei.stdgrade.endpoint.event;

import static java.lang.System.getenv;

import lombok.Getter;
import school.hei.stdgrade.PojaGenerated;

@PojaGenerated
public enum EventStack {
  EVENT_STACK_1(getenv("AWS_EVENT_STACK_1_SQS_QUEUE_URL"));

  @Getter private final String sqsQueueUrl;

  EventStack(String sqsQueueUrl) {
    this.sqsQueueUrl = sqsQueueUrl;
  }
}
