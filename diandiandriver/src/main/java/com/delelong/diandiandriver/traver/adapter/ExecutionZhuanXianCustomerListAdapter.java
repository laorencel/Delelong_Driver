package com.delelong.diandiandriver.traver.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.base.adapter.BaseListAdapter;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.bean.ZhuanXianCustomerBean;
import com.delelong.diandiandriver.utils.ImageLoaderUtils;
import com.delelong.diandiandriver.utils.MyApp;

import java.math.BigDecimal;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/3/18.
 */

public class ExecutionZhuanXianCustomerListAdapter extends BaseListAdapter<ZhuanXianCustomerBean> {
    private OnChildItemClickListener childItemClickListener;

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_zhuanxian_customer, parent, false);
        return new TraverHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        final ZhuanXianCustomerBean bean = getData().get(position);
        if (childItemClickListener != null) {
            ((TraverHolder) holder).phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childItemClickListener.onChildItemClick(v, position, new BigDecimal(0), bean);
                }
            });
            ((TraverHolder) holder).tv_nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childItemClickListener.onChildItemClick(v, position, new BigDecimal(1), bean);
                }
            });
        }
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, int realPosition, ZhuanXianCustomerBean zhuanXianCustomerBean) {
        if (zhuanXianCustomerBean.getHeadPortrait() != null && !zhuanXianCustomerBean.getHeadPortrait().isEmpty()) {
            ImageLoaderUtils.display(MyApp.getInstance(), (((TraverHolder) holder).circle_head),
                    Str.URL_SERVICE_IMAGEPATH + zhuanXianCustomerBean.getHeadPortrait(), R.mipmap.img_head_menuinfo, R.mipmap.img_head_menuinfo);
        }
        if (zhuanXianCustomerBean.getName() != null) {
            ((TraverHolder) holder).tv_driver.setText(zhuanXianCustomerBean.getName());
        } else {
            ((TraverHolder) holder).tv_driver.setVisibility(View.GONE);
        }
        //是否拼车（true.拼车 false.不拼车）
        if (zhuanXianCustomerBean.getType() == false) {
            ((TraverHolder) holder).tv_pd.setText("包车");
        } else if (zhuanXianCustomerBean.getType() == true) {
            ((TraverHolder) holder).tv_pd.setText("拼车");
        }
        if (zhuanXianCustomerBean.getPeople() != null) {
            ((TraverHolder) holder).tv_people.setText(zhuanXianCustomerBean.getPeople() + " 人");
        } else {
            ((TraverHolder) holder).tv_people.setVisibility(View.GONE);
        }
        if (zhuanXianCustomerBean.getAmount() != null) {
            ((TraverHolder) holder).tv_amount.setText("￥ " + zhuanXianCustomerBean.getAmount());
        } else {
            ((TraverHolder) holder).tv_amount.setVisibility(View.GONE);
        }
    }

    class TraverHolder extends Holder {

        CircleImageView circle_head, phone;
        TextView tv_driver;
        TextView tv_pd, tv_people, tv_amount, tv_nav;

        public TraverHolder(View itemView) {
            super(itemView);
            circle_head = (CircleImageView) itemView.findViewById(R.id.circle_head);
            phone = (CircleImageView) itemView.findViewById(R.id.phone);
            tv_driver = (TextView) itemView.findViewById(R.id.tv_driver);
            tv_pd = (TextView) itemView.findViewById(R.id.tv_pd);
            tv_people = (TextView) itemView.findViewById(R.id.tv_people);
            tv_amount = (TextView) itemView.findViewById(R.id.tv_amount);
            tv_nav = (TextView) itemView.findViewById(R.id.tv_nav);
        }
    }

    public void setOnChildItemClickListener(OnChildItemClickListener childItemClickListener) {
        this.childItemClickListener = childItemClickListener;
    }

    public interface OnChildItemClickListener<Data> {
        /**
         * @param view
         * @param position
         * @param amountType 选择拼车还是包车或寄货
         */
        void onChildItemClick(View view, int position, BigDecimal amountType, Data bean);
    }
}
