package org.acme.dao;

import io.quarkus.scheduler.Scheduler;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.model.AppMessage;
import org.acme.model.AppMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@QuarkusTest
class PriorityQueueServiceTest {

    @Inject
    PriorityQueueService queueService;

    @Inject
    Scheduler scheduler;

    @BeforeEach
    public void setup(){
        this.scheduler.pause("queueProcessor");
        this.queueService.clearQueue();
    }
    @AfterEach
    public void teardown(){
        this.queueService.clearQueue();
        this.scheduler.resume("queueProcessor");
    }

    @Test
    public void testPriorityQueueService(){
        assertTrue(this.queueService.popQueueItem().isEmpty());

        final int start = 0;
        final int end = 1_000;

        for (int i = start; i <= end; i++){
            log.info("Pushing queue item: {}", i);
            queueService.pushQueueItem(
                    i,
                    AppMetadata.builder().correlationId(UUID.randomUUID().toString()).build(),
                    AppMessage.builder().name("Message " + i).priority((short) i).build()
            );
            log.info("Pushed queue item: {}", i);
        }

        for (int i = end; i >= start; i--){
            log.info("Popping queue item.");
            Optional<AppMessage> curResultOp = queueService.popQueueItem();
            log.info("Popped queue item.");

            assertTrue(curResultOp.isPresent());

            AppMessage message = curResultOp.get();

            log.info("Popped queue item: {}", message);

            assertEquals(i, message.getPriority());
        }
    }
}