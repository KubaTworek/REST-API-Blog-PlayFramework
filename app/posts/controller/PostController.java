package posts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;
import posts.model.PostResource;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.mvc.Results.created;
import static play.mvc.Results.ok;

@With(PostAction.class)
public class PostController {

    private final HttpExecutionContext httpExecutionContext;
    private final PostResourceHandler postResourceHandler;

    @Inject
    public PostController(HttpExecutionContext httpExecutionContext, PostResourceHandler postResourceHandler) {
        this.httpExecutionContext = httpExecutionContext;
        this.postResourceHandler = postResourceHandler;
    }

    public CompletionStage<Result> findAll(Http.Request request) {
        return postResourceHandler.findAll(request).thenApplyAsync(posts -> {
            final List<PostResource> postList = posts.collect(Collectors.toList());
            return ok(Json.toJson(postList));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> findById(Http.Request request, String id) {
        return postResourceHandler.findById(request, id).thenApplyAsync(optionalResource -> optionalResource.map(resource ->
                ok(Json.toJson(resource))
        ).orElseGet(Results::notFound), httpExecutionContext.current());
    }

    public CompletionStage<Result> update(Http.Request request, String id) {
        JsonNode json = request.body().asJson();
        PostResource resource = Json.fromJson(json, PostResource.class);
        return postResourceHandler.update(request, id, resource).thenApplyAsync(optionalResource -> optionalResource.map(r ->
                ok(Json.toJson(r))
        ).orElseGet(Results::notFound
        ), httpExecutionContext.current());
    }

    public CompletionStage<Result> save(Http.Request request) {
        JsonNode json = request.body().asJson();
        final PostResource resource = Json.fromJson(json, PostResource.class);
        return postResourceHandler.save(request, resource).thenApplyAsync(savedResource -> created(Json.toJson(savedResource)), httpExecutionContext.current());
    }
}
