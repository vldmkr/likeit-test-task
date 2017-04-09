package org.best.taskboard;

import android.os.Handler;

import org.ws4d.coap.core.connection.BasicCoapChannelManager;
import org.ws4d.coap.core.connection.api.CoapClientChannel;
import org.ws4d.coap.core.enumerations.CoapRequestCode;
import org.ws4d.coap.core.messages.api.CoapRequest;
import org.ws4d.coap.core.messages.api.CoapResponse;

import java.net.InetAddress;
import java.nio.charset.Charset;

public class CoapPeerClient {
    private static final int PORT = 5683;
    private CoapClientChannel mClientChannel = null;
    private Handler mHandler = new Handler();
    private Listener mListener;

    public interface Listener {
        void onResponse(String msg);
    }

    public CoapPeerClient(final InetAddress address, final Listener listener) {
        mListener = listener;
        mClientChannel = BasicCoapChannelManager.getInstance().connect(new org.ws4d.coap.core.CoapClient() {
            @Override
            public void onResponse(CoapClientChannel channel, CoapResponse response) {
                if (mListener != null) {
                    mListener.onResponse(new String(response.toString()));
                }
            }

            @Override
            public void onMCResponse(CoapClientChannel channel, CoapResponse response, InetAddress srcAddress, int srcPort) {

            }

            @Override
            public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {

            }
        }, address, PORT);
    }

    public int send(String msg) {
        CoapRequest coapRequest = mClientChannel.createRequest(true, CoapRequestCode.POST);
        coapRequest.setMulticast(true);
        coapRequest.setPayload(msg.getBytes(Charset.forName("UTF-8")));
//        coapRequest.setUriPath("/.well-known/core");
        coapRequest.setUriPath("/peer");
        mClientChannel.sendMessage(coapRequest);
        return coapRequest.getMessageID();
    }
}
