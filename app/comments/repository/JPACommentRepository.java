package comments.repository;

import comments.model.Comment;
import database.DatabaseExecutionContext;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPACommentRepository implements CommentRepository{

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;
    private final CircuitBreaker<Optional<Comment>> circuitBreaker = new CircuitBreaker<Optional<Comment>>().withFailureThreshold(1).withSuccessThreshold(3);

    @Inject
    public JPACommentRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Stream<Comment>> findAll() {
        return supplyAsync(() -> wrap(this::select), executionContext);
    }

    @Override
    public CompletionStage<Comment> save(Comment comment) {
        return supplyAsync(() -> wrap(em -> insert(em,comment)), executionContext);
    }

    @Override
    public CompletionStage<Optional<Comment>> findById(Long id) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> lookup(em, id))), executionContext);
    }

    @Override
    public CompletionStage<Optional<Comment>> update(Long id, Comment comment) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> modify(em, id, comment))), executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Optional<Comment> lookup(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    private Stream<Comment> select(EntityManager em) {
        TypedQuery<Comment> query = em.createQuery("SELECT p FROM Comment p", Comment.class);
        return query.getResultList().stream();
    }

    private Optional<Comment> modify(EntityManager em, Long id, Comment comment) throws InterruptedException {
        final Comment tempComment = em.find(Comment.class, id);
        if (tempComment != null) {
            tempComment.content = comment.content;
        }
        Thread.sleep(10000L);
        return Optional.ofNullable(tempComment);
    }

    private Comment insert(EntityManager em, Comment comment) {
        return em.merge(comment);
    }
}
