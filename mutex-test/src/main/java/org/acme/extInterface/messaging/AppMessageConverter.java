package org.acme.extInterface.messaging;

import io.smallrye.reactive.messaging.MessageConverter;
import io.smallrye.reactive.messaging.amqp.IncomingAmqpMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import io.vertx.core.json.JsonObject;
import org.acme.model.AppMessage;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.lang.reflect.Type;

@ApplicationScoped
public class AppMessageConverter implements MessageConverter {

    @Override
    public boolean canConvert(Message<?> message, Type targetType) {
        // Confirm this converter applies to your specific POJO and AMQP payload
        return message.getMetadata(IncomingAmqpMetadata.class).isPresent()
                && targetType.equals(AppMessage.class);
    }

    @Override
    public Message<?> convert(Message<?> message, Type targetType) {
        return message.withPayload(((JsonObject) message.getPayload()).mapTo(AppMessage.class));
    }
}
