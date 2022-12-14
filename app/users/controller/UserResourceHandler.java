package users.controller;

import com.palominolabs.http.url.UrlBuilder;
import users.model.User;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import users.model.UserResource;
import users.repository.UserRepository;

import javax.inject.Inject;
import java.nio.charset.CharacterCodingException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class UserResourceHandler {

    private final UserRepository userRepository;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public UserResourceHandler(UserRepository userRepository, HttpExecutionContext httpExecutionContext) {
        this.userRepository = userRepository;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Stream<UserResource>> findAll(Http.Request request) {
        return userRepository.findAll().thenApplyAsync(userStream -> userStream.map(user -> new UserResource(user, link(request, user))), httpExecutionContext.current());
    }

    public CompletionStage<UserResource> save(Http.Request request, UserResource resource) {
        final User user = new User(resource.getFirstName(), resource.getLastName());
        return userRepository.save(user).thenApplyAsync(savedUser -> new UserResource(savedUser, link(request, savedUser)), httpExecutionContext.current());
    }

    public CompletionStage<Optional<UserResource>> findById(Http.Request request, String id) {
        return userRepository.findById(Long.parseLong(id)).thenApplyAsync(optionalUser -> optionalUser.map(user -> new UserResource(user, link(request, user))), httpExecutionContext.current());
    }

    public CompletionStage<Optional<UserResource>> update(Http.Request request,String id, UserResource resource) {
        final User user = new User(resource.getFirstName(), resource.getLastName());
        return userRepository.update(Long.parseLong(id), user).thenApplyAsync(optionalUser -> optionalUser.map(ou -> new UserResource(ou, link(request, ou))), httpExecutionContext.current());
    }

    private String link(Http.Request request, User user) {
        final String[] hostPort = request.host().split(":");
        String host = hostPort[0];
        int port = (hostPort.length == 2) ? Integer.parseInt(hostPort[1]) : -1;
        final String scheme = request.secure() ? "https" : "http";
        try {
            return UrlBuilder.forHost(scheme, host, port)
                    .pathSegments("users", user.id.toString())
                    .toUrlString();
        } catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }
}