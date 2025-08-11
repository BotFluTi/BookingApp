package project.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import project.events.BookingEvent;

@Component
public class BookingEventProducer {

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;
    private final String topic;

    public BookingEventProducer(
            KafkaTemplate<String, BookingEvent> kafkaTemplate,
            @Value("${app.kafka.topic.booking}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(BookingEvent event) {
        String key = event.getUserId() != null ? event.getUserId().toString() : null;
        kafkaTemplate.send(topic, key, event);
    }
}

