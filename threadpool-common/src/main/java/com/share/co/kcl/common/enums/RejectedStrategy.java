package com.share.co.kcl.common.enums;

import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public enum RejectedStrategy {

    NONE,

    /**
     * A strategy for rejected tasks that runs the rejected task directly in the calling thread of the execute method,
     * unless the executor has been shut down, in which case the task is discarded.
     */
    ABORT_POLICY,

    /**
     * A strategy for rejected tasks that throws a RejectedExecutionException.
     */
    CALLER_RUNS_POLICY,

    /**
     * A strategy for rejected tasks that silently discards the rejected task.
     */
    DISCARD_POLICY,

    /**
     * A strategy for rejected tasks that discards the oldest unhandled request and then retries execute,
     * unless the executor is shut down, in which case the task is discarded.
     */
    DISCARD_OLDEST_POLICY;

    public static RejectedExecutionHandler parse(RejectedStrategy strategy) {
        if (Objects.isNull(strategy)) {
            throw new IllegalArgumentException("strategy is null");
        }
        switch (strategy) {
            case ABORT_POLICY:
                return new ThreadPoolExecutor.AbortPolicy();
            case CALLER_RUNS_POLICY:
                return new ThreadPoolExecutor.CallerRunsPolicy();
            case DISCARD_POLICY:
                return new ThreadPoolExecutor.DiscardPolicy();
            case DISCARD_OLDEST_POLICY:
                return new ThreadPoolExecutor.DiscardOldestPolicy();
            default:
                return null;
        }
    }

    public static RejectedStrategy parse(RejectedExecutionHandler rejectedHandler) {
        if (Objects.isNull(rejectedHandler)) {
            throw new IllegalArgumentException("rejectedHandler is null");
        }
        if (rejectedHandler instanceof ThreadPoolExecutor.AbortPolicy) {
            return ABORT_POLICY;
        } else if (rejectedHandler instanceof ThreadPoolExecutor.CallerRunsPolicy) {
            return CALLER_RUNS_POLICY;
        } else if (rejectedHandler instanceof ThreadPoolExecutor.DiscardPolicy) {
            return DISCARD_POLICY;
        } else if (rejectedHandler instanceof ThreadPoolExecutor.DiscardOldestPolicy) {
            return DISCARD_OLDEST_POLICY;
        } else {
            return NONE;
        }
    }
}
