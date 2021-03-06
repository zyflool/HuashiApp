package net.muxi.huashiapp.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.muxistudio.common.util.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;


/**
 * @author messi-wpy
 * 参考 android-async-http这个开源库 的Cookie管理机制方案，PersistentCookieStore 进行改造 ccnu定制
 * 主要进行okhttp cookie的本地持久化存储
 */
public class CookieManager {

    private final SharedPreferences cookiePrefs;
    private static final String HOST_PRE = "HOST_";
    private ConcurrentHashMap<String, Map<String,Cookie>> cookies;
    private Context context;
    //private HashMap<String, List<Cookie>> newCookies;
    private static final String COOKIE_FILE = "CookiePreFile";
    private Encryption encryption;
    /**
     * @param context 必须是application的context,用app类里的，千万别用当前activity的Context否则造成内存泄漏
     *                关于在构造函数里进行很多操作是否ok的讨论:  https://stackoverflow.com/questions/7048515/is-doing-a-lot-in-constructors-bad
     */
    public CookieManager(Context context) {
       this.context=context;
        this.cookiePrefs = context.getSharedPreferences(COOKIE_FILE, 0);
        encryption=new Encryption();
    }


    /**
     * 从sharePreference中获取数据
     * cookie的存储格式,整体是一个map,根据其host为cookie分组
     * 而每个cookie存在sharePreference里的key是  host___index  这样一个字符串  (index是这个cookie在其分组的list的序号)
     * HashMap<String , List<Cookie>>
     */

    public void getDataFromPre() {
        if (cookies != null) {
            return;
        }
        cookies = new ConcurrentHashMap<>();
        Map tempCookieMap = new HashMap<String, Object>(cookiePrefs.getAll());

        for (Object key : tempCookieMap.keySet()) {
            String host = ((String) key).split("___")[0];
            if (!cookies.containsKey(host)) {
                cookies.put(host, new HashMap<>());
            }
            Cookie cookie= null;
            try {
                cookie = decodeCookie(encryption.decryptAES((String) tempCookieMap.get(key)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            cookies.get(host).put(cookie.name(),cookie);

        }

    }

    /**
     * 请求成功服务器不会返回cookie，所以也不需要添加，更新
     * 如果请求了,说明登录失败，之前的cookie过期了需要把这个替换上去
     *
     * @param host
     * @param cookie
     */
    public void addCookie(String host, Cookie cookie) {
        if (cookies==null)
            throw new NullPointerException("cookies is null,please use getDataFromPre() firstly to init cookies ");
        if (cookies.get(host)==null){
            cookies.put(host,new HashMap<>());
        }
        Map<String,Cookie> hashMap=cookies.get(host);
        hashMap.put(cookie.name(),cookie);

    }


    public void addAll(String host, List<Cookie> list) {

        for (int i = 0; i <list.size() ; i++) {
            addCookie(host,list.get(i));
        }

    }

    public void clearAll() {
        cookies = null;
        SharedPreferences pref=context.getSharedPreferences(COOKIE_FILE, 0);
        pref.edit().clear().commit();


    }

    /**
     * 这个方法其实是比较新cookie和旧cookie的差别,
     * 如果不同说明更新了，要更换
     * <p>
     * 最终存储的实现写在{@link #performSave(Map)} 方法里
     */
    public void saveToPre() {
      performSave(cookies);
    }

    private void performSave(Map<String, Map<String,Cookie>> res) {
        SharedPreferences.Editor writer = cookiePrefs.edit();

        for (String key : res.keySet()) {
            List<Cookie>list=new ArrayList<>(res.get(key).values());
            for (int i = 0; i < list.size(); i++) {
                String preKey = key + "___" + i;
                try {
                    writer.putString(preKey, encryption.encryptAES(encodeCookie(new SerializableCookie(list.get(i)))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        writer.apply();

    }

    @NotNull
    public List<Cookie> provideCookies(String host) {
        getDataFromPre();
        if (cookies != null && cookies.get(host) != null) {
            return new ArrayList<>(cookies.get(host).values());
        } else
            return Collections.emptyList();

    }


    @Nullable
    protected String encodeCookie(SerializableCookie cookie) {
        if (cookie == null) return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] bytes = null;
        try {
            out = new ObjectOutputStream(os);
            out.writeObject(cookie);
            bytes = os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                out.close();
                os.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayToHexString(bytes);
    }


    @Nullable
    protected Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        ObjectInputStream objectIn = null;
        try {
            objectIn = new ObjectInputStream(in);
            cookie = ((SerializableCookie) objectIn.readObject()).getCookie();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                objectIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cookie;

    }


    /**
     * 这是一个将字符数组转换为16进制表示的string的算法
     * 一个字节可以表示两个16进制的数-128~127--->255（128-255会溢出变成负数）
     * elem & 0xff这是为了让有符号的byte转换为int,
     * 因为当byte是负数时他的int值会变11111...（反码）
     * <p>
     * 最后把变成大写，是为了方便之后的base64编码,
     * (16进制 a b c d e f 大小写对其值也没有影响嘛)
     *
     * @return
     */
    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte elem : bytes) {
            int value = elem & 0xff;
            if (value < 16) {
                sb.append(0);
            }
            sb.append(Integer.toHexString(value));


        }
        return sb.toString().toUpperCase(Locale.US);
    }

    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }


    public boolean isSameCookie(@NotNull Cookie first,@NotNull Cookie second){

        boolean flag= first.name().equals(second.name())
                &&first.domain().equals(second.domain())
                &&first.hostOnly()==second.hostOnly()
                &&first.httpOnly()==second.httpOnly()
                &&first.path().equals(second.path());
        Logger.i("sad"+" isSameCookie: "+flag);
        return flag;

    }
}
