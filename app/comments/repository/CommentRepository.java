package comments.repository;

import comments.model.Comment;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface CommentRepository {
    CompletionStage<Stream<Comment>> findAll();
    CompletionStage<Comment> save(Comment comment);
    CompletionStage<Optional<Comment>> findById(Long id);
    CompletionStage<Optional<Comment>> update(Long id, Comment comment);
}
