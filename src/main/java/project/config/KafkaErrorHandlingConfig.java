package project.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
public class KafkaErrorHandlingConfig {

    @Value("${app.kafka.topic.booking.errors}")
    private String bookingErrorsTopic;

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> template) {
        var recoverer = new DeadLetterPublishingRecoverer(
                template,
                (record, ex) -> new TopicPartition(bookingErrorsTopic, record.partition())
        );

        var backoff = new ExponentialBackOff(1000, 2.0);
        backoff.setMaxElapsedTime(15000);

        var handler = new DefaultErrorHandler(recoverer, backoff);
        handler.addNotRetryableExceptions(org.apache.kafka.common.errors.SerializationException.class);
        return handler;
    }
}
