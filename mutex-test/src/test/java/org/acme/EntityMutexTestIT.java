package org.acme;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.acme.extInterface.rest.EntityCrud;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@QuarkusIntegrationTest
@TestHTTPEndpoint(EntityCrud.class)
public class EntityMutexTestIT extends EntityMutexTest {

}
