package users.controller;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import users.model.UserResource;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@With(UserAction.class)
public class UserController extends Controller {
    private HttpExecutionContext httpExecutionContext;
    private UserResourceHandler userResourceHandler;

    @Inject
    public UserController(HttpExecutionContext httpExecutionContext, UserResourceHandler userResourceHandler) {
        this.httpExecutionContext = httpExecutionContext;
        this.userResourceHandler = userResourceHandler;
    }

    public CompletionStage<Result> findAll(Http.Request request) {
        return userResourceHandler.findAll(request).thenApplyAsync(users -> {
            final List<UserResource> userList = users.collect(Collectors.toList());
            return ok(Json.toJson(userList));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> findById(Http.Request request, String id) {
        return userResourceHandler.findById(request, id).thenApplyAsync(optionalResource -> {
            return optionalResource.map(resource ->
                    ok(Json.toJson(resource))
            ).orElseGet(Results::notFound);
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> update(Http.Request request, String id) {
        JsonNode json = request.body().asJson();
        UserResource resource = Json.fromJson(json, UserResource.class);
        return userResourceHandler.update(request, id, resource).thenApplyAsync(optionalResource -> {
            return optionalResource.map(r ->
                    ok(Json.toJson(r))
            ).orElseGet(Results::notFound
            );
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> save(Http.Request request) {
        JsonNode json = request.body().asJson();
        final UserResource resource = Json.fromJson(json, UserResource.class);
        return userResourceHandler.save(request, resource).thenApplyAsync(savedResource -> {
            return created(Json.toJson(savedResource));
        }, httpExecutionContext.current());
    }
}