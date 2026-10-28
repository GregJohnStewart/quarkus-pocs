package org.acme.extInterface.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.amqp.IncomingAmqpMetadata;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.amqp.OutgoingAmqpMetadata;
import io.smallrye.reactive.messaging.providers.locals.LocalContextMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.acme.dao.PriorityQueueService;
import org.acme.model.AppMessage;
import org.acme.model.AppMetadata;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@ApplicationScoped
public class MessageIngress {

    @Inject
    DownstreamProcessor dp;

    @Inject
    PriorityQueueService pq;

    @Incoming("requests")
//    @Outgoing("priority-queue-out")
    public Uni<Void> processToPriority(Message<AppMessage> quoteRequestMessage) {
        return Uni.createFrom().item(()->{
            AppMessage quoteRequest = quoteRequestMessage.getPayload();
            short priority = quoteRequest.getPriority();
            log.info("Got request. Putting on priority queue: {}/{}", quoteRequest, priority);


            quoteRequestMessage.getMetadata()
                    .iterator().forEachRemaining((o)->{
                        log.info("Metadata object from requests: {}", o);
                    });

            this.pq.pushQueueItem(
                    priority,
                    AppMetadata.builder().correlationId(UUID.randomUUID().toString()).build(),
                    quoteRequest
            );
            return null;
        });

    }

    @Scheduled(every = "100m", concurrentExecution = Scheduled.ConcurrentExecution.SKIP, identity = "queueProcessor")
    public void processFromQueue() throws InterruptedException {
        log.debug("Running queue check thread.");

        Optional<AppMessage> messageOp = this.pq.popQueueItem();
        while(messageOp.isPresent()) {
            AppMessage quoteRequest = messageOp.get();

            log.info("Got request from priority queue: {} / ", quoteRequest);

            try {
//        Thread.sleep(5_60_1000);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            log.info("Finished processing request from priority queue: {}", quoteRequest.getName());
            dp.add(quoteRequest);

            messageOp = this.pq.popQueueItem();
        }
    }
}
