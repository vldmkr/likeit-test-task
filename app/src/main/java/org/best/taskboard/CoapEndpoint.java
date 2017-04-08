package org.best.taskboard;

import android.util.Log;

import org.ws4d.coap.core.connection.BasicCoapServerChannel;
import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.rest.BasicCoapResource;
import org.ws4d.coap.core.rest.CoapResourceServer;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class CoapEndpoint {

    BasicCoapServerChannel mServerChanel;
    CoapResourceServer mResourceServer;
    Listener listener = null;
    public Set<Integer> usedIds = new HashSet<>(); // Library's issue, it duplicates messages in multicast request case

    public interface Listener {
        void onReceive(String value);
    }

    public CoapEndpoint(final Listener l) {
//        mServerChanel = BasicCoapChannelManager.getInstance().createServerChannel()
        mResourceServer = new CoapResourceServer();
        listener = l;
        BasicCoapResource res = new BasicCoapResource("/test", "ololo", CoapMediaType.text_plain) {

            @Override
            public synchronized boolean post(byte[] data, CoapMediaType type) {
                boolean value = super.post(data, type);
                String payload = null;
                if (data != null && data.length > 0) {
                    payload = new String(data, Charset.forName("UTF-8"));
                }
                int msgId = mResourceServer.lastMsgId;

                if (listener != null && payload != null && !usedIds.contains(msgId)) {
                    usedIds.add(msgId);
                    listener.onReceive(payload);
                }
                return value;
            }
        };
        mResourceServer.createResource(res.setResourceType("Test"));
        try {
            mResourceServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
