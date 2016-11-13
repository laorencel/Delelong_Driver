package com.delelong.diandiandriver.menuActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.MyFanLiInfo;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyHttpUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/11/4.
 */

public class MyFanLiActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BAIDUMAPFORTEST";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fanli);
        initActionBar();
        initView();
        setMyFanLiAdapter(pageIndex,pageSize);
    }


    ListView lv_fanli;
    TextView tv_no_info;
    Button btn_preV, btn_next;

    private void initView() {
        lv_fanli = (ListView) findViewById(R.id.lv_fanli);
        tv_no_info = (TextView) findViewById(R.id.tv_no_info);
        btn_preV = (Button) findViewById(R.id.btn_preV);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_preV.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    ImageView arrow_back;

    private void initActionBar() {
        arrow_back = (ImageView) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }

    MyHttpUtils myHttpUtils;
    List<MyFanLiInfo> myFanLiInfos;
    MyFanLiAdapter myFanLiAdapter;
    private int pageIndex = 1;
    private final int pageSize = 10;

    private void setMyFanLiAdapter(int pageIndex,int pageSize) {
        if (myHttpUtils == null) {
            myHttpUtils = new MyHttpUtils(this);
        }
        myFanLiInfos = myHttpUtils.getFanLiInfo(Str.URL_FANLI_INFO, pageIndex, pageSize);
        if (myFanLiInfos == null || myFanLiInfos.size() == 0) {
            tv_no_info.setVisibility(View.VISIBLE);
            lv_fanli.setVisibility(View.GONE);
            return;
        }
        tv_no_info.setVisibility(View.GONE);
        lv_fanli.setVisibility(View.VISIBLE);
        myFanLiAdapter = new MyFanLiAdapter(this,myFanLiInfos);
        lv_fanli.setAdapter(myFanLiAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.btn_preV:
                if (pageIndex > 1) {
                    --pageIndex;
                    Log.i(TAG, "onClick: " + pageIndex);
                    setMyFanLiAdapter(pageIndex,pageSize);
                }
                break;
            case R.id.btn_next:
                if (myFanLiInfos == null){
                    return;
                }
                if (myFanLiInfos.size() == pageSize){
                    ++pageIndex;
                    Log.i(TAG, "onClick: " + pageIndex);
                    setMyFanLiAdapter(pageIndex,pageSize);
                }
                break;
        }
    }

    class MyFanLiAdapter extends BaseAdapter {
        Context context;
        List<MyFanLiInfo> mFanLiInfos;

        public MyFanLiAdapter(Context context, List<MyFanLiInfo> myFanLiInfos) {
            this.context = context;
            this.mFanLiInfos = myFanLiInfos;
        }

        @Override
        public int getCount() {
            if (mFanLiInfos == null){
                return 0;
            }else {
                return mFanLiInfos.size();
            }
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
            MyFanLiInfo fanLiInfo = mFanLiInfos.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_fanli, null);
                holder = new ViewHolder();
                holder.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
                holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (fanLiInfo == null){
                return convertView;
            }
            holder.tv_amount.setText("￥ "+fanLiInfo.getAmount());
            holder.tv_create_time.setText(fanLiInfo.getCreate_time());
            return convertView;
        }

        class ViewHolder {
            TextView tv_create_time, tv_amount;
        }
    }
}
