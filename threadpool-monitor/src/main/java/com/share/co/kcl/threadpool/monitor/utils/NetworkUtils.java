package com.share.co.kcl.threadpool.monitor.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class NetworkUtils {

    private static final String LOCAL_IP = "127.0.0.1";

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取 UA
     */
    public static String getUa() {
        HttpServletRequest request = getRequest();
        return request.getHeader("user-agent");
    }

    /**
     * 获取客户端IP地址，此方法用在proxy环境中
     */
    public static String getRemoteAddr() {
        HttpServletRequest request = getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(ip)) {
            String[] ips = StringUtils.split(ip, ',');
            if (ips != null) {
                for (String tmpip : ips) {
                    if (StringUtils.isBlank(tmpip)) {
                        continue;
                    }
                    tmpip = tmpip.trim();
                    if (isIpAddr(tmpip) && !tmpip.startsWith("10.") && !tmpip.startsWith("192.168.")
                            && !"127.0.0.1".equals(tmpip)) {
                        return tmpip.trim();
                    }
                }
            }
        }
        ip = request.getHeader("x-real-ip");
        if (isIpAddr(ip)) {
            return ip;
        }
        ip = request.getRemoteAddr();
        if (ip.indexOf('.') == -1) {
            ip = LOCAL_IP;
        }
        return ip;
    }

    /**
     * 获取真实ip地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Real-IP");
        if (!checkIpAddressAvailable(ipAddress)) {
            ipAddress = request.getHeader("x-forwarded-for");
        }
        if (!checkIpAddressAvailable(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (!checkIpAddressAvailable(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!checkIpAddressAvailable(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            String localIpFormat = "0:0:0:0:0:0:0:1";
            if (ipAddress.equals(LOCAL_IP) || localIpFormat.equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                try {
                    ipAddress = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (!org.springframework.util.StringUtils.isEmpty(ipAddress)) {
            String[] ipAddressArray = ipAddress.split(",");
            if (ipAddressArray.length > 0) {
                ipAddress = ipAddressArray[0];
            }
        }
        return ipAddress;
    }

    public static Boolean checkIpAddressAvailable(String ipAddress) {
        return !(StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)
                || LOCAL_IP.equalsIgnoreCase(ipAddress));
    }

    /**
     * 判断字符串是否是一个IP地址
     */
    public static boolean isIpAddr(String addr) {
        if (StringUtils.isEmpty(addr)) {
            return false;
        }
        String[] ips = StringUtils.split(addr, '.');
        if (ips.length != 4) {
            return false;
        }
        try {
            int ipa = Integer.parseInt(ips[0]);
            int ipb = Integer.parseInt(ips[1]);
            int ipc = Integer.parseInt(ips[2]);
            int ipd = Integer.parseInt(ips[3]);
            return ipa >= 0 && ipa <= 255 && ipb >= 0 && ipb <= 255 && ipc >= 0 && ipc <= 255 && ipd >= 0 && ipd <= 255;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 获取HTTP端口
     */
    public static int getHttpPort() {
        try {
            HttpServletRequest req = getRequest();
            return new URL(req.getRequestURL().toString()).getPort();
        } catch (MalformedURLException excp) {
            return 80;
        }
    }

    public static Map<String, String> getHeader(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>(16);
        Enumeration<?> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    public static Map<String, Object> getBody(HttpServletRequest request) {
        Enumeration<String> enumeration = request.getParameterNames();

        Map<String, Object> parameterMap = new HashMap<>(16);
        while (enumeration.hasMoreElements()) {
            String parameter = enumeration.nextElement();
            String value = request.getParameter(parameter);
            parameterMap.put(parameter, value);
        }

        return parameterMap;
    }

    public static Map<String, Object> getMultiBody(HttpServletRequest request) {
        Enumeration<String> enumeration = request.getParameterNames();

        Map<String, Object> parameterMap = new HashMap<>(16);
        while (enumeration.hasMoreElements()) {
            String parameter = enumeration.nextElement();
            String[] value = request.getParameterValues(parameter);
            parameterMap.put(parameter, value);
        }

        return parameterMap;
    }
}
