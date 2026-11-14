package com.example.usermanagement.user;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.usermanagement.config.JsonConfiguration;
import com.example.usermanagement.error.ApiExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserController.class)
@Import({ ApiExceptionHandler.class, JsonConfiguration.class })
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService service;

    @Test
    void createsUser() throws Exception {
        when(service.create(new CreateUserRequest("Ana Silva", "ana@example.com")))
            .thenReturn(new UserResponse(1L, "Ana Silva", "ana@example.com"));

        ResultActions response = mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("""
                {
                    "name": "Ana Silva",
                    "email": "ana@example.com"
                }
                """));

        response.andExpect(status().isCreated());
        response.andExpect(header().string("Location", "/users/1"));

        response.andExpect(jsonPath("$.id").value(1));
        response.andExpect(jsonPath("$.name").value("Ana Silva"));
        response.andExpect(jsonPath("$.email").value("ana@example.com"));
    }

    @Test
    void findsUser() throws Exception {
        when(service.find(1)).thenReturn(new UserResponse(1L, "Ana Silva", "ana@example.com"));

        ResultActions response = mvc.perform(get("/users/1"));

        response.andExpect(status().isOk());

        response.andExpect(jsonPath("$.id").value(1));
        response.andExpect(jsonPath("$.name").value("Ana Silva"));
        response.andExpect(jsonPath("$.email").value("ana@example.com"));
    }

    @Test
    void listsUsers() throws Exception {
        UserResponse user = new UserResponse(1L, "Ana Silva", "ana@example.com");
        when(service.list(0, 20)).thenReturn(new UserPageResponse(List.of(user), 0, 20, 1, 1));

        ResultActions response = mvc.perform(get("/users"));

        response.andExpect(status().isOk());

        response.andExpect(jsonPath("$.content[0].id").value(1));
        response.andExpect(jsonPath("$.content[0].name").value("Ana Silva"));

        response.andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void updatesUser() throws Exception {
        when(service.update(1, new UpdateUserRequest("Ana Santos", "ana.santos@example.com")))
            .thenReturn(new UserResponse(1L, "Ana Santos", "ana.santos@example.com"));

        ResultActions response = mvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON).content("""
                {
                    "name": "Ana Santos",
                    "email": "ana.santos@example.com"
                }
                """));

        response.andExpect(status().isOk());

        response.andExpect(jsonPath("$.id").value(1));
        response.andExpect(jsonPath("$.name").value("Ana Santos"));
        response.andExpect(jsonPath("$.email").value("ana.santos@example.com"));
    }

    @Test
    void deletesUser() throws Exception {
        ResultActions response = mvc.perform(delete("/users/1"));

        response.andExpect(status().isNoContent());
    }

}
