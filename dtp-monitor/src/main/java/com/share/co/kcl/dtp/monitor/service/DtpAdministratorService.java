package com.share.co.kcl.dtp.monitor.service;

import com.share.co.kcl.dtp.common.exception.ToastException;
import com.share.co.kcl.dtp.monitor.dao.DtpAdministratorDao;
import com.share.co.kcl.dtp.monitor.model.po.DtpAdministrator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class DtpAdministratorService extends DtpBaseService<DtpAdministratorDao, DtpAdministrator> {

    public Long check(String account, String password) {
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            throw new ToastException("账号或者密码不能为空");
        }
        DtpAdministrator dtpAdministrator = this.getFirst(this.getQueryWrapper()
                .eq(DtpAdministrator::getAccount, account)).orElseThrow(() -> new ToastException("账号不存在"));
        if (!StringUtils.equals(dtpAdministrator.getPassword(), password)) {
            throw new ToastException("账号或者密码不正确");
        }
        return dtpAdministrator.getId();
    }
}
