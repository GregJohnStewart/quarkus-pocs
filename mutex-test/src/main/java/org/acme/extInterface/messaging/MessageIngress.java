package org.acme.extInterface.messaging;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@Slf4j
@ApplicationScoped
public class MessageIngress {

    @Incoming("requests")
    @Blocking
    public void process(String quoteRequest) throws InterruptedException {
        log.info("Got request: {}", quoteRequest);
    }
}
