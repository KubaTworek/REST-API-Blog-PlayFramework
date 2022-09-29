package posts.repository;

import database.DatabaseExecutionContext;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import play.db.jpa.JPAApi;
import posts.model.Post;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPAPostRepository implements PostRepository{

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;
    private final CircuitBreaker<Optional<Post>> circuitBreaker = new CircuitBreaker<Optional<Post>>().withFailureThreshold(1).withSuccessThreshold(3);

    @Inject
    public JPAPostRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Stream<Post>> findAll() {
        return supplyAsync(() -> wrap(this::select), executionContext);
    }

    @Override
    public CompletionStage<Post> save(Post post) {
        return supplyAsync(() -> wrap(em -> insert(em,post)), executionContext);
    }

    @Override
    public CompletionStage<Optional<Post>> findById(Long id) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> lookup(em, id))), executionContext);
    }

    @Override
    public CompletionStage<Optional<Post>> update(Long id, Post post) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> modify(em, id, post))), executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Optional<Post> lookup(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Post.class, id));
    }

    private Stream<Post> select(EntityManager em) {
        TypedQuery<Post> query = em.createQuery("SELECT p FROM Post p", Post.class);
        return query.getResultList().stream();
    }

    private Optional<Post> modify(EntityManager em, Long id, Post post) throws InterruptedException {
        final Post tempPost = em.find(Post.class, id);
        if (tempPost != null) {
            tempPost.title = post.title;
            tempPost.text = post.text;
        }
        Thread.sleep(10000L);
        return Optional.ofNullable(tempPost);
    }

    private Post insert(EntityManager em, Post post) {
        return em.merge(post);
    }
}
