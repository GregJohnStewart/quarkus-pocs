package org.acme.extInterface.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.amqp.OutgoingAmqpMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.model.AppMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;


@Slf4j
@ApplicationScoped
public class MessageIngress {

    @Inject
    DownstreamProcessor dp;

    @Incoming("requests")
    @Outgoing("priority-queue-out")
    public Message<AppMessage> processToPriority(AppMessage quoteRequest) {
        log.info("Got request. Putting on priority queue: {}", quoteRequest);

        Message<AppMessage> output = Message.of(quoteRequest);
        output.addMetadata(
                OutgoingAmqpMetadata.builder()
                .withPriority(quoteRequest.getPriority())
                .build()
        );

        return output;
    }

    @Incoming("priority-queue-in")
    @Blocking("single-thread-pool")
    public void process(AppMessage quoteRequest) throws InterruptedException {
        log.info("Got request from priority queue: {}", quoteRequest);

//        Thread.sleep(5_60_1000);
        Thread.sleep(500);

        log.info("Finished processing request from priority queue: {}", quoteRequest.getName());
        dp.add(quoteRequest);
    }
}
