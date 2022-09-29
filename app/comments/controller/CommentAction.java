package comments.controller;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import net.jodah.failsafe.FailsafeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.concurrent.Futures;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static play.mvc.Http.Status.*;

public class CommentAction extends Action.Simple {
    private final Logger logger = LoggerFactory.getLogger("application.UserAction");
    private final Meter requestsMeter;
    private final Timer responsesTimer;
    private final HttpExecutionContext httpExecutionContext;
    private final Futures futures;

    @Singleton
    @Inject
    public CommentAction(MetricRegistry metrics, HttpExecutionContext httpExecutionContext, Futures futures) {
        this.requestsMeter = metrics.meter("requestsMeter");
        this.responsesTimer = metrics.timer(name(CommentAction.class, "responsesTimer"));
        this.httpExecutionContext = httpExecutionContext;
        this.futures = futures;
    }

    @Override
    public CompletionStage<Result> call(Http.Request req) {
        if (logger.isTraceEnabled()) {
            logger.trace("call: request = " + req);
        }

        requestsMeter.mark();
        if (req.accepts("application/json")) {
            final Timer.Context time = responsesTimer.time();
            return futures.timeout(doCall(req), 1L, TimeUnit.SECONDS).exceptionally(e -> (Results.status(GATEWAY_TIMEOUT, views.html.timeout.render()))).whenComplete((r, e) -> time.close());
        } else {
            return completedFuture(
                    status(NOT_ACCEPTABLE, "We only accept application/json")
            );
        }
    }

    private CompletionStage<Result> doCall(Http.Request request) {
        return delegate.call(request).handleAsync((result, e) -> {
            if (e != null) {
                if (e instanceof CompletionException) {
                    Throwable completionException = e.getCause();
                    if (completionException instanceof FailsafeException) {
                        logger.error("Circuit breaker is open!", completionException);
                        return Results.status(SERVICE_UNAVAILABLE, "Service has timed out");
                    } else {
                        logger.error("Direct exception " + e.getMessage(), e);
                        return internalServerError();
                    }
                } else {
                    logger.error("Unknown exception " + e.getMessage(), e);
                    return internalServerError();
                }
            } else {
                return result;
            }
        }, httpExecutionContext.current());
    }
}
