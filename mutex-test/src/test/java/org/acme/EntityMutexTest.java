package org.acme;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.acme.dao.MyEntity;
import org.acme.rest.EntityCrud;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@QuarkusTest
@TestHTTPEndpoint(EntityCrud.class)
public class EntityMutexTest {


    public static Stream<Arguments> getParams() {
        return Stream.of(
                Arguments.of(2, 10, Duration.of(250, ChronoUnit.MILLIS)),
                Arguments.of(3, 10, Duration.of(250, ChronoUnit.MILLIS)),
                Arguments.of(5, 10, Duration.of(250, ChronoUnit.MILLIS)),
                Arguments.of(10, 10, Duration.of(250, ChronoUnit.MILLIS)),
                Arguments.of(20, 20, Duration.of(150, ChronoUnit.MILLIS))
        );
    }


    @ParameterizedTest
    @MethodSource("getParams")
    public void threadTest(int numThreads, int numIterations, Duration workDuration) throws InterruptedException, ExecutionException {
        String mutexId = "testMutex2";
        List<Future<List<ThreadResult>>> futures = new ArrayList<>(numThreads);
        SortedSet<ThreadResult> results = new TreeSet<>();
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        TestThread.TestThreadBuilder threadBuilder = TestThread.builder()
                .mutexId(mutexId)
                .numIterations(numIterations)
                .durationOfWork(workDuration);

        for (int i = 1; i <= numThreads; i++) {
            threadBuilder.threadId("testThread-" + i);

            futures.add(executor.submit(threadBuilder.build()));
        }
        executor.shutdown();
        while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            log.info("Still waiting on threads...");
        }

        for (Future<List<ThreadResult>> future : futures) {
            results.addAll(future.get());
        }

        assertEquals(numIterations * numThreads, results.size());

        //TODO:: check results
        log.info("Results: {}", results);

        Iterator<ThreadResult> iterator = results.iterator();
        ThreadResult cur = iterator.next();
        while (iterator.hasNext()) {
            ThreadResult next = iterator.next();

//            assertTrue(
//                    next.getStart().isAfter(cur.getStart()),
//                    "result " + cur + " start overlaps with the next result " + next + " (next start is before cur start)"
//            );
//            assertTrue(
//                    (next.getStart().isAfter(cur.getEnd()) || next.getStart().equals(cur.getEnd())),
//                    "result " + cur + " overlaps with the next result " + next + " (next start is before cur end)"
//            );

            cur = next;
        }

    }


    @Builder
    @Data
    @AllArgsConstructor
    static
    class ThreadResult implements Comparable<ThreadResult> {
        private String threadId;
        private LocalDateTime start;
        private LocalDateTime end;

        @Override
        public int compareTo(@NonNull ThreadResult threadResult) {
            return this.getStart().compareTo(threadResult.getStart());
        }
    }

    @Builder
    @Slf4j
    @AllArgsConstructor
    static class TestThread implements Callable<List<ThreadResult>> {

        private String mutexId;
        private String threadId;
        private int numIterations;
        private Duration durationOfWork;

        @SneakyThrows
        @Override
        public List<ThreadResult> call() {
            log.info("Running test thread {}", this.threadId);

//			Thread.sleep(500);

            List<ThreadResult> results = new ArrayList<>(this.numIterations);
            for (int i = 1; i <= this.numIterations; i++) {
                log.info("Thread {} waiting for lock on iteration {}", this.threadId, i);

                MyEntity entityOut = MyEntity.builder().field(this.threadId + "-" + i).build();

                MyEntity entityIn = given()
                        .contentType(ContentType.JSON)
                        .body(entityOut)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().body().as(MyEntity.class);


                assertEquals(entityOut.field, entityIn.field);


                log.info("Thread {} done doing work & released lock on iteration {}; {}/{}", this.threadId, i, entityOut.field, entityIn.id);
//                results.add(resultBuilder.build());
            }
            log.info("DONE running test thread {}", this.threadId);
            return results;
        }
    }
}
