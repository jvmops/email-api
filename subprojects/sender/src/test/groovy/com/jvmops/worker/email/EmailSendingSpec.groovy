package com.jvmops.worker.email

import com.jvmops.worker.Main
import com.jvmops.worker.email.adapters.DummyClient
import com.jvmops.worker.email.adapters.HighThroughputClient
import com.jvmops.worker.email.model.EmailMessage
import com.jvmops.worker.email.model.PendingEmailMessage
import com.jvmops.worker.email.model.Priority
import com.jvmops.worker.email.model.Status
import com.jvmops.worker.email.ports.EmailMessageRepository
import com.jvmops.worker.email.ports.EmailMessageTestRepository
import com.jvmops.worker.email.ports.SmtpClient
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static com.jvmops.worker.email.model.Status.*
import static com.jvmops.worker.email.ports.EmailMessageRepository.*
import static com.jvmops.worker.email.ports.SmtpClient.UnableToSendEmail
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(classes = Main, webEnvironment = NONE)
@ImportAutoConfiguration(exclude = RabbitAutoConfiguration.class)
class EmailSendingSpec extends Specification {
    private static final SmtpClient ALWAYS_SUCCESS = new DummyClient()
    private static final SmtpClient ALWAYS_FAIL = new HighThroughputClient()

    @Autowired
    EmailMessageTestRepository testRepository

    @Autowired
    EmailMessageRepository emailMessageRepository

    SendEmailCommand sendEmailCommand

    ObjectId messageId
    EmailMessage pendingEmail

    def setup() {
        messageId = ObjectId.get();
        pendingEmail = emailMessage(messageId)

        testRepository.deleteAll()
        testRepository.save(pendingEmail)
    }

    def "After email is dispatched expect message status changed to SENT"() {
        given:
        sendEmailCommand = new SendEmailCommand(ALWAYS_SUCCESS, emailMessageRepository)
        def pendingMessage = PendingEmailMessage.builder()
                .id(messageId)
                .build()

        when:
        sendEmailCommand.send(pendingMessage)

        then:
        SENT == emailMessageRepository.findById(messageId)
                .map() { it.getStatus() }
                .orElseGet() { messageWithStatus(ABORT) }
    }

    def "If message can't be sent leave its PENDING status be strategy"() {
        given:
        sendEmailCommand = new SendEmailCommand(ALWAYS_FAIL, emailMessageRepository)
        def pendingMessage = PendingEmailMessage.builder()
                .id(messageId)
                .build()

        when:
        sendEmailCommand.send(pendingMessage)

        then:
        thrown UnableToSendEmail

        and:
        PENDING == emailMessageRepository.findById(messageId)
                .map() { email -> email.getStatus() }
                .orElse(ABORT)
    }

    def "You can change the message status to error"() {
        given:
        def pendingMessage = PendingEmailMessage.builder()
                .id(messageId)
                .build()

        when:
        emailMessageRepository.error(pendingMessage)

        then:
        ABORT == emailMessageRepository.findById(messageId)
                .map() { email -> email.getStatus() }
                .orElse(PENDING)

    }

    def "You can change the message status to error and provide the cause of the fail"() {
        given:
        def pendingMessage = PendingEmailMessage.builder()
                .id(messageId)
                .build()

        when:
        emailMessageRepository.error(pendingMessage, new NuSuchEmailMessage(messageId))

        then:
        ABORT == emailMessageRepository.findById(messageId)
                .map() { email -> email.getStatus() }
                .orElse(PENDING)
    }

    static EmailMessage emailMessage(ObjectId id) {
        EmailMessage.builder()
                .id(id)
                .sender("me+test@jvmops.com")
                .recipients(["you+test@jvmops.com"].toSet())
                .topic("Some irrelevant test email")
                .body("Even move irrelevant stuff is here do not bother to read it.")
                .status(PENDING)
                .priority(Priority.LOW)
                .build()
    }

    private EmailMessage messageWithStatus(Status status) {
        EmailMessage.builder()
                .id(messageId)
                .status(status)
                .build()
    }
}
