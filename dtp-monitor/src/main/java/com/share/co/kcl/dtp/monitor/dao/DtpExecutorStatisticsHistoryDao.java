package com.share.co.kcl.dtp.monitor.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.share.co.kcl.dtp.monitor.model.po.entity.DtpExecutorStatisticsHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DtpExecutorStatisticsHistoryDao extends BaseMapper<DtpExecutorStatisticsHistory> {

    List<DtpExecutorStatisticsHistory> getLineChart(@Param("serverId") Long serverId,
                                                    @Param("serverIp") String serverIp,
                                                    @Param("executorId") String executorId,
                                                    @Param("createTimeStart") LocalDateTime createTimeStart,
                                                    @Param("dateFormat") String dateFormat);
}
