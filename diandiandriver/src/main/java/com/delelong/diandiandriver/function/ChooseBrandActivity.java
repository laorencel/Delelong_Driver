package com.delelong.diandiandriver.function;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.CarBrandBean;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.utils.ToastUtil;

/**
 * Created by Administrator on 2016/10/14.
 */

public class ChooseBrandActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TextWatcher {

    private static final String TAG = "BAIDUMAPFORTEST";
    Context context;
    private int pageIndex = 1;
    private final int pageSize = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_brand);
        initActionBar();
        initView();
        initMsg();
    }

    ImageButton arrow_back;

    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }

    EditText edt_brand;
    TextView tv_searchBrand;
    ListView lv_brand;
    TextView btn_preV, btn_next;
    MyBrandAdapter brandAdapter;
    MyHttpUtils myHttpUtils;
    CarBrandBean mCarBrandBean;

    private void initView() {
        context = this;
        edt_brand = (EditText) findViewById(R.id.edt_brand);
        tv_searchBrand = (TextView) findViewById(R.id.tv_searchBrand);
        tv_searchBrand.setEnabled(false);
        lv_brand = (ListView) findViewById(R.id.lv_brand);
        btn_preV = (TextView) findViewById(R.id.btn_preV);
        btn_next = (TextView) findViewById(R.id.btn_next);

        edt_brand.addTextChangedListener(this);
        tv_searchBrand.setOnClickListener(this);
        btn_preV.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        lv_brand.setOnItemClickListener(this);
    }

    private void initMsg() {
        myHttpUtils = new MyHttpUtils(this);
        setAdapter(Str.URL_CAR_BRAND, pageIndex, pageSize, "");

    }

    private void setAdapter(String url_upDate, int pageIndex, int pageSize, String brandName) {
        mCarBrandBean = myHttpUtils.getCarBrands(url_upDate, pageIndex, pageSize, brandName);
        if (mCarBrandBean == null) {
            ToastUtil.show(this, "暂未获取到信息，请稍后再试");
            return;
        }
        brandAdapter = new MyBrandAdapter(mCarBrandBean);
        lv_brand.setAdapter(brandAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: ");
        Intent intent = new Intent();
        intent.putExtra("carBrand", mCarBrandBean.getCarBrands().get(position));
        setResult(Str.REQUESTBRANDCODE, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.tv_searchBrand:
                String brandName = edt_brand.getText().toString();
                if (brandName.equals("")) {
                    return;
                }
                pageIndex = 1;
                setAdapter(Str.URL_CAR_BRAND, pageIndex, pageSize, brandName);
                break;
            case R.id.btn_preV:
                //第一页不搜索
                if (pageIndex > 1) {
                    --pageIndex;
                    Log.i(TAG, "onClick: " + pageIndex);
                    setAdapter(Str.URL_CAR_BRAND, pageIndex, pageSize, "");
                }
                break;
            case R.id.btn_next:
                //如果size不等于（小于10），说明到最后一页了
                if (mCarBrandBean.getCarBrands().size() == pageSize) {
                    ++pageIndex;
                    Log.i(TAG, "onClick: " + pageIndex);
                    setAdapter(Str.URL_CAR_BRAND, pageIndex, pageSize, "");
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s)) {
            tv_searchBrand.setEnabled(false);
            pageIndex = 1;
            setAdapter(Str.URL_CAR_BRAND, pageIndex, pageSize, "");
        } else {
            tv_searchBrand.setEnabled(true);
        }
    }

    class MyBrandAdapter extends BaseAdapter {
        CarBrandBean carBrandBean;

        public MyBrandAdapter(CarBrandBean carBrandBean) {
            this.carBrandBean = carBrandBean;
        }

        @Override
        public int getCount() {
            return carBrandBean.getCarBrands().size();
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
            CarBrandBean.CarBrand carBrand = carBrandBean.getCarBrands().get(position);
            Bitmap imgBitmap = myHttpUtils.downloadImage(Str.URL_SERVICE_IMAGEPATH + carBrand.getLogo());
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_list_brand, null);
                holder = new ViewHolder();
                holder.tv_brand = (TextView) convertView.findViewById(R.id.tv_brand);
                holder.img_brand = (ImageView) convertView.findViewById(R.id.img_brand);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (carBrand == null) {
                return convertView;
            }
            if (imgBitmap != null) {
                holder.img_brand.setImageBitmap(myHttpUtils.downloadImage(Str.URL_SERVICE_IMAGEPATH + carBrand.getLogo()));
            }
            holder.tv_brand.setText(carBrand.getName());
            return convertView;
        }

        class ViewHolder {
            ImageView img_brand;
            TextView tv_brand;
        }
    }
}
