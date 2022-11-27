package com.share.co.kcl.dtp.monitor.model.domain;

import com.share.co.kcl.dtp.monitor.utils.SecurityUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SecurityAuthDo {

    private String userId;

    public SecurityAuthDo() {
    }

    public SecurityAuthDo(String userId) {
        this.userId = userId;
    }

    /**
     * 生成token
     *
     * @return 返回值
     */
    public String echoToken() {
        if (StringUtils.isBlank(this.getUserId())) {
            return "";
        }
        return SecurityUtils.echoToken(this);
    }

    public static class Builder {

        private Builder() {
        }

        /**
         * 解析token
         *
         * @param token 传入token
         * @return 返回解析数据
         */
        public static SecurityAuthDo parseToken(String token) {
            if (StringUtils.isBlank(token)) {
                return null;
            }
            return SecurityUtils.parseToken(token);
        }
    }

}
