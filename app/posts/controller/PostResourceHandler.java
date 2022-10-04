package posts.controller;

import com.palominolabs.http.url.UrlBuilder;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import posts.model.Post;
import posts.model.PostResource;
import posts.repository.PostRepository;
import users.model.User;
import users.repository.UserRepository;

import javax.inject.Inject;
import java.nio.charset.CharacterCodingException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class PostResourceHandler {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public PostResourceHandler(PostRepository postRepository, UserRepository userRepository, HttpExecutionContext httpExecutionContext) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Stream<PostResource>> findAll(Http.Request request) {
        return postRepository.findAll().thenApplyAsync(postStream -> postStream.map(post -> new PostResource(post, link(request, post))), httpExecutionContext.current());
    }

    public CompletionStage<Stream<PostResource>> findAllByUser(Http.Request request, String userId) {
        return postRepository.findAllByUser(Long.parseLong(userId)).thenApplyAsync(postStream -> postStream.map(post -> new PostResource(post, link(request, post))), httpExecutionContext.current());
    }

    public CompletionStage<PostResource> save(Http.Request request, PostResource resource,String userId) {
        final Post post = new Post(resource.getTitle(), resource.getText(), userRepository.findUserById(Long.parseLong(userId)));
        return postRepository.save(post).thenApplyAsync(savedPost -> new PostResource(savedPost, link(request, savedPost)), httpExecutionContext.current());
    }

    public CompletionStage<Optional<PostResource>> findById(Http.Request request, String id) {
        return postRepository.findById(Long.parseLong(id)).thenApplyAsync(optionalPost -> optionalPost.map(post -> new PostResource(post, link(request, post))), httpExecutionContext.current());
    }

    public CompletionStage<Optional<PostResource>> update(Http.Request request,String id, PostResource resource) {
        final Post post = new Post(resource.getTitle(), resource.getText());
        return postRepository.update(Long.parseLong(id), post).thenApplyAsync(optionalPost -> optionalPost.map(op -> new PostResource(op, link(request, op))), httpExecutionContext.current());
    }

    private String link(Http.Request request, Post post) {
        final String[] hostPort = request.host().split(":");
        String host = hostPort[0];
        int port = (hostPort.length == 2) ? Integer.parseInt(hostPort[1]) : -1;
        final String scheme = request.secure() ? "https" : "http";
        try {
            return UrlBuilder.forHost(scheme, host, port)
                    .pathSegments("posts", post.id.toString())
                    .toUrlString();
        } catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
