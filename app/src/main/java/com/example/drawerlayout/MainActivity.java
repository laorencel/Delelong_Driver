package com.example.drawerlayout;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.drawerlayout.adapter.ContentAdapter;
import com.example.drawerlayout.fragment.NewsFragment;
import com.example.drawerlayout.fragment.PictureFragment;
import com.example.drawerlayout.fragment.SubscriptionFragment;
import com.example.drawerlayout.model.ContentModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private RelativeLayout rightLayout;
    private List<ContentModel> list;
    private ContentAdapter adapter;
    private ImageView leftMenu,rightMenu;
    private ListView listView;
    private FragmentManager frgmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        leftMenu = (ImageView)findViewById(R.id.left_menu);
        rightMenu = (ImageView)findViewById(R.id.right_menu);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        rightLayout = (RelativeLayout)findViewById(R.id.right);
        listView=(ListView)findViewById(R.id.left_listView);
        frgmentManager=getSupportFragmentManager();

        initData();

        adapter = new ContentAdapter(this,list);
        listView.setAdapter(adapter);
        leftMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FragmentTransaction fragmentTransaction = frgmentManager.beginTransaction();
                switch((int)id){
                    case 1:
                        fragmentTransaction.replace(R.id.content,new NewsFragment());
                        break;
                    case 2:
                        fragmentTransaction.replace(R.id.content,new SubscriptionFragment());
                        break;
                    case 3:
                        fragmentTransaction.replace(R.id.content,new PictureFragment());
                        break;
                    default:
                        break;
                }

                fragmentTransaction.commit();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        rightMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });

        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
    }

    private void initData(){

        list = new ArrayList<ContentModel>();

        list.add(new ContentModel(R.drawable.news,"新闻",1));
        list.add(new ContentModel(R.drawable.subscription,"订阅",2));
        list.add(new ContentModel(R.drawable.picture, "图片", 3));
        list.add(new ContentModel(R.drawable.video, "视频", 4));
        list.add(new ContentModel(R.drawable.followposter, "跟帖", 5));
        list.add(new ContentModel(R.drawable.vote, "投票", 6));
    }
}
