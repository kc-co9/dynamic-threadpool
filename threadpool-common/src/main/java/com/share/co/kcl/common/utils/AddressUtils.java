package com.share.co.kcl.common.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class AddressUtils {
    private AddressUtils() {
    }

    public static List<String> getLocalIpList() {
        ArrayList<String> ipList = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;

            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address
                            && !inetAddress.isLoopbackAddress()
                            && !inetAddress.getHostAddress().contains(":")) {
                        ipList.add(inetAddress.getHostAddress());
                    }
                }
            }

            return ipList;
        } catch (SocketException ex) {
            return Collections.emptyList();
        }
    }
}
