package com.jvmops.api.email.endpoints.emails

import com.jvmops.api.Main
import com.jvmops.api.email.endpoints.emails.ports.EmailMessageRepository
import com.jvmops.api.email.endpoints.emails.model.EmailMessageDto
import com.jvmops.api.email.endpoints.emails.model.Priority
import com.jvmops.api.email.endpoints.emails.model.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes= Main, webEnvironment = RANDOM_PORT)
class EmailCreationSpec extends Specification {

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

    def "POST /emails - if email was created expect its id to be returned"() {
        when:
        def response = restTemplate.postForEntity(emailsEndpoint, validEmailMessageDto(), EmailMessageDto)

        then:
        null != response.getBody().id
    }

    def "POST /emails - created email can be retrieved through API by its id"() {
        when:
        def savedEmailId = restTemplate.postForEntity(emailsEndpoint, validEmailMessageDto(), EmailMessageDto)
                .getBody()
                .getId()
        def emailDto = restTemplate.getForEntity("$emailsEndpoint/$savedEmailId", EmailMessageDto)
                .getBody();

        then:
        savedEmailId == emailDto.id
    }

    def "POST /emails - created email will have the lowest priority by default"() {
        when:
        def savedEmailId = restTemplate.postForEntity(emailsEndpoint, validEmailMessageDto(), EmailMessageDto)
                .getBody()
                .getId()
        def emailDto = restTemplate.getForEntity("$emailsEndpoint/$savedEmailId", EmailMessageDto)
                .getBody();

        then:
        Priority.LOW == emailDto.priority
    }

    def "POST /emails - you can pass custom priority while creating email"() {
        when:
        def savedEmailId = restTemplate.postForEntity(emailsEndpoint, validEmailMessageDto(Priority.HIGH), EmailMessageDto)
                .getBody()
                .getId()
        def emailDto = restTemplate.getForEntity("$emailsEndpoint/$savedEmailId", EmailMessageDto)
                .getBody();

        then:
        Priority.HIGH == emailDto.priority
    }

    def "POST /emails - created email will get PENDING status"() {
        when:
        def savedEmailId = restTemplate.postForEntity(emailsEndpoint, validEmailMessageDto(), EmailMessageDto)
                .getBody()
                .getId()
        def emailDto = restTemplate.getForEntity("$emailsEndpoint/$savedEmailId", EmailMessageDto)
                .getBody();

        then:
        Status.PENDING == emailDto.status
    }

    private static EmailMessageDto validEmailMessageDto() {
        return builder().build()
    }

    private static EmailMessageDto validEmailMessageDto(Priority priority) {
        return builder()
                .priority(priority)
                .build();
    }

    private static EmailMessageDto.EmailMessageDtoBuilder builder() {
        EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .recipients(Set.of("jvmops@gmail.com"))
                .topic("Important email")
                .body("The message")
    }
}
