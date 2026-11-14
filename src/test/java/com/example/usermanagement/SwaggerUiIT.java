package com.example.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class SwaggerUiIT extends MySqlIntegrationTest {

    @LocalServerPort
    private int port;

    private final HttpClient http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    @Test
    void servesSwaggerUi() throws Exception {
        HttpResponse<String> response = get("/swagger-ui.html");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("swagger-ui-bundle.js", "swagger-initializer.js");

        assertThat(get("/swagger-ui/swagger-ui-bundle.js").statusCode()).isEqualTo(200);
        assertThat(get("/swagger-ui/swagger-initializer.js").body()).contains("/v3/api-docs/swagger-config");

        HttpResponse<String> configuration = get("/v3/api-docs/swagger-config");

        assertThat(configuration.statusCode()).isEqualTo(200);
        assertThat(JsonMapper.builder().build().readTree(configuration.body()).path("url").asText())
            .isEqualTo("/v3/api-docs");
    }

    @Test
    void documentsUserEndpoints() throws Exception {
        HttpResponse<String> response = get("/v3/api-docs");

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode document = JsonMapper.builder().build().readTree(response.body());
        JsonNode paths = document.path("paths");

        assertThat(document.path("info").path("title").asText()).isEqualTo("User Management API");

        assertThat(paths.path("/users").path("post").path("responses").has("201")).isTrue();
        assertThat(paths.path("/users").path("get").path("responses").has("200")).isTrue();
        assertThat(paths.path("/users/{id}").path("get").path("responses").has("200")).isTrue();
        assertThat(paths.path("/users/{id}").path("put").path("responses").has("200")).isTrue();
        assertThat(paths.path("/users/{id}").path("delete").path("responses").has("204")).isTrue();

        JsonNode request = document.path("components").path("schemas").path("CreateUserRequest");

        assertThat(request.path("properties").path("email").path("example").asText()).isEqualTo("ana@example.com");
        assertThat(paths.path("/users").path("post").path("responses").path("201").toString())
            .contains("#/components/schemas/UserResponse");
        assertThat(paths.path("/users/{id}").path("get").path("responses").path("200").toString())
            .contains("#/components/schemas/UserResponse");
        assertThat(paths.path("/users").path("get").path("responses").path("200").toString())
            .contains("#/components/schemas/UserPageResponse");
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();

        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
