package com.muxistudio.common.jsbridge;

import android.net.Uri;
import android.text.TextUtils;

import com.muxistudio.common.util.Logger;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by ybao (ybaovv@gmail.com)
 * Date: 17/4/3
 */

public class BridgeWebClient extends WebViewClient {

    public static final String PROTOCOL_HYBRID = "hybrid";
    public static final String CALLBACK_RESOLVED = "Resolved";
    private BridgeWebView mBridgeWebView;

    public BridgeWebClient(BridgeWebView bridgeWebView) {
        mBridgeWebView = bridgeWebView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String s) {
        Uri uri = Uri.parse(s);
        Logger.d("jsbridge"+" "+s);
        if (uri.getScheme().equals(PROTOCOL_HYBRID)) {
            try {
                final String event = uri.getHost();
                final int uniqueId = uri.getPort();
                String data = uri.getPath().replaceFirst("/", "");
                if (event.indexOf(CALLBACK_RESOLVED) > -1) {
                    mBridgeWebView.responseHandlers.get(event + uniqueId).handle(data, null);
                } else {
                    mBridgeWebView.handlers.get(event).handle(data, data1 -> {
                        if (!TextUtils.isEmpty(data1)) {
                            mBridgeWebView.send(event + CALLBACK_RESOLVED, data1, null,
                                    uniqueId);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.shouldOverrideUrlLoading(webView, s);
    }
}
