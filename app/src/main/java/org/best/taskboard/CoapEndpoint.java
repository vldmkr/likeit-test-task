package org.best.taskboard;

import android.util.Log;

import org.ws4d.coap.core.connection.BasicCoapServerChannel;
import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.rest.BasicCoapResource;
import org.ws4d.coap.core.rest.CoapResourceServer;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class CoapEndpoint {

    BasicCoapServerChannel mServerChanel;
    CoapResourceServer mResourceServer;
    Listener listener = null;
    public final Set<Integer> usedIds = new HashSet<>(); // Library's issue, it duplicates messages in multicast request case
    private final String mName;

    public interface Listener {
        void onReceiveBroadcast(String value);

        void onReceive(InetAddress address, String msg);
    }

    public CoapEndpoint(final String name, final Listener l) {
        mName = name;
//        mServerChanel = BasicCoapChannelManager.getInstance().createServerChannel()
        mResourceServer = new CoapResourceServer();
        listener = l;
        BasicCoapResource res = new BasicCoapResource("/broadcast", "ololo", CoapMediaType.text_plain) {
            @Override
            public synchronized boolean post(byte[] data, CoapMediaType type) {
                boolean value = super.post(data, type);
                String payload = null;
                if (data != null && data.length > 0) {
                    payload = new String(data, Charset.forName("UTF-8"));
                }
                Log.e("sadasd", mResourceServer.lastRemoteAddress.toString());
                synchronized (usedIds) {
                    if (listener != null && payload != null) {
                        if (usedIds.add(mResourceServer.lastMsgId)) {
                            listener.onReceiveBroadcast(payload);
                        }
                    }
                }
                return value;
            }
        };
        BasicCoapResource resPeer = new BasicCoapResource("/peer", "ololo", CoapMediaType.text_plain) {
            @Override
            public synchronized boolean post(byte[] data, CoapMediaType type) {
                boolean value = super.post(data, type);
                String payload = null;
                if (data != null && data.length > 0) {
                    payload = new String(data, Charset.forName("UTF-8"));
                }
                Log.e("sadasd", mResourceServer.lastRemoteAddress.toString());
                synchronized (usedIds) {
                    if (listener != null && payload != null) {
                        if (usedIds.add(mResourceServer.lastMsgId)) {
                            listener.onReceive(mResourceServer.lastRemoteAddress, payload);
                        }
                    }
                }
                return value;
            }
        };
        mResourceServer.createResource(res);
        mResourceServer.createResource(resPeer);
        mResourceServer.createResource(new BasicCoapResource("/name", mName, CoapMediaType.link_format));
        try {
            mResourceServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
