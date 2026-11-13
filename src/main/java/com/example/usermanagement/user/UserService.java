package com.example.usermanagement.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    /**
     * Creates a user; the database constraint also protects against concurrent duplicate
     * requests.
     */
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (users.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        return UserResponse.from(users.saveAndFlush(new User(request.name(), request.email())));
    }

    public UserResponse find(long id) {
        return UserResponse.from(requireUser(id));
    }

    /** Bounds the offset before passing it to JPA's integer-based pagination API. */
    public UserPageResponse list(int page, int size) {
        if (page < 0 || size < 1 || size > 100 || (long) page * size > Integer.MAX_VALUE) {
            throw new InvalidPaginationException();
        }

        var result = users.findAll(PageRequest.of(page, size, Sort.by("id").ascending()));

        return new UserPageResponse(result.map(UserResponse::from).getContent(), page, size, result.getTotalElements(),
                result.getTotalPages());
    }

    @Transactional
    public UserResponse update(long id, UpdateUserRequest request) {
        User user = requireUser(id);

        if (users.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateEmailException();
        }

        user.update(request.name(), request.email());
        users.flush();

        return UserResponse.from(user);
    }

    @Transactional
    public void delete(long id) {
        users.delete(requireUser(id));
        users.flush();
    }

    private User requireUser(long id) {
        return users.findById(id).orElseThrow(UserNotFoundException::new);
    }

}
