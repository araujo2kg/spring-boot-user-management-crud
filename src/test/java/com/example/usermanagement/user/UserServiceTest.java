package com.example.usermanagement.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(repository);
    }

    @Test
    void createsUser() {
        when(repository.saveAndFlush(any(User.class))).thenAnswer(call -> call.getArgument(0));

        UserResponse result = service.create(new CreateUserRequest("Ana Silva", "ana@example.com"));

        assertThat(result.name()).isEqualTo("Ana Silva");
        assertThat(result.email()).isEqualTo("ana@example.com");
    }

    @Test
    void findsUser() {
        User user = new User("Ana Silva", "ana@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = service.find(1);

        assertThat(result.name()).isEqualTo("Ana Silva");
        assertThat(result.email()).isEqualTo("ana@example.com");
    }

    @Test
    void listsUsers() {
        PageRequest page = PageRequest.of(0, 20, Sort.by("id").ascending());
        User user = new User("Ana Silva", "ana@example.com");
        when(repository.findAll(page)).thenReturn(new PageImpl<>(List.of(user), page, 1));

        UserPageResponse result = service.list(0, 20);

        assertThat(result.content()).extracting(UserResponse::name).containsExactly("Ana Silva");

        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void updatesUser() {
        User user = new User("Ana Silva", "ana@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = service.update(1, new UpdateUserRequest("Ana Santos", "ana.santos@example.com"));

        assertThat(result.name()).isEqualTo("Ana Santos");
        assertThat(result.email()).isEqualTo("ana.santos@example.com");

        verify(repository).flush();
    }

    @Test
    void deletesUser() {
        User user = new User("Ana Silva", "ana@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(1);

        verify(repository).delete(user);
        verify(repository).flush();
    }

}
