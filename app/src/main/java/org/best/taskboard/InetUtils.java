package org.best.taskboard;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class InetUtils {

    public static NetworkInterface getNetworkInterface() {
        try {
            Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
            while (networkInterface.hasMoreElements()) {
                NetworkInterface singleInterface = networkInterface.nextElement();
                if (singleInterface.getDisplayName().contains("wlan0")
                        || singleInterface.getDisplayName().contains("eth0")
                        || singleInterface.getDisplayName().contains("ap0")) {
                    return singleInterface;
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static InetAddress getIpAddress(final NetworkInterface networkInterface) {
        if (networkInterface != null) {
            Enumeration<InetAddress> ipAddresses = networkInterface.getInetAddresses();
            while (ipAddresses.hasMoreElements()) {
                InetAddress inetAddress = ipAddresses.nextElement();
                if (!inetAddress.isLoopbackAddress() && !inetAddress.toString().contains(":")) {
                    return inetAddress;
                }
            }
        } else {
            new NullPointerException("NetworkInterface is null").printStackTrace();
        }
        return null;
    }

    public static InetAddress getBroadcastAddress(final NetworkInterface networkInterface) {
        if (networkInterface != null) {
            List<InterfaceAddress> ifAddresses = networkInterface.getInterfaceAddresses();
            for (InterfaceAddress val : ifAddresses) {
                InetAddress broadcastAddress = val.getBroadcast();
                if (broadcastAddress != null) {
                    return broadcastAddress;
                }
            }
        } else {
            new NullPointerException("NetworkInterface is null").printStackTrace();
        }
        return null;
    }
}