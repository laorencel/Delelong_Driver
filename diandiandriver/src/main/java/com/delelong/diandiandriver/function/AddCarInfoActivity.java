package com.delelong.diandiandriver.function;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.CarBrandBean;
import com.delelong.diandiandriver.bean.CarModelBean;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */

public class AddCarInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "BAIDUMAPFORTEST";
    MyHttpUtils myHttpUtils;
    MyAddCarInfo mAddCarInfo;
    MyAddCarDialog dialog;
    CarBrandBean.CarBrand mCarBrand;
    CarModelBean.CarModel mCarModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        initActionBar();
        initView();
        initMsg();
    }

    //品牌，型号，车牌号，车架号，车身颜色，车辆图片
    RelativeLayout rl_brand, rl_model, rl_plateNumber, rl_vin, rl_color, rl_picture;
    TextView tv_brand, tv_model, tv_plateNumber, tv_vin, tv_color;
    ImageView img_picture;

    private void initView() {
        rl_brand = (RelativeLayout) findViewById(R.id.rl_brand);
        rl_model = (RelativeLayout) findViewById(R.id.rl_model);
        rl_plateNumber = (RelativeLayout) findViewById(R.id.rl_plateNumber);
        rl_vin = (RelativeLayout) findViewById(R.id.rl_vin);
        rl_color = (RelativeLayout) findViewById(R.id.rl_color);
        rl_picture = (RelativeLayout) findViewById(R.id.rl_picture);

        tv_brand = (TextView) findViewById(R.id.tv_brand);
        tv_model = (TextView) findViewById(R.id.tv_model);
        tv_plateNumber = (TextView) findViewById(R.id.tv_plateNumber);
        tv_vin = (TextView) findViewById(R.id.tv_vin);
        tv_color = (TextView) findViewById(R.id.tv_color);

        img_picture = (ImageView) findViewById(R.id.img_picture);

        rl_brand.setOnClickListener(this);
        rl_model.setOnClickListener(this);
        rl_plateNumber.setOnClickListener(this);
        rl_vin.setOnClickListener(this);
        rl_color.setOnClickListener(this);
        rl_picture.setOnClickListener(this);
    }

    ImageButton arrow_back;
    TextView tv_confirm;

    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        arrow_back.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }

    private void initMsg() {
        myHttpUtils = new MyHttpUtils(this);
        mAddCarInfo = new MyAddCarInfo();
        dialog = new MyAddCarDialog(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.tv_confirm:
                Log.i(TAG, "tv_confirm: "+mAddCarInfo);
                if (!canUpdate()){
                    return;
                }
                List<String > result = myHttpUtils.plusCar(Str.URL_PLUS_CAR,mAddCarInfo);
                if (result.get(0).equalsIgnoreCase("OK")){
                    ToastUtil.show(this,"添加车辆信息成功！");
                    finish();
                }else {
                    ToastUtil.show(this,"添加失败，请重试！");
                }
                break;
            case R.id.rl_brand:
                startActivityForResult(new Intent(this, ChooseBrandActivity.class), Str.REQUESTBRANDCODE);
                break;
            case R.id.rl_model:
                if (mCarBrand == null){
                    ToastUtil.show(this,"请先选择车辆品牌");
                    return;
                }
                Intent intent_model = new Intent(this, ChooseModelActivity.class);
                intent_model.putExtra("brand",mCarBrand);
                startActivityForResult(intent_model, Str.REQUESTMODELCODE);
                break;
            case R.id.rl_plateNumber:
                dialog.showAddPlateNumber(new MyAddCarDialog.AddCarInterface(){
                    @Override
                    public void sure(String result) {
                        tv_plateNumber.setText(result);
                        mAddCarInfo.setPlateNumber(result);
                    }
                });
                break;
            case R.id.rl_vin:
                dialog.showAddVin(new MyAddCarDialog.AddCarInterface(){
                    @Override
                    public void sure(String result) {
                        tv_vin.setText(result);
                        mAddCarInfo.setVin(result);
                    }
                });
                break;
            case R.id.rl_color:
                dialog.showAddColor(new MyAddCarDialog.AddCarInterface(){
                    @Override
                    public void sure(String result) {
                        tv_color.setText(result);
                        mAddCarInfo.setColor(result);
                    }
                });
                break;
            case R.id.rl_picture:
                dialog.showAddPicture(new MyAddCarDialog.AddCarInterface(){
                    @Override
                    public void sure(String result) {
                        if (result.equals("camera")){//拍照
                            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intentCamera, Str.REQUESTCODECAMERA);
                        }else if (result.equals("album")){//相册
                            Intent intentAlbum = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intentAlbum, Str.REQUESTCODEALBUM);
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (requestCode == Str.REQUESTBRANDCODE) {//brand
            Log.i(TAG, "onActivityResult: carBrand");
            mCarBrand = (CarBrandBean.CarBrand) data.getSerializableExtra("carBrand");
            mCarModel = null;//重置mCarModel
            tv_brand.setText(mCarBrand.getName());
            mAddCarInfo.setBrand(mCarBrand.getId());
        }else if (requestCode == Str.REQUESTMODELCODE) {//brand_model
            Log.i(TAG, "onActivityResult: carModel");
            mCarModel = (CarModelBean.CarModel) data.getSerializableExtra("carModel");
            tv_model.setText(mCarModel.getName());
            mAddCarInfo.setModel(mCarModel.getId());
        }else if (requestCode == Str.REQUESTCODECAMERA) {//camera
            bitmap = getCamera(data, bitmap);
        } else if (requestCode == Str.REQUESTCODEALBUM) {//album
            bitmap = getAlbum(data, bitmap);
        }
        if (bitmap != null){//获取到图片
            img_picture.setImageBitmap(bitmap);
            String imgPath = Str.FILEPATH+"carImage.JPEG";
            File file = new File(imgPath);
            if (!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            createImage(Str.FILEPATH, "carImage.JPEG", bitmap);
            List<String> imageResult = myHttpUtils.upDateFile(Str.URL_UPDATEFILE, imgPath);
            mAddCarInfo.setPicture(imageResult.get(2));
        }else {
            ToastUtil.show(this,"未获取到图片，请重试");
        }
    }

    public boolean canUpdate(){
        if (mAddCarInfo.getBrand()==99999){
            ToastUtil.show(AddCarInfoActivity.this,"未添加品牌");
            return false;
        }
        if (mAddCarInfo.getModel()==99999){
            ToastUtil.show(AddCarInfoActivity.this,"未添加型号");
            return false;
        }
        if (mAddCarInfo.getColor() == null){
            ToastUtil.show(AddCarInfoActivity.this,"未添加车身颜色");
            return false;
        }
        if (mAddCarInfo.getPicture() == null){
            ToastUtil.show(AddCarInfoActivity.this,"未添加车辆图片");
            return false;
        }
        if (mAddCarInfo.getPlateNumber() == null){
            ToastUtil.show(AddCarInfoActivity.this,"未添加车牌号");
            return false;
        }
        if (mAddCarInfo.getVin() == null){
            ToastUtil.show(AddCarInfoActivity.this,"未添加车架号");
            return false;
        }
        return true;
    }
    public class MyAddCarInfo{
        private int brand = 99999,model = 99999;
        private String plateNumber,color,vin,picture;

        public MyAddCarInfo() {
        }

        /**
         *
         * @param brand
         * @param model
         * @param plateNumber //车牌号
         * @param color //车身颜色
         * @param vin //车架号
         * @param picture //车辆图片
         */
        public MyAddCarInfo(int brand, int model, String plateNumber, String color, String vin, String picture) {
            this.brand = brand;
            this.model = model;
            this.plateNumber = plateNumber;
            this.color = color;
            this.vin = vin;
            this.picture = picture;
        }

        public int getBrand() {
            return brand;
        }

        public void setBrand(int brand) {
            this.brand = brand;
        }

        public int getModel() {
            return model;
        }

        public void setModel(int model) {
            this.model = model;
        }

        public String getPlateNumber() {
            return plateNumber;
        }

        public void setPlateNumber(String plateNumber) {
            this.plateNumber = plateNumber;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        @Override
        public String toString() {
            return "MyAddCarInfo{" +
                    "brand=" + brand +
                    ", model=" + model +
                    ", plateNumber='" + plateNumber + '\'' +
                    ", color='" + color + '\'' +
                    ", vin='" + vin + '\'' +
                    ", picture='" + picture + '\'' +
                    '}';
        }
    }
}
