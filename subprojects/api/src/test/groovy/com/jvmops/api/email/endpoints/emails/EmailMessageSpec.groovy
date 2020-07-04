package com.jvmops.api.email.endpoints.emails

import com.jvmops.api.Main
import com.jvmops.api.email.endpoints.emails.model.EmailMessageDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes = Main, webEnvironment = RANDOM_PORT)
class EmailMessageSpec extends EmailsEndpointSpecification {
    @LocalServerPort
    private int port

    @Autowired
    private TestRestTemplate restTemplate

    private URI emailsEndpoint

    def setup() {
        emailsEndpoint = URI.create("http://localhost:$port/emails")
    }

    def "POST /emails - Validation - payload needs to be provided"() {
        given:
        def response = restTemplate.postForEntity(emailsEndpoint, null, EmailMessageDto)

        expect:
        response.statusCode.is4xxClientError()
    }

    def "POST /emails - Validation - email sender can't be null"() {
        given:
        def email = EmailMessageDto.builder()
                .recipients(Set.of("jvmops@gmail.com"))
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "POST /emails - Validation - email sender can't be an empty string"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("")
                .recipients(Set.of("jvmops@gmail.com"))
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "POST /emails - Validation - email sender needs to be a valid email address"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail..com")
                .recipients(Set.of("jvmops@gmail.com"))
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "POST /emails - Validation - email recipients might be null"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is2xxSuccessful()
    }

    def "POST /emails - Validation - email recipients may be an empty set"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .recipients(Set.of())
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is2xxSuccessful()
    }

    def "POST /emails - Validation - email recipients must contain valid email addresses"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .recipients(Set.of("jvmops@gmail.com", "jvmops@gm..com"))
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "POST /emails - Validation - email topic can't be empty"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .recipients(Set.of("jvmops@gmail.com", "jvmops@gm..com"))
                .topic("")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "POST /emails - Validation - mail body can't be empty"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .recipients(Set.of("jvmops@gmail.com", "jvmops@gm..com"))
                .topic("")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }
}