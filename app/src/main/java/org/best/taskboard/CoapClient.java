package org.best.taskboard;

import android.os.Handler;
import android.util.Log;

import org.ws4d.coap.core.connection.BasicCoapChannelManager;
import org.ws4d.coap.core.connection.api.CoapClientChannel;
import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.enumerations.CoapRequestCode;
import org.ws4d.coap.core.messages.api.CoapRequest;
import org.ws4d.coap.core.messages.api.CoapResponse;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class CoapClient {
    private static final int PORT = 5683;
    private CoapClientChannel mClientChannel = null;
    private Handler mHandler = new Handler();
    private Map<InetAddress, String> mClients = new HashMap<>();
    private Listener mListener;

    public interface Listener {
        void onResponse(Map<InetAddress, String> clients);
    }

    public CoapClient(final Listener listener) {
        mListener = listener;
        mClientChannel = BasicCoapChannelManager.getInstance().connect(new org.ws4d.coap.core.CoapClient() {
            @Override
            public void onResponse(CoapClientChannel channel, CoapResponse response) {

            }

            @Override
            public void onMCResponse(CoapClientChannel channel, CoapResponse response, InetAddress srcAddress, int srcPort) {
                if (response.getContentType() == CoapMediaType.link_format &&
                        !srcAddress.equals(InetUtils.getIpAddress(InetUtils.getNetworkInterface()))) {
                    Log.e("asdasdad", new String(response.getPayload()) + srcAddress.toString());
                    mClients.put(srcAddress, new String(response.getPayload()));
                    if (mListener != null) {
                        mListener.onResponse(mClients);
                    }
                }
            }

            @Override
            public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
            }
        }, InetUtils.getBroadcastAddress(InetUtils.getNetworkInterface()), PORT);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendDiscovery();
                mHandler.postDelayed(this, 3000);
            }
        }, 1000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mClients.clear();
                mHandler.postDelayed(this, 10_000);
            }
        }, 10_000);
    }

    public int send(String msg) {
        CoapRequest coapRequest = mClientChannel.createRequest(true, CoapRequestCode.POST);
        coapRequest.setMulticast(true);
        coapRequest.setPayload(msg.getBytes(Charset.forName("UTF-8")));
//        coapRequest.setUriPath("/.well-known/core");
        coapRequest.setUriPath("/broadcast");
        mClientChannel.sendMessage(coapRequest);

        return coapRequest.getMessageID();
    }

    private void sendDiscovery() {
        CoapRequest coapRequest = mClientChannel.createRequest(true, CoapRequestCode.GET);
        coapRequest.setMulticast(true);
        coapRequest.setUriPath("/name");
        mClientChannel.sendMessage(coapRequest);
    }

    public String getClientName(InetAddress address) {
        return mClients.get(address);
    }
}
