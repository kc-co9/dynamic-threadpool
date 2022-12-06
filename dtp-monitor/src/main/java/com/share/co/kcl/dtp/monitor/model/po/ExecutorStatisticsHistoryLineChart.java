package com.share.co.kcl.dtp.monitor.model.po;

import com.share.co.kcl.dtp.monitor.model.po.entity.DtpExecutorStatisticsHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Deprecated
public class ExecutorStatisticsHistoryLineChart extends DtpExecutorStatisticsHistory {

    private LocalDateTime time;
}
