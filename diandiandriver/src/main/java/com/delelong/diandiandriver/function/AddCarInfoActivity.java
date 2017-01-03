package com.delelong.diandiandriver.function;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.CarBrandBean;
import com.delelong.diandiandriver.bean.CarModelBean;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyHttpHelper;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.delelong.diandiandriver.R.array.carType;

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
    Spinner spinner_carType;
    RelativeLayout rl_brand, rl_model, rl_plateNumber, rl_vin, rl_color, rl_picture;
    TextView tv_brand, tv_model, tv_plateNumber, tv_vin, tv_color;
    ImageView img_picture;

    private void initView() {
        spinner_carType = (Spinner) findViewById(R.id.spinner_carType);
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

        spinner_carType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String carTypeStr = getResources().getStringArray(carType)[position];
                if (carTypeStr != null) {
                    String carTypeNum = transformCarType(carTypeStr);
                    mAddCarInfo.setCarType(carTypeNum);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rl_brand.setOnClickListener(this);
        rl_model.setOnClickListener(this);
        rl_plateNumber.setOnClickListener(this);
        rl_vin.setOnClickListener(this);
        rl_color.setOnClickListener(this);
        rl_picture.setOnClickListener(this);
    }

    private String transformCarType(String carTypeStr) {
        String carTypeNum = "";
        switch (carTypeStr) {
            case "快车":
                carTypeNum = "44";
                break;
            case "专车-舒适型":
                carTypeNum = "43";
                break;
            case "专车-豪华型":
                carTypeNum = "37";
                break;
            case "专车-七座商务型":
                carTypeNum = "45";
                break;
            case "出租车":
                carTypeNum = "42";
                break;
        }
        return carTypeNum;
    }

    ImageButton arrow_back;
    TextView tv_confirm;

    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        arrow_back.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }

    MyHttpHelper myHttpHelper;

    private void initMsg() {
        myHttpHelper = new MyHttpHelper(this);
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
                Log.i(TAG, "tv_confirm: " + mAddCarInfo);
                if (!canUpdate()) {
                    return;
                }
//                RequestParams params = myHttpHelper.getPlusCarParams(mAddCarInfo);
//                MyAsyncHttpUtils.post(Str.URL_PLUS_CAR, params, new MyProgTextHttpResponseHandler(this) {
//                    @Override
//                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
////                        MyToastDialog.show(AddCarInfoActivity.this,"添加失败，请重试！");
//                        ToastUtil.show(AddCarInfoActivity.this,"添加失败，请重试！");
//                        Log.i(TAG, "onFailure:AddCarInfoActivity: " + s);
//                    }
//
//                    @Override
//                    public void onSuccess(int i, Header[] headers, String s) {
//                        List<String> result = myHttpHelper.resultByJson(s, null);
//
//                    }
//                });
                List<String> result = myHttpUtils.plusCar(Str.URL_PLUS_CAR, mAddCarInfo);
                if (result == null) {
                    ToastUtil.show(AddCarInfoActivity.this,"添加失败，请重试！");
                    return;
                }
                if (result.get(0).equalsIgnoreCase("OK")) {
                    ToastUtil.show(AddCarInfoActivity.this,"添加车辆信息成功,等待审核！");
                    finish();
                } else {
                    ToastUtil.show(AddCarInfoActivity.this,"添加失败，" + result.get(1));
                }
                break;
            case R.id.rl_brand:
                startActivityForResult(new Intent(this, ChooseBrandActivity.class), Str.REQUESTBRANDCODE);
                break;
            case R.id.rl_model:
                if (mCarBrand == null) {
//                    MyToastDialog.show(AddCarInfoActivity.this,"请先选择车辆品牌");
                    ToastUtil.show(AddCarInfoActivity.this,"请先选择车辆品牌");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("brand", mCarBrand);
                Intent intent_model = new Intent(this, ChooseModelActivity.class);
                intent_model.putExtra("bundle", bundle);
                startActivityForResult(intent_model, Str.REQUESTMODELCODE);
                break;
            case R.id.rl_plateNumber:
                dialog.showAddPlateNumber(new MyAddCarDialog.AddCarInterface() {
                    @Override
                    public void sure(String result) {
                        tv_plateNumber.setText(result);
                        mAddCarInfo.setPlateNumber(result);
                    }
                });
                break;
            case R.id.rl_vin:
                dialog.showAddVin(new MyAddCarDialog.AddCarInterface() {
                    @Override
                    public void sure(String result) {
                        tv_vin.setText(result);
                        mAddCarInfo.setVin(result);
                    }
                });
                break;
            case R.id.rl_color:
                dialog.showAddColor(new MyAddCarDialog.AddCarInterface() {
                    @Override
                    public void sure(String result) {
                        tv_color.setText(result);
                        mAddCarInfo.setColor(result);
                    }
                });
                break;
            case R.id.rl_picture:
                permissionExternalStorage();
                showAddPictureDialog();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Str.REQUEST_WRITE_EXTERNALSTORAGE || requestCode == Str.REQUEST_DELE_CREATE_EXTERNALSTORAGE) {
            if (grantResults != null && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAddPictureDialog();
                }
            }
        }
    }

    private void showAddPictureDialog() {
        if (isNull(dialog)) {
            dialog = new MyAddCarDialog(this);
        }
        if (dialog.isShowing()) {
            return;
        }
        dialog.showAddPicture(new MyAddCarDialog.AddCarInterface() {
            @Override
            public void sure(String result) {
                if (result.equals("camera")) {//拍照
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intentCamera, Str.REQUESTCODECAMERA);
                } else if (result.equals("album")) {//相册
                    Intent intentAlbum = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentAlbum, Str.REQUESTCODEALBUM);
                }
            }
        });
    }

    ProgressDialog progressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        progressDialog = ProgressDialog.show(this, null, null);
        Bitmap bitmap = null;
        if (requestCode == Str.REQUESTBRANDCODE) {//brand
            Log.i(TAG, "onActivityResult: carBrand");
            mCarBrand = (CarBrandBean.CarBrand) data.getSerializableExtra("carBrand");
            if (mCarBrand == null) {
                return;
            }
            mCarModel = null;//重置mCarModel
            tv_brand.setText(mCarBrand.getName());
            mAddCarInfo.setBrand(mCarBrand.getId());
        } else if (requestCode == Str.REQUESTMODELCODE) {//brand_model
            Log.i(TAG, "onActivityResult: carModel");

            mCarModel = (CarModelBean.CarModel) data.getSerializableExtra("carModel");
            if (mCarModel == null) {
                return;
            }
            tv_model.setText(mCarModel.getName());
            mAddCarInfo.setModel(mCarModel.getId());
        } else if (requestCode == Str.REQUESTCODECAMERA) {//camera
            bitmap = getCamera(data, bitmap);
            updateFile(bitmap);

        } else if (requestCode == Str.REQUESTCODEALBUM) {//album
            bitmap = getAlbum(data, bitmap);
            updateFile(bitmap);
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void updateFile(Bitmap bitmap) {
        if (bitmap != null) {//获取到图片
            img_picture.setImageBitmap(bitmap);
            String imgPath = Str.FILEIMAGEPATH + File.separator + "carImage.JPEG";
            File fileDir = new File(Str.FILEIMAGEPATH);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File file = new File(fileDir.getAbsolutePath() + File.separator + "carImage.JPEG");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "updateFile: " + e);
                }
            }
            MyCreateImageThread myCreateImageThread = new MyCreateImageThread(bitmap);
            myCreateImageThread.start();
            try {
                myCreateImageThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            createImage(Str.FILEIMAGEPATH, "carImage.JPEG", bitmap);
            List<String> imageResult = myHttpUtils.upDateFile(Str.URL_UPDATEFILE, imgPath);
            if (imageResult == null) {
                return;
            }
            mAddCarInfo.setPicture(imageResult.get(2));
        } else {
            ToastUtil.show(AddCarInfoActivity.this,"未获取到图片，请重试");
//            MyToastDialog.show(this,"未获取到图片，请重试");
        }
    }

    private class MyCreateImageThread extends Thread {
        Bitmap bitmap;

        MyCreateImageThread(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
            super.run();
            createImage(Str.FILEIMAGEPATH, File.separator + "carImage.JPEG", bitmap);
        }
    }

    public boolean canUpdate() {
        if (mAddCarInfo.getBrand() == 99999) {
//            MyToastDialog.show(AddCarInfoActivity.this,"未添加品牌");
            ToastUtil.show(AddCarInfoActivity.this,"未添加品牌");
            return false;
        }
        if (mAddCarInfo.getModel() == 99999) {
//            MyToastDialog.show(AddCarInfoActivity.this,"未添加型号");
            ToastUtil.show(AddCarInfoActivity.this,"未添加型号");
            return false;
        }
        if (mAddCarInfo.getCarType() == null) {
//            MyToastDialog.show(AddCarInfoActivity.this,"未选择车辆类型");
            ToastUtil.show(AddCarInfoActivity.this,"未选择车辆类型");
            return false;
        }
        if (mAddCarInfo.getColor() == null) {
//            MyToastDialog.show(AddCarInfoActivity.this,"未添加车身颜色");
            ToastUtil.show(AddCarInfoActivity.this,"未添加车身颜色");
            return false;
        }
        if (mAddCarInfo.getPicture() == null) {
//            MyToastDialog.show(AddCarInfoActivity.this,"未添加车辆图片");
            ToastUtil.show(AddCarInfoActivity.this,"未添加车辆图片");
            return false;
        }
        if (mAddCarInfo.getPlateNumber() == null) {
//            MyToastDialog.show(AddCarInfoActivity.this,"未添加车牌号");
            ToastUtil.show(AddCarInfoActivity.this,"未添加车牌号");
            return false;
        }
        if (mAddCarInfo.getVin() == null) {
//            MyToastDialog.show(AddCarInfoActivity.this,"未添加车架号");
            ToastUtil.show(AddCarInfoActivity.this,"未添加车架号");
            return false;
        }
        return true;
    }

    public class MyAddCarInfo {
        private int brand = 99999, model = 99999;
        private String carType, plateNumber, color, vin, picture;

        public MyAddCarInfo() {
        }

        /**
         * @param brand
         * @param model
         * @param plateNumber //车牌号
         * @param color       //车身颜色
         * @param vin         //车架号
         * @param picture     //车辆图片
         */
        public MyAddCarInfo(int brand, int model, String carType, String plateNumber, String color, String vin, String picture) {
            this.brand = brand;
            this.model = model;
            this.carType = carType;
            this.plateNumber = plateNumber;
            this.color = color;
            this.vin = vin;
            this.picture = picture;
        }

        public String getCarType() {
            return carType;
        }

        public void setCarType(String carType) {
            this.carType = carType;
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
