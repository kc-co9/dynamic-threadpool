package com.share.co.kcl.dtp.common.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum SyncStatus {

    /**
     * unknown
     */
    NONE,

    /**
     * waiting to be synced
     */
    WAITING,

    /**
     * has been finished to sync
     */
    FINISH,
    ;

    public static Optional<SyncStatus> parse(String status) {
        if (StringUtils.isBlank(status)) {
            return Optional.empty();
        }

        return Arrays.stream(SyncStatus.values())
                .filter(o -> StringUtils.equals(o.name(), status))
                .findFirst();
    }
}
