package com.example.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.usermanagement.user.CreateUserRequest;
import com.example.usermanagement.user.UserRepository;
import com.example.usermanagement.user.UserResponse;
import com.example.usermanagement.user.UserService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class UserApiIT extends MySqlIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository users;

    @Autowired
    private UserService service;

    private final JsonMapper json = JsonMapper.builder().build();

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    @BeforeEach
    void cleanData() {
        users.deleteAllInBatch();
    }

    @Test
    void createsUser() throws Exception {
        HttpResponse<String> response = call("POST", "/users", """
                {
                    "name": "Ana Silva",
                    "email": "ana@example.com"
                }
                """);

        assertThat(response.statusCode()).isEqualTo(201);

        JsonNode body = json.readTree(response.body());
        long id = body.path("id").asLong();

        assertThat(id).isPositive();
        assertThat(body.path("name").asText()).isEqualTo("Ana Silva");
        assertThat(body.path("email").asText()).isEqualTo("ana@example.com");

        assertThat(users.findById(id).orElseThrow().getEmail()).isEqualTo("ana@example.com");
    }

    @Test
    void findsUser() throws Exception {
        UserResponse user = service.create(new CreateUserRequest("Ana Silva", "ana@example.com"));

        HttpResponse<String> response = call("GET", "/users/" + user.id(), null);

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode body = json.readTree(response.body());

        assertThat(body.path("id").asLong()).isEqualTo(user.id());
        assertThat(body.path("name").asText()).isEqualTo("Ana Silva");
        assertThat(body.path("email").asText()).isEqualTo("ana@example.com");
    }

    @Test
    void listsUsers() throws Exception {
        UserResponse user = service.create(new CreateUserRequest("Ana Silva", "ana@example.com"));

        HttpResponse<String> response = call("GET", "/users?page=0&size=10", null);

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode body = json.readTree(response.body());

        assertThat(body.path("content").size()).isEqualTo(1);
        assertThat(body.path("content").get(0).path("id").asLong()).isEqualTo(user.id());

        assertThat(body.path("totalElements").asLong()).isEqualTo(1);
        assertThat(body.path("size").asInt()).isEqualTo(10);
    }

    @Test
    void updatesUser() throws Exception {
        UserResponse user = service.create(new CreateUserRequest("Ana Silva", "ana@example.com"));

        HttpResponse<String> response = call("PUT", "/users/" + user.id(), """
                {
                    "name": "Ana Santos",
                    "email": "ana.santos@example.com"
                }
                """);

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode body = json.readTree(response.body());

        assertThat(body.path("id").asLong()).isEqualTo(user.id());
        assertThat(body.path("name").asText()).isEqualTo("Ana Santos");
        assertThat(body.path("email").asText()).isEqualTo("ana.santos@example.com");

        assertThat(users.findById(user.id()).orElseThrow().getName()).isEqualTo("Ana Santos");
    }

    @Test
    void deletesUser() throws Exception {
        UserResponse user = service.create(new CreateUserRequest("Ana Silva", "ana@example.com"));

        HttpResponse<String> response = call("DELETE", "/users/" + user.id(), null);

        assertThat(response.statusCode()).isEqualTo(204);
        assertThat(response.body()).isEmpty();

        assertThat(users.findById(user.id())).isEmpty();
    }

    private HttpResponse<String> call(String method, String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
            .timeout(Duration.ofSeconds(10))
            .header("Content-Type", "application/json")
            .method(method,
                    body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
            .build();

        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
