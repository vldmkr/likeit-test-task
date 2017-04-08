package org.best.taskboard;

import android.util.Log;

import org.best.taskboard.InetUtils;
import org.ws4d.coap.core.connection.BasicCoapChannelManager;
import org.ws4d.coap.core.connection.api.CoapClientChannel;
import org.ws4d.coap.core.enumerations.CoapRequestCode;
import org.ws4d.coap.core.messages.api.CoapRequest;
import org.ws4d.coap.core.messages.api.CoapResponse;

import java.net.InetAddress;
import java.nio.charset.Charset;

public class CoapClient {
    private static final int PORT = 5683;
    private CoapClientChannel mClientChannel = null;

    public CoapClient() {
        mClientChannel = BasicCoapChannelManager.getInstance().connect(new org.ws4d.coap.core.CoapClient() {
            @Override
            public void onResponse(CoapClientChannel channel, CoapResponse response) {

            }

            @Override
            public void onMCResponse(CoapClientChannel channel, CoapResponse response, InetAddress srcAddress, int srcPort) {
//                Log.e("asdasdad", new String(response.getPayload()));
            }

            @Override
            public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {

            }
        }, InetUtils.getBroadcastAddress(InetUtils.getNetworkInterface()), PORT);
    }

    public int send(String msg) {
        CoapRequest coapRequest = mClientChannel.createRequest(true, CoapRequestCode.POST);
        coapRequest.setMulticast(true);
        coapRequest.setPayload(msg.getBytes(Charset.forName("UTF-8")));
//        coapRequest.setUriPath("/.well-known/core");
        coapRequest.setUriPath("/test");
        mClientChannel.sendMessage(coapRequest);
        return coapRequest.getMessageID();
    }
}
