# JMS producer to test JMS->amqp message sending

1. Run shared Artemis broker:

    ```
    docker run --network host --add-host="broker:0.0.0.0" -e AMQ_USER=admin -e AMQ_PASSWORD=admin -e "AMQ_HOST=0.0.0.0" -e AMQ_HTTP_HOST=0.0.0.0 -e="AMQ_RELAX_JOLOKIA=true" quay.io/artemiscloud/activemq-artemis-broker:1.0.25
    ```

1. Update `App.java` to use ip at end of logs of broker
2. Enable / update app's artemis config (`amqp-*`) in main `application.yaml`
3. Run this to send a message: `./mvnw clean compile exec:java -Dexec.mainClass="com.example.App"`

