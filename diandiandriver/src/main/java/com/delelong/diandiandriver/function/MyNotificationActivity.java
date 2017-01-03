package com.delelong.diandiandriver.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.MyNoticeInfo;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyAsyncHttpUtils;
import com.delelong.diandiandriver.http.MyHttpHelper;
import com.delelong.diandiandriver.http.MyProgTextHttpResponseHandler;
import com.delelong.diandiandriver.http.MyTextHttpResponseHandler;
import com.delelong.diandiandriver.menuActivity.MyWebViewActivity;
import com.delelong.diandiandriver.view.BadgeView;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.delelong.diandiandriver.R.id.tv_notice_content;

/**
 * 公告
 * Created by Administrator on 2016/11/4.
 */

public class MyNotificationActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView tv_notification;
    MyHttpHelper myHttpHelper;
    List<MyNoticeInfo> myNoticeInfos;
    MyNoticeAdapter mNoticeAdapter;
    ListView lv_notice;
    String url;
    private static final String TAG = "BAIDUMAPFORTEST";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initActionBar();
        tv_notification = (TextView) findViewById(R.id.tv_notification);
        lv_notice = (ListView) findViewById(R.id.lv_notice);

        url = getIntent().getStringExtra("url");
        if (isNull(url)) {
            url = Str.URL_NOTICE;
        }
        if (url.equalsIgnoreCase(Str.URL_NOTICE)) {
            tv_bar_title.setText("我的公告");
        } else {
            tv_bar_title.setText("我的消息");
        }
        setAdapter();

    }

    private void setAdapter() {
        if (myHttpHelper == null) {
            myHttpHelper = new MyHttpHelper(this);
        }
        MyAsyncHttpUtils.get(url, new MyProgTextHttpResponseHandler(this) {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                lv_notice.setVisibility(View.GONE);
                if (url.equalsIgnoreCase(Str.URL_NOTICE)) {
                    tv_notification.setText("暂无公告");
                } else {
                    tv_notification.setText("暂无消息");
                }
                tv_notification.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if (myNoticeInfos == null) {
                    myNoticeInfos = myHttpHelper.getNoticesByJson(s, null);
                } else {
                    List<MyNoticeInfo> myNoticeInfoList = myHttpHelper.getNoticesByJson(s, null);
                    if (myNoticeInfoList != null && myNoticeInfoList.size() > 0) {
                        myNoticeInfos.removeAll(myNoticeInfos);
                        myNoticeInfos.addAll(myNoticeInfoList);
                    } else {
                        lv_notice.setVisibility(View.GONE);
                        if (url.equalsIgnoreCase(Str.URL_NOTICE)) {
                            tv_notification.setText("暂无公告");
                        } else {
                            tv_notification.setText("暂无消息");
                        }
                        tv_notification.setVisibility(View.VISIBLE);
                    }
                }
                if (myNoticeInfos == null || myNoticeInfos.size() == 0) {
                    lv_notice.setVisibility(View.GONE);
                    if (url.equalsIgnoreCase(Str.URL_NOTICE)) {
                        tv_notification.setText("暂无公告");
                    } else {
                        tv_notification.setText("暂无消息");
                    }
                    tv_notification.setVisibility(View.VISIBLE);
                } else {
                    lv_notice.setVisibility(View.VISIBLE);
                    tv_notification.setVisibility(View.GONE);
                    if (mNoticeAdapter == null) {
                        mNoticeAdapter = new MyNoticeAdapter(MyNotificationActivity.this, myNoticeInfos);
                        lv_notice.setAdapter(mNoticeAdapter);
                        lv_notice.setOnItemClickListener(MyNotificationActivity.this);
                    } else {
                        mNoticeAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    ImageButton arrow_back;
    TextView tv_bar_title;

    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        tv_bar_title = (TextView) findViewById(R.id.tv_bar_title);
        arrow_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (myNoticeInfos != null && myNoticeInfos.size() > 0) {
            final MyNoticeInfo myNoticeInfo = myNoticeInfos.get(position);
            final MyNoticeAdapter.ViewHolder viewHolder = (MyNoticeAdapter.ViewHolder) lv_notice.getChildAt(position - lv_notice.getFirstVisiblePosition()).getTag();

            String url = myNoticeInfo.getUrl();
            if (url != null && !url.equals("")) {
                Intent intent = new Intent(this, MyWebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
            if (myNoticeInfo.getRead_flag().equalsIgnoreCase("0")) {
                RequestParams params = myHttpHelper.getUpdateMessageParams(myNoticeInfo.getId());
                MyAsyncHttpUtils.get(Str.URL_UPDATE_MESSAGE, params, new MyTextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        super.onFailure(i, headers, s, throwable);
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        super.onSuccess(i, headers, s);
                        Log.i(TAG, "onSuccess: " + s);
                        if (viewHolder.badgeView != null && viewHolder.badgeView.isShown()) {
                            viewHolder.badgeView.hide();
                        }
                        myNoticeInfo.setRead_flag("1");
                    }
                });
            } else {
                if (viewHolder.tv_notice_content.getLineCount() == 1) {
                    viewHolder.tv_notice_content.setEllipsize(null);
                    viewHolder.tv_notice_content.setSingleLine(false);
                } else {
                    viewHolder.tv_notice_content.setEllipsize(TextUtils.TruncateAt.END);
                    viewHolder.tv_notice_content.setSingleLine(true);
                }
            }
        }
    }

    class MyNoticeAdapter extends BaseAdapter {
        Context context;
        List<MyNoticeInfo> myNoticeInfos;

        public MyNoticeAdapter(Context context, List<MyNoticeInfo> myNoticeInfos) {
            this.context = context;
            this.myNoticeInfos = myNoticeInfos;
        }

        @Override
        public int getCount() {
            return myNoticeInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyNoticeInfo noticeInfo = myNoticeInfos.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_notice, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_notice_title = (TextView) convertView.findViewById(R.id.tv_notice_title);
                viewHolder.tv_screate_time = (TextView) convertView.findViewById(R.id.tv_screate_time);
                viewHolder.tv_notice_content = (TextView) convertView.findViewById(tv_notice_content);

                if (noticeInfo.getRead_flag().equalsIgnoreCase("0")) {
                    viewHolder.badgeView = new BadgeView(context, viewHolder.tv_screate_time);
                    viewHolder.badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                    viewHolder.badgeView.setWidthHeight(18);
                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (noticeInfo == null) {
                return convertView;
            }
            Log.i(TAG, "getView: " + noticeInfo);
            viewHolder.tv_notice_title.setText(noticeInfo.getTitle());
            viewHolder.tv_screate_time.setText(noticeInfo.getScreate_time());
            viewHolder.tv_notice_content.setText(noticeInfo.getContent());
            viewHolder.tv_notice_content.setEllipsize(TextUtils.TruncateAt.END);
            if (noticeInfo.getRead_flag().equalsIgnoreCase("0")) {
                if (viewHolder.badgeView != null) {
                    viewHolder.badgeView.show();
                }
            }
            return convertView;
        }

        class ViewHolder {
            TextView tv_notice_title, tv_screate_time, tv_notice_content;
            BadgeView badgeView;
        }
    }
}
