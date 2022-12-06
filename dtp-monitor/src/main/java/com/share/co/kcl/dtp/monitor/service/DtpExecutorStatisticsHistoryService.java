package com.share.co.kcl.dtp.monitor.service;

import com.share.co.kcl.dtp.monitor.constants.SQLDateFormatConstants;
import com.share.co.kcl.dtp.monitor.dao.DtpExecutorStatisticsHistoryDao;
import com.share.co.kcl.dtp.monitor.model.enums.LineChartDuration;
import com.share.co.kcl.dtp.monitor.model.po.entity.DtpExecutorStatisticsHistory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class DtpExecutorStatisticsHistoryService extends DtpBaseService<DtpExecutorStatisticsHistoryDao, DtpExecutorStatisticsHistory> {

    public List<DtpExecutorStatisticsHistory> getLineChart(Long serverId, String serverIp, String executorId, LineChartDuration duration) {
        switch (duration) {
            case IN_15_MINUTES:
                return this.getLineChartIn15Minutes(serverId, serverIp, executorId);
            case IN_1_HOURS:
                return this.getLineChartIn1Hours(serverId, serverIp, executorId);
            case IN_1_DAYS:
                return this.getLineChartIn1Days(serverId, serverIp, executorId);
            case IN_7_DAYS:
                return this.getLineChartIn7Days(serverId, serverIp, executorId);
            case IN_14_DAYS:
                return this.getLineChartIn14Days(serverId, serverIp, executorId);
            case IN_30_DAYS:
                return this.getLineChartIn30Days(serverId, serverIp, executorId);
            default:
                return Collections.emptyList();
        }
    }

    public List<DtpExecutorStatisticsHistory> getLineChartIn15Minutes(Long serverId, String serverIp, String executorId) {
        return this.baseMapper.getLineChart(serverId, serverIp, executorId, LocalDateTime.now().minusMinutes(15), SQLDateFormatConstants.SECOND);
    }

    public List<DtpExecutorStatisticsHistory> getLineChartIn1Hours(Long serverId, String serverIp, String executorId) {
        return this.baseMapper.getLineChart(serverId, serverIp, executorId, LocalDateTime.now().minusHours(1), SQLDateFormatConstants.MINUTE);
    }

    public List<DtpExecutorStatisticsHistory> getLineChartIn1Days(Long serverId, String serverIp, String executorId) {
        return this.baseMapper.getLineChart(serverId, serverIp, executorId, LocalDateTime.now().minusDays(1), SQLDateFormatConstants.HOUR);
    }

    public List<DtpExecutorStatisticsHistory> getLineChartIn7Days(Long serverId, String serverIp, String executorId) {
        return this.baseMapper.getLineChart(serverId, serverIp, executorId, LocalDateTime.now().minusDays(7), SQLDateFormatConstants.DAY);
    }

    public List<DtpExecutorStatisticsHistory> getLineChartIn14Days(Long serverId, String serverIp, String executorId) {
        return this.baseMapper.getLineChart(serverId, serverIp, executorId, LocalDateTime.now().minusDays(14), SQLDateFormatConstants.DAY);
    }

    public List<DtpExecutorStatisticsHistory> getLineChartIn30Days(Long serverId, String serverIp, String executorId) {
        return this.baseMapper.getLineChart(serverId, serverIp, executorId, LocalDateTime.now().minusDays(30), SQLDateFormatConstants.DAY);
    }

}
