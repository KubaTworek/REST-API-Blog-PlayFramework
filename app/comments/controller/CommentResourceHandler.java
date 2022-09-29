package comments.controller;

import com.palominolabs.http.url.UrlBuilder;
import comments.model.Comment;
import comments.model.CommentResource;
import comments.repository.CommentRepository;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;

import javax.inject.Inject;
import java.nio.charset.CharacterCodingException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class CommentResourceHandler {
    private final CommentRepository commentRepository;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public CommentResourceHandler(CommentRepository commentRepository, HttpExecutionContext httpExecutionContext) {
        this.commentRepository = commentRepository;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Stream<CommentResource>> findAll(Http.Request request) {
        return commentRepository.findAll().thenApplyAsync(commentStream -> commentStream.map(comment -> new CommentResource(comment, link(request, comment))), httpExecutionContext.current());
    }

    public CompletionStage<CommentResource> save(Http.Request request, CommentResource resource) {
        final Comment comment = new Comment(resource.getContent());
        return commentRepository.save(comment).thenApplyAsync(savedComment -> new CommentResource(savedComment, link(request, savedComment)), httpExecutionContext.current());
    }

    public CompletionStage<Optional<CommentResource>> findById(Http.Request request, String id) {
        return commentRepository.findById(Long.parseLong(id)).thenApplyAsync(optionalComment -> optionalComment.map(comment -> new CommentResource(comment, link(request, comment))), httpExecutionContext.current());
    }

    public CompletionStage<Optional<CommentResource>> update(Http.Request request,String id, CommentResource resource) {
        final Comment comment = new Comment(resource.getContent());
        return commentRepository.update(Long.parseLong(id), comment).thenApplyAsync(optionalComment -> optionalComment.map(oc -> new CommentResource(oc, link(request, oc))), httpExecutionContext.current());
    }

    private String link(Http.Request request, Comment comment) {
        final String[] hostPort = request.host().split(":");
        String host = hostPort[0];
        int port = (hostPort.length == 2) ? Integer.parseInt(hostPort[1]) : -1;
        final String scheme = request.secure() ? "https" : "http";
        try {
            return UrlBuilder.forHost(scheme, host, port)
                    .pathSegments("comments", comment.id.toString())
                    .toUrlString();
        } catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
