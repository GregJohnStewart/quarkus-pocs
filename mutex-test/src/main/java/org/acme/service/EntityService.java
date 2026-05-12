package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.acme.dao.MyEntity;

import jakarta.inject.Inject;
import java.time.Duration;
import java.util.Random;

@Slf4j
@ApplicationScoped
public class EntityService {

    private Random random = new Random();

    @Inject
    TransactionService transactionService;

    public MyEntity create(MyEntity entity) throws InterruptedException {
        try(TransactionService.TransactionResource resource = this.transactionService.getTransaction(true)){
            log.info("Creating entity...");
            entity.persist();
            Thread.sleep(
                    Duration.ofSeconds(random.nextInt(2, 5))
            );

            log.info("Created entity {}", entity);
        }
        return entity;
    }
}
