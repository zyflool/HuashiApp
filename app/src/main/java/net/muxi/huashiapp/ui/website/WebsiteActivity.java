package net.muxi.huashiapp.ui.website;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.muxi.huashiapp.R;
import net.muxi.huashiapp.common.base.ToolbarActivity;
import net.muxi.huashiapp.common.data.WebsiteData;
import net.muxi.huashiapp.common.db.HuaShiDao;
import net.muxi.huashiapp.common.net.CampusFactory;
import net.muxi.huashiapp.util.ToastUtil;
import net.muxi.huashiapp.ui.webview.WebViewActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

/**
 * Created by december on 16/11/2.
 */

public class WebsiteActivity extends ToolbarActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private HuaShiDao mDao;
    private List<WebsiteData> mWebsiteDatas;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);
        ButterKnife.bind(this);

        mDao = new HuaShiDao();
        try {
            mWebsiteDatas = mDao.loadSite();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context,R.color.colorPrimary));
        if (mWebsiteDatas != null && mWebsiteDatas.size() > 0) {
            setupRecyclerView(mWebsiteDatas);
        } else {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }
        mSwipeRefreshLayout.setEnabled(false);



        setTitle("常用网站");
        CampusFactory.getRetrofitService().getWebsite()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<List<WebsiteData>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtil.showShort(getString(R.string.tip_net_error));
                    }

                    @Override
                    public void onNext(List<WebsiteData> websiteData) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (mWebsiteDatas == null || websiteData.size() != mWebsiteDatas.size()) {
                            setupRecyclerView(websiteData);
                            mDao.deleteWebsite();
                            for (WebsiteData data : websiteData){
                                mDao.insertSite(data);
                            }
                        }
                    }
                });
    }


    public void setupRecyclerView(List<WebsiteData> websiteData) {
        WebsiteAdapter adapter;
        adapter = new WebsiteAdapter(websiteData);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new WebsiteAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, List<WebsiteData> websiteData, int position) {
                //Uri uri = Uri.parse(websiteData.get(position).getUrl());
                //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                //startActivity(intent);
                Intent intent = WebViewActivity.newIntent(WebsiteActivity.this,websiteData.get(position).getUrl());
                startActivity(intent);
                //finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}