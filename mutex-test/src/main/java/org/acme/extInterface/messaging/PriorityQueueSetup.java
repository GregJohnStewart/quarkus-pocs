package org.acme.extInterface.messaging;

import io.smallrye.common.annotation.Identifier;
import io.vertx.amqp.AmqpClientOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@ApplicationScoped
public class PriorityQueueSetup {
    @Produces
    @Identifier("priority-queue-args") // This name maps to your application.properties
    public Map<String, Object> priorityQueueArguments() {
        log.info("Getting priority queue args.");
        return Map.of("x-max-priority", 10); // Sets max priority level to 10
    }

//    @Produces
//    @Identifier("my-options")
//    public AmqpClientOptions getNamedOptions() {
//
//        return new AmqpClientOptions()
//                .set;
//    }
}
