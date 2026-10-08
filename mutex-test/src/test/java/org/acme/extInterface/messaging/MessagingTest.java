package org.acme.extInterface.messaging;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.model.AppMessage;
import org.acme.testResources.TestingMessagingResource;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.awaitility.Awaitility.await;

@Slf4j
@QuarkusTest
@QuarkusTestResource(TestingMessagingResource.class)
public class MessagingTest {

    @Inject
    DownstreamProcessor processor;

    @BeforeEach
    void setup(){
        this.processor.reset();
    }

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector connector;

    private InMemorySource<AppMessage> getSender() {
        return connector.source("requests");
    }

    @Disabled
    @Test
    public void testMessageSend() {
        this.getSender().send(AppMessage.builder().name("test").priority((short) 5).build());

        await("wait for message to be processed.")
                .atMost(Duration.of(5, ChronoUnit.SECONDS))
                .until(() -> processor.getNumReceived() == 1);
    }

    @Test
    public void testMessagesSendLowToHigh() {

        for(short i = 0; i <= 9; i++){
            this.getSender().send(AppMessage.builder().name("test " + i).priority(i).build());
        }

        await("wait for message to be processed.")
                .atMost(Duration.of(1, ChronoUnit.MINUTES))
                .until(() -> processor.getNumReceived() == 10);

        log.info("Got messages (lth): {}", this.processor.getReceived());
    }

    @Test
    public void testMessagesSendHighToLow() throws InterruptedException {

        for(short i = 9; i >= 0; i--){
            this.getSender().send(AppMessage.builder().name("test " + i).priority(i).build());
        }

        await("wait for message to be processed.")
                .atMost(Duration.of(1, ChronoUnit.MINUTES))
                .until(() -> processor.getNumReceived() == 10);

        log.info("Got messages (htl): {}", this.processor.getReceived());
//        Thread.sleep(5_60_1000);
    }
}
