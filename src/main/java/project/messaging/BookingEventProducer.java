package project.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import project.events.BookingEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

        kafkaTemplate.send(topic, key, event)
                .whenComplete((SendResult<String, BookingEvent> res, Throwable ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish booking {} (key={}) to topic {}",
                                event.getBookingId(), key, topic, ex);
                        kafkaTemplate.send("booking-errors", key, event);
                    } else {
                        var md = res.getRecordMetadata();
                        log.info("Published booking {} (key={}) to {}-{}@{}",
                                event.getBookingId(), key, md.topic(), md.partition(), md.offset());
                    }
                });
    }

}

