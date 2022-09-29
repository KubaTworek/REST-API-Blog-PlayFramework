package comments.controller;

import com.fasterxml.jackson.databind.JsonNode;
import comments.model.CommentResource;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.mvc.Results.created;
import static play.mvc.Results.ok;

@With(CommentAction.class)
public class CommentController {
    private final HttpExecutionContext httpExecutionContext;
    private final CommentResourceHandler commentResourceHandler;

    @Inject
    public CommentController(HttpExecutionContext httpExecutionContext, CommentResourceHandler commentResourceHandler) {
        this.httpExecutionContext = httpExecutionContext;
        this.commentResourceHandler = commentResourceHandler;
    }

    public CompletionStage<Result> findAll(Http.Request request) {
        return commentResourceHandler.findAll(request).thenApplyAsync(comments -> {
            final List<CommentResource> commentList = comments.collect(Collectors.toList());
            return ok(Json.toJson(commentList));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> findById(Http.Request request, String id) {
        return commentResourceHandler.findById(request, id).thenApplyAsync(optionalResource -> optionalResource.map(resource ->
                ok(Json.toJson(resource))
        ).orElseGet(Results::notFound), httpExecutionContext.current());
    }

    public CompletionStage<Result> update(Http.Request request, String id) {
        JsonNode json = request.body().asJson();
        CommentResource resource = Json.fromJson(json, CommentResource.class);
        return commentResourceHandler.update(request, id, resource).thenApplyAsync(optionalResource -> optionalResource.map(r ->
                ok(Json.toJson(r))
        ).orElseGet(Results::notFound
        ), httpExecutionContext.current());
    }

    public CompletionStage<Result> save(Http.Request request) {
        JsonNode json = request.body().asJson();
        final CommentResource resource = Json.fromJson(json, CommentResource.class);
        return commentResourceHandler.save(request, resource).thenApplyAsync(savedResource -> created(Json.toJson(savedResource)), httpExecutionContext.current());
    }
}
