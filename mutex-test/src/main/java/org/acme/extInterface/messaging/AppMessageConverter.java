package org.acme.extInterface.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.MessageConverter;
import io.smallrye.reactive.messaging.amqp.IncomingAmqpMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.model.AppMessage;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.lang.reflect.Type;

@Slf4j
@ApplicationScoped
public class AppMessageConverter implements MessageConverter {

    @Inject
    ObjectMapper mapper;

    @Override
    public boolean canConvert(Message<?> message, Type targetType) {
        // Confirm this converter applies to your specific POJO and AMQP payload
        return message.getMetadata(IncomingAmqpMetadata.class).isPresent()
                && targetType.equals(AppMessage.class);
    }

    @Override
    public Message<?> convert(Message<?> message, Type targetType) {

        Object payload = message.getPayload();

        log.info("Got message payload: {}", payload);

        if(payload instanceof JsonObject) {
            return message.withPayload(((JsonObject)payload).mapTo(AppMessage.class));
        } else if (payload instanceof String){
            try {
                return message.withPayload(this.mapper.readValue((String)payload, AppMessage.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Could not map incoming string value.", e);
            }
        }
        throw new IllegalArgumentException("Message coming in not expected type: " + payload.getClass());
    }
}
