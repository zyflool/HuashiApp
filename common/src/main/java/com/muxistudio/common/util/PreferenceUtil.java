package com.muxistudio.common.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.muxistudio.common.base.Global;

/**
 * Created by ybao on 16/4/19.
 * SharedPreferences存储类
 */
public class PreferenceUtil {

    public static final String LAST_LOGIN_MOMENT = "sLastLoginMoment";

    public static final String HINT_UPDATE_TIME = "sHintUpdateTime";

    //phpsessionid 有两种情况 下面中的一种
    public static final String CURWEEK = "sCurWeek";
    public static final String PHPSESSID = "sPhpSessId";
    public static final String PHPSESSION_ID = "buisPhpSessionId";
    public static final String BIG_SERVER_POOL = "sBigipServerPool";
    public static final String JSESSIONID = "sJid";
    public static final String STUDENT_ID = "sId";
    public static final String STUDENT_PWD = "sPwd";
    public static final String LIBRARY_ID = "libraryId";
    public static final String LIBRARY_PWD = "libraryPwd";
    public static final String COURSE_ID = "course_id";


    //课程表
    //课表中的第一周的第一天日期
    public static final String FIRST_WEEK_DATE = "first_date_v2";
    public static final String SELECTED_WEEK = "mCurweek_v2";
    public static final String SELECTED_WEEK_DATE="select_date_v2";




    //是否是第一次进入课程表
    public static final String IS_FIRST_ENTER_TABLE = "is_first_enter_table";

    //成绩
    //当前出了成绩的科目数
    public static final String SCORES_NUM = "score_num";

    //校历
    //校历的最近更新时间
    public static final String CALENDAR_UPDATE = "calendar_update";
    //校历的图片地址
    public static final String CALENDAR_ADDRESS = "calendar_address";
    public static final String OLD_CALENDAR_ADDRESS="old_calendar_url";
    //校历的尺寸
    public static final String CALENDAR_SIZE = "calendar_size";

    //电费的查询参数
    public static final String ELE_QUERY_STRING = "ele_query";

    //空闲教室的查询参数
    public static final String STUDY_ROOM_QUERY_STRING = "study_room_query";
    //更新前的 App 版本
    public static final String LAST_APP_VERSION = "last_app_version";

    //首页产品更新时间
    public static final String PRODUCT_UPDATE = "product_update";

    //首页产品 json 数据
    public static final String PRODUCT_DATA = "product_data";
    //是否是初次选择周数
    public static final String FIRST_SELECT_WEEK = "first_select_week";
    //保存的上一次不再提醒的版本号
    public static final String LAST_NOT_REMIND_VERSION = "not_remind_version";
    //提醒更新的状态,在有新版本的时候自动开启
    public static final String REMIND_UPDATE = "remind_update";
    //最新的通知公告日期
    public static final String LATEST_NEWS_DATE = "latest_news_date";

    //图书馆
    //关注图书的列表
    public static final String ATTENTION_BOOK_IDS = "attention_book_ids";
    //借阅图书的列表
    public static final String BORROW_BOOK_IDS = "borrow_book_ids";



    //判断是否是第一次启动应用
    public static final String APP_FIRST_OPEN = "first_open";
    //是否第一次进入主界面
    public static final String IS_FIRST_ENTER_MAIN = "is_first_enter_main";



    public static void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        return PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean def) {
        return PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).getBoolean(key, def);
    }

    public static void saveString(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static String getString(String key) {
        return PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).getString(key, "");
    }


    public static String getString(String key, String def) {
        return PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).getString(key, def);
    }


    //可用于用户上次使用后注销账号时移除账号
    public static void clearString(String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).edit();
        editor.remove(key);
        editor.apply();
    }


    public static void saveInt(String key, int value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void saveLong(String key, long value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void saveFloat(String key, float value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Global.getApplication());
        return sp.getFloat(key, -1);
    }

    public static void clearFloat(String key){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Global.getApplication()).edit();
        editor.remove(key);
        editor.apply();

    }

    public static long getLong(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Global.getApplication());
        return sp.getLong(key, -1);
    }

    public static int getInt(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Global.getApplication());
        return sp.getInt(key, -1);
    }

    public static int getInt(String key, int def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Global.getApplication());
        return sp.getInt(key, def);
    }

    /**
     * 清楚所有的数据,在注销时使用
     */
    public void clearAllData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Global.getApplication());
    }



    //这个方法竟然都没有用,怪不得以前每次打开app都重新登录,有点坑啊
    /**
     * 记录用户登录的时间差 如果超过2个小时启动自动登录 可以直接通过 App中常量引用
     */
    public static void saveLastLoginMoment(){
        PreferenceUtil.saveLong(PreferenceUtil.LAST_LOGIN_MOMENT,(System.currentTimeMillis()));
    }

    /**
     * cookie是否仍然有效?
     * @return false invalid ; true valid
     */
    public static boolean isCookieValid(){
      return System.currentTimeMillis() <= PreferenceUtil.getLong(PreferenceUtil.LAST_LOGIN_MOMENT);
    }

    public static long getLastLoginMoment(){
        return PreferenceUtil.getLong(PreferenceUtil.LAST_LOGIN_MOMENT);
    }


}
