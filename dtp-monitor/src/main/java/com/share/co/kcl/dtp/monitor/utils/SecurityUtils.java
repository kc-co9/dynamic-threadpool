package com.share.co.kcl.dtp.monitor.utils;

import com.alibaba.fastjson2.JSON;
import com.share.co.kcl.dtp.monitor.model.domain.SecurityAuthDo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Objects;
import java.util.Random;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class SecurityUtils {

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private static final String SECRET = "3d990d2276917dfac04467df11fff26d";
    private static final Key SECRET_KEY = new SecretKeySpec(SECRET.getBytes(), SIGNATURE_ALGORITHM.getJcaName());

    private static final Random RAN = new Random();

    private static final String KEY_BODY = "body";
    private static final String KEY_RAN = "ran";

    private SecurityUtils() {
    }

    /**
     * 生成token
     *
     * @return 返回值
     */
    public static String echoToken(SecurityAuthDo jwtObject) {
        if (Objects.isNull(jwtObject)) {
            return "";
        }
        return Jwts.builder()
                .setSubject(null)
                .claim(KEY_BODY, JSON.toJSONString(jwtObject))
                .claim(KEY_RAN, RAN.nextInt())
                .signWith(SIGNATURE_ALGORITHM, SECRET_KEY)
                .compact();
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
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            return JSON.parseObject((String) claims.get(KEY_BODY), SecurityAuthDo.class);
        } catch (Exception ignoreEx) {
        }
        return null;
    }

    /**
     * 生成salt盐
     *
     * @return 返回值
     */
    public static String echoSalt() {
        return null;
    }
}
