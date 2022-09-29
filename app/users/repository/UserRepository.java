package users.repository;

import users.model.User;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface UserRepository {
    CompletionStage<Stream<User>> findAll();
    CompletionStage<User> save(User user);
    CompletionStage<Optional<User>> findById(Long id);
    CompletionStage<Optional<User>> update(Long id, User user);
}