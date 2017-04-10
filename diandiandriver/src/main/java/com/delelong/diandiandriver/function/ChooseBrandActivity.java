package com.delelong.diandiandriver.function;

import android.app.ProgressDialog;
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
import com.delelong.diandiandriver.dialog.MyToastDialog;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.utils.ImageLoaderUtils;
import com.delelong.diandiandriver.utils.ToastUtil;

import java.util.List;

import static com.delelong.diandiandriver.R.id.img_brand;

/**
 * Created by Administrator on 2016/10/14.
 */

public class ChooseBrandActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TextWatcher {

    private static final String TAG = "BAIDUMAPFORTEST";
    Context context;
    private int pageIndex = 1;
    private final int pageSize = 6;

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
    List<CarBrandBean.CarBrand> carBrands;

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
        ProgressDialog progressDialog = ProgressDialog.show(ChooseBrandActivity.this, null, "加载中...");
        CarBrandBean mCarBrandBean = myHttpUtils.getCarBrands(url_upDate, pageIndex, pageSize, brandName);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (mCarBrandBean == null || mCarBrandBean.getCarBrands().size() == 0) {
            ToastUtil.show(this, "暂未获取到信息，请稍后再试");
            return;
        }
        if (carBrands == null) {
            carBrands = mCarBrandBean.getCarBrands();
        } else {
            carBrands.removeAll(carBrands);
            carBrands.addAll(mCarBrandBean.getCarBrands());
        }
        if (carBrands != null) {
            if (brandAdapter == null) {
                brandAdapter = new MyBrandAdapter(carBrands);
                lv_brand.setAdapter(brandAdapter);
            } else {
                brandAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("carBrand", carBrands.get(position));
        setResult(Str.REQUESTBRANDCODE, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        String brandName = edt_brand.getText().toString();
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.tv_searchBrand:
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
                    setAdapter(Str.URL_CAR_BRAND, pageIndex, pageSize, brandName);
                } else {
                    MyToastDialog.show(ChooseBrandActivity.this, "已是第一页");
                }
                break;
            case R.id.btn_next:
                //如果size不等于（小于10），说明到最后一页了
                if (carBrands == null) {
                    return;
                }
                if (carBrands.size() == pageSize) {
                    ++pageIndex;
                    Log.i(TAG, "onClick: " + pageIndex);
                    setAdapter(Str.URL_CAR_BRAND, pageIndex, pageSize, brandName);
                } else {
                    MyToastDialog.show(ChooseBrandActivity.this, "已到最后一页");
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
        List<CarBrandBean.CarBrand> carBrands;
        Bitmap head;
        int countNum;

        public MyBrandAdapter(List<CarBrandBean.CarBrand> carBrands) {
            this.carBrands = carBrands;
        }

        @Override
        public int getCount() {
            return carBrands.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            CarBrandBean.CarBrand carBrand = carBrands.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_list_brand, null);
                holder = new ViewHolder();
                holder.tv_brand = (TextView) convertView.findViewById(R.id.tv_brand);
                holder.img_brand = (ImageView) convertView.findViewById(img_brand);
//                holder.tv_brand.setTag(position);
//                holder.img_brand.setTag(carBrand);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (carBrand == null) {
                return convertView;
            }
            if (carBrand.getLogo() != null && !carBrand.getLogo().equals("")) {
                ImageLoaderUtils.display(context,holder.img_brand,Str.URL_SERVICE_IMAGEPATH + carBrand.getLogo());
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
