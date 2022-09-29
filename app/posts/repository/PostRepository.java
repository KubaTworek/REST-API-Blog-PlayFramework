package posts.repository;

import posts.model.Post;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface PostRepository {
    CompletionStage<Stream<Post>> findAll();
    CompletionStage<Post> save(Post post);
    CompletionStage<Optional<Post>> findById(Long id);
    CompletionStage<Optional<Post>> update(Long id, Post post);
}
