package net.muxi.huashiapp.net;

import android.util.Log;

import net.muxi.huashiapp.App;
import net.muxi.huashiapp.common.data.InfoCookie;
import net.muxi.huashiapp.net.ccnu.CcnuCrawler;
import net.muxi.huashiapp.net.ccnu.CcnuCrawler2;
import net.muxi.huashiapp.util.Base64Util;
import net.muxi.huashiapp.util.PreferenceUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ybao (ybaovv@gmail.com)
 * Date: 17/4/24
 */

public class CookieInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request.Builder builder = originRequest.newBuilder();

        List<String> pathSegments = originRequest.url().pathSegments();

        if (pathSegments.get(1).equals("table") || pathSegments.get(1).equals("grade")) {
            if (CcnuCrawler.sSid != null && !CcnuCrawler.sSid.equals(App.sUser.sid)) {
                CcnuCrawler.clearCookieStore();
            }
            //执行了储存
            InfoCookie cookie = CcnuCrawler2.getInfoCookie();
            String big = PreferenceUtil.getString(PreferenceUtil.BIG_SERVER_POOL);
            String jid = PreferenceUtil.getString(PreferenceUtil.JSESSIONID);
            Log.d("fixing", "intercept: " + cookie.Bigipserverpool_Jwc_Xk + "\n" + jid);
            if (big.equals("") && jid.equals("")) {
                builder.addHeader("Bigipserverpool", cookie.Bigipserverpool_Jwc_Xk);
                builder.addHeader("Jsessionid", cookie.Jsessionid);
                builder.addHeader("Sid", App.sUser.sid);
                builder.addHeader("Authorization", Base64Util.createBaseStr(App.sUser));
                Log.d("crypt", "intercept: " + Base64Util.createBaseStr(App.sUser));
            } else {
                builder.addHeader("Bigipserverpool", big);
                builder.addHeader("Jsessionid", jid);
                builder.addHeader("Sid", App.sUser.sid);
                builder.addHeader("Authorization", Base64Util.createBaseStr(App.sUser));
                Log.d("crypt", "intercept: " + Base64Util.createBaseStr(App.sUser));
            }
        }

        return chain.proceed(builder.build());
    }
}