package com.delelong.diandiandriver.start;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.Str;

import java.io.File;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/12/2.
 */

public class MyStartViewPagerActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    // 定义ViewPager对象
    private ViewPager viewPager;
    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;
    // 定义一个ArrayList来存放View
    private ArrayList<View> views;

    // 声明main布局对象,对应main.xml
    private View main;

    private ImageView img_skip;
    // 声明导航显示区域,因为要装载多个小圆点,所以声明为ViewGroup
    private ViewGroup pointGroup;
    // 引导图片资源
    private static final int[] pics = {R.drawable.address_company, R.drawable.address_home};
    // 底部小点的图片
    private ImageView[] points;
    // 具体一个小圆点
    private ImageView imageView;
    // 记录当前选中位置
    private int currentIndex;


    private Button button;
    private static final int TURN_TO_NEXT = 51;
    private static final int SETRESULT = 52;

    private Handler startHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TURN_TO_NEXT://需要amaplocation
                    currentIndex++;
                    setCurView(currentIndex);
                    setCurDot(currentIndex);
                    if (files != null) {
                        if (files.length > 1) {
                            if (currentIndex == files.length - 1) {
                                startHandler.sendEmptyMessageDelayed(TURN_TO_NEXT, 5000);
                            } else {
                                startHandler.sendEmptyMessageDelayed(TURN_TO_NEXT, 5000);
                            }
                        } else {
                            setResult();
                        }
                    } else {
                        setResult();
                    }
                    break;
                case SETRESULT:
                    setResult();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //3. 获取LayoutInflater,目的是方便后面得到xml布局文件进行装配
        LayoutInflater inflater = getLayoutInflater();

        //6. 获取main.xml layout对象,他是装配其他图片布局的中心点
        //   要记得,它里面声明了一个图片区域ViewPager,以及一个导航指示区域
        main = (View) inflater.inflate(R.layout.activity_ad_viewpager, null);

        initView();
        initData();
        startHandler.sendEmptyMessageDelayed(TURN_TO_NEXT, 3000);
    }

    /**
     * 初始化组件
     */
    private void initView() {
        // 实例化ArrayList对象
        views = new ArrayList<View>();
        // 实例化ViewPager
        viewPager = (ViewPager) main.findViewById(R.id.viewpager);

        //8. 通过main layout对象获取导航指示区域
        pointGroup = (ViewGroup) main.findViewById(R.id.pointGroup);
        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);
        img_skip = (ImageView) main.findViewById(R.id.img_skip);
        img_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
            }
        });
    }

    File[] files;

    /**
     * 初始化数据
     */
    private void initData() {
        // 定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        File root = new File(Str.ADIMAGEPATH_START);
        if (root.exists()) {
            files = root.listFiles();
            if (files == null || files.length == 0) {
                setResult();
                return;
            }
            for (File file : files) {
                if (!file.isDirectory()) {
//                System.out.println("显示"+filePath+"下所有子目录"+file.getAbsolutePath());
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    ImageView iv = new ImageView(this);
                    iv.setLayoutParams(mParams);
                    // 防止图片不能填满屏幕
                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                    // 加载图片资源
                    if (bitmap != null) {
                        iv.setImageBitmap(bitmap);
                    } else {
                        iv.setBackgroundResource(R.mipmap.img_start_client);
                    }
                    views.add(iv);
                }
            }
        } else {
            setResult();
        }
//        // 初始化引导图片列表
//        for (int i = 0; i < pics.length; i++) {
//            ImageView iv = new ImageView(this);
//            iv.setLayoutParams(mParams);
//            // 防止图片不能填满屏幕
//            iv.setScaleType(ImageView.ScaleType.FIT_XY);
//            // 加载图片资源
//            iv.setImageResource(pics[i]);
//            views.add(iv);
//        }
        // 初始化底部小点
        initPoint();
    }

    /**
     * 初始化底部小点
     */
    private void initPoint() {
//        points = new ImageView[pics.length];
        points = new ImageView[files.length];

        // 循环取得小点图片
//        for (int i = 0; i < pics.length; i++) {
        for (int i = 0; i < files.length; i++) {
            imageView = new ImageView(MyStartViewPagerActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
            lp.setMargins(20, 0, 20, 0);
            imageView.setLayoutParams(lp);
            // 得到一个LinearLayout下面的每一个子元素
            points[i] = imageView;
            if (i == 0) {
                points[i].setBackgroundResource(R.drawable.point_select);
            } else {
                points[i].setBackgroundResource(R.drawable.point_normal);
            }
            // 把每一个导航小圆点都加入到ViewGroup中
            pointGroup.addView(points[i]);
            setContentView(main);
            // 设置数据
            viewPager.setAdapter(vpAdapter);
            // 设置监听
            viewPager.setOnPageChangeListener(new PointChangeListener());
            // 默认都设为灰色
            points[i].setEnabled(true);
            // 给每个小点设置监听
            points[i].setOnClickListener(this);
            // 设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        // 设置当面默认的位置
        currentIndex = 0;
        // 设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    /**
     * 滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    /**
     * 当前页面滑动时调用
     */
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * 新的页面被选中时调用
     */
    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
//        if (arg0 == pics.length - 1) {
        if (arg0 == files.length - 1) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }

        setCurDot(arg0);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (startHandler != null) {
            startHandler.removeCallbacksAndMessages(null);
            startHandler = null;
        }
    }

    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position) {
//        if (position < 0 || position >= pics.length) {
        if (position < 0 || position >= files.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
//        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
        if (positon < 0 || positon > files.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }


    // 监听器内部类
    // 指引页面更改事件监听器
    class PointChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {

            points[arg0].setBackgroundResource(R.drawable.point_select);
            for (int i = 0; i < points.length; i++) {
                if (arg0 != i) {
                    points[i].setBackgroundResource(R.drawable.point_normal);
                }
            }
            // 简单测试:

            if (arg0 == points.length - 1) {
                Log.i(TAG, "onPageSelected: " + (points.length - 1));
                startHandler.sendEmptyMessageDelayed(SETRESULT, 5000);
//                Thread thread = new Thread();
//                try {
//                    thread.join(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                setResult();
            }
        }
    }

    public void setResult() {
        setResult(Str.REQUESTADCODE);
        finish();
    }
}
