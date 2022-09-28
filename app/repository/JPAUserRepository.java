package repository;

import entities.User;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Singleton
public class JPAUserRepository implements UserRepository{

    private final JPAApi jpapi;
    private final DatabaseExecutionContext executionContext;
    private final CircuitBreaker<Optional<User>> circuitBreaker = new CircuitBreaker<Optional<User>>().withFailureThreshold(1).withSuccessThreshold(3);

    @Inject
    public JPAUserRepository(JPAApi jpapi, DatabaseExecutionContext executionContext) {
        this.jpapi = jpapi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Stream<User>> findAll() {
        return supplyAsync(() -> wrap(em -> select(em)), executionContext);
    }

    @Override
    public CompletionStage<User> save(User user) {
        return supplyAsync(() -> wrap(em -> insert(em,user)), executionContext);
    }

    @Override
    public CompletionStage<Optional<User>> findById(Long id) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> lookup(em, id))), executionContext);
    }

    @Override
    public CompletionStage<Optional<User>> update(Long id, User user) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> modify(em, id, user))), executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpapi.withTransaction(function);
    }

    private Optional<User> lookup(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    private Stream<User> select(EntityManager em) {
        TypedQuery<User> query = em.createQuery("SELECT p FROM User p", User.class);
        return query.getResultList().stream();
    }

    private Optional<User> modify(EntityManager em, Long id, User user) throws InterruptedException {
        final User tempUser = em.find(User.class, id);
        if (tempUser != null) {
            tempUser.firstName = user.firstName;
            tempUser.lastName = user.lastName;
        }
        Thread.sleep(10000L);
        return Optional.ofNullable(tempUser);
    }

    private User insert(EntityManager em, User user) {
        return em.merge(user);
    }
}
