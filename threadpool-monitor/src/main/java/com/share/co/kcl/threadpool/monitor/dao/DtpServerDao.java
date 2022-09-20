package com.share.co.kcl.threadpool.monitor.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.share.co.kcl.threadpool.monitor.model.po.DtpServer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DtpServerDao extends BaseMapper<DtpServer> {
}
