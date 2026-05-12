package org.acme;


import jakarta.interceptor.Interceptor;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class MutexInterceptService {

    private static final ReentrantLock mutex =  new ReentrantLock();

    /**
     * Locks the mutex
     * @param requestContext
     */
    @ServerRequestFilter(priority = Interceptor.Priority.PLATFORM_BEFORE + 150)
    public void requestFilter(ContainerRequestContext requestContext) {
//        log.info("Trying for lock...");
//        mutex.lock();
//        log.info("Got Lock.");
    }

    @ServerResponseFilter()
    public void responseFilter(ContainerResponseContext responseContext) {
//        mutex.unlock();
//        log.info("Unlocked.");
    }
}
