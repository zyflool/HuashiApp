package com.muxistudio.appcommon.presenter;

import android.content.Intent;

import com.muxistudio.appcommon.RxBus;
import com.muxistudio.appcommon.data.User;
import com.muxistudio.appcommon.event.LibLoginEvent;
import com.muxistudio.appcommon.event.LoginSuccessEvent;
import com.muxistudio.appcommon.net.ccnu.CcnuCrawler2;
import com.muxistudio.appcommon.user.UserAccountManager;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by kolibreath on 17-12-21.
 */

public class LoginPresenter {

    private final String TAG="LoginPresenter";
    /**
     * login 在完成一个登录过程 在回调中自定义逻辑 如果返回的内容是false的话 直接使用onError()
     *
     * @param user
     * @return
     */
    //在完成登陆之后无论是否成功需要清除了 cookieStore
    public Observable<Boolean> login(User user){
        return Observable.
                unsafeCreate((Observable.OnSubscribe<Boolean>) subscriber -> {
                    subscriber.onStart();
                    boolean crawlerResult = false;
                    try {
                        crawlerResult  = CcnuCrawler2.performLogin(user.sid,user.password);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(crawlerResult);
                    subscriber.onCompleted();
                })
                .subscribeOn(Schedulers.io());
    }


    public void saveLoginState(Intent intent,User user,String type){
        UserAccountManager.getInstance().saveInfoUser(user);
        MobclickAgent.onProfileSignIn(user.getSid());
        String target = intent.hasExtra("target") ?
                intent.getStringExtra("target") : null;
        if (type.equals("info")) {
            RxBus.getDefault().send(new LoginSuccessEvent(target));
        } else {
            RxBus.getDefault().send(new LibLoginEvent());
        }
    }
}




