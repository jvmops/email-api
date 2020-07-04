package com.jvmops.api.email.endpoints.emails

import com.jvmops.api.Main
import com.jvmops.api.email.endpoints.emails.model.EmailMessage
import com.jvmops.api.email.endpoints.emails.model.EmailMessagesDto
import com.jvmops.api.email.endpoints.emails.model.Priority
import com.jvmops.api.email.endpoints.emails.model.Status
import com.jvmops.api.email.endpoints.emails.ports.EmailMessageRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes = Main, webEnvironment = RANDOM_PORT)
class EmailPaginationSpec extends EmailsEndpointSpecification {
    private static final Set<ObjectId> EMAIL_IDS
    private static final ObjectId RANDOM_EXISTING_ID
    private static final Set<EmailMessage> EMAIL_MESSAGES

    static {
        EMAIL_IDS = generateIdsForTest()
        RANDOM_EXISTING_ID = EMAIL_IDS.first()
        EMAIL_MESSAGES = generateEmailMessages(EMAIL_IDS)
        assert RANDOM_EXISTING_ID != null
    }

    static Set<ObjectId> generateIdsForTest() {
        return (1..32).collect { ObjectId.get() }
                .toSet()
    }

    static Set<EmailMessage> generateEmailMessages(Set<ObjectId> objectIds) {
        objectIds.collect { id ->
            EmailMessage.builder()
                    .id(id)
                    .sender("me+test@jvmops.com")
                    .recipients(["you+test@jvmops.com"].toSet())
                    .topic("Some irrelevant test email")
                    .body("Even move irrelevant stuff is here do not bother to read it.")
                    .status(Status.PENDING)
                    .priority(Priority.LOW)
                    .build()
        }.toSet()
    }

    @Autowired
    private EmailMessageRepository emailMessages

    @LocalServerPort
    private int port

    @Autowired
    private TestRestTemplate restTemplate

    private URI emailsEndpoint

    def setup() {
        emailsEndpoint = URI.create("http://localhost:$port/emails")
        emailMessages.deleteAll()
    }

    def "GET /emails - Pagination - test dataset contains multiple pages of emails"() {
        when: "no pagination info"
        def body = restTemplate.getForEntity(emailsEndpoint, EmailMessagesDto)
                .body
        then:
        body.maxPages > 0
        and: "0 is a first page number"
        body.pageNumber == 0
    }
}
