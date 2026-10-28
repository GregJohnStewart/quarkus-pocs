package org.acme.dao;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.model.AppMessage;
import org.acme.model.AppMetadata;
import org.acme.service.TransactionService;
import org.hibernate.StatelessSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class PriorityQueueService {
    private static final Sort PRIORITY_QUEUE_SORT = Sort.descending("priority");

//    @Inject
//    DataSource dataSource;

//    @Inject
//    StatelessSession statelessSession;

    @Inject
    TransactionService transactionService;

    public void pushQueueItem(int priority, AppMetadata metadata, AppMessage message) {
        try (TransactionService.TransactionResource transaction = this.transactionService.getTransaction(true)) {
            PriorityQueueEntry entry = PriorityQueueEntry.builder()
                    .priority(priority)
                    .metadata(metadata)
                    .payload(message)
                    .build();
            entry.persistAndFlush();
        }
    }

    public synchronized Optional<AppMessage> popQueueItem() {
        Optional<AppMessage> output;

        try (TransactionService.TransactionResource transactionResource = this.transactionService.getTransaction(true)) {
            PriorityQueueEntry result = PriorityQueueEntry.findAll(PRIORITY_QUEUE_SORT).firstResult();

            if (result != null) {
                result.delete();
                output = Optional.of(result.payload);
            } else {
                return Optional.empty();
            }
        }

        return output;
    }

    public void clearQueue() {
        try (TransactionService.TransactionResource transactionResource = this.transactionService.getTransaction(true)) {
            PriorityQueueEntry.deleteAll();
        }
    }


}
