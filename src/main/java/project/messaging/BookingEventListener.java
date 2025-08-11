package project.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import project.events.BookingEvent;

@Component
public class BookingEventListener {

    private final JavaMailSender mailSender;
    private final String from;

    public BookingEventListener(JavaMailSender mailSender,
                                @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @KafkaListener(topics = "${app.kafka.topic.booking}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(BookingEvent event) {
        if (event.getUserEmail() == null || event.getUserEmail().isBlank()) return;

        String subject;
        String text;

        switch (event.getType()) {
            case CREATED -> {
                subject = "Your reservation is confirmed";
                text = String.format("""
                        Hi %s,

                        Your reservation (#%d) for room %d, %s → %s, has been CONFIRMED.

                        Thank you,
                        Hotel Horizon
                        """,
                        safe(event.getUsername()), event.getBookingId(), event.getRoomId(),
                        event.getCheckIn(), event.getCheckOut());
            }
            case CANCELLED -> {
                subject = "Your reservation has been cancelled";
                text = String.format("""
                        Hi %s,

                        Your reservation (#%d) has been CANCELLED.
                        Period: %s → %s, room %d.

                        If this cancellation was initiated by us due to the room becoming unavailable, you'll receive a full refund. 
                        If you have any questions, please contact our support.

                        Hotel Horizon
                        """,
                        safe(event.getUsername()), event.getBookingId(),
                        event.getCheckIn(), event.getCheckOut(), event.getRoomId());
            }
            default -> { return; }
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(event.getUserEmail());
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    private String safe(String s) { return s == null ? "" : s; }
}
