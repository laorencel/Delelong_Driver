package com.delelong.diandiandriver.traver.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.base.adapter.BaseListAdapter;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.bean.TraverCustomerBean;
import com.delelong.diandiandriver.utils.ImageLoaderUtils;
import com.delelong.diandiandriver.utils.MyApp;

import java.math.BigDecimal;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/2/26.
 */

public class ExecutionCustomerListAdapter extends BaseListAdapter<TraverCustomerBean> {

    private OnChildItemClickListener childItemClickListener;

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_traver_customer, parent, false);
        return new TraverHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        final TraverCustomerBean bean = getData().get(position);

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
    public void onBind(RecyclerView.ViewHolder holder, int realPosition, TraverCustomerBean traverBean) {
        if (traverBean.getHeadPortrait() != null && !traverBean.getHeadPortrait().isEmpty()) {
            ImageLoaderUtils.display(MyApp.getInstance(), (((TraverHolder) holder).circle_head),
                    Str.URL_SERVICE_IMAGEPATH + traverBean.getHeadPortrait(), R.mipmap.img_head_menuinfo, R.mipmap.img_head_menuinfo);
        }
        if (traverBean.getNickName() != null) {
            ((TraverHolder) holder).tv_driver.setText(traverBean.getNickName());
        } else {
            ((TraverHolder) holder).tv_driver.setVisibility(View.GONE);
        }
        //是否拼车：0：包车；1：拼车；2：寄货
        if (traverBean.getPd() != null) {
            String pd = "";
            if (traverBean.getPd().compareTo(new BigDecimal(0)) == 0) {
                pd = "包车";
            } else if (traverBean.getPd().compareTo(new BigDecimal(1)) == 0) {
                pd = "拼车";
            } else if (traverBean.getPd().compareTo(new BigDecimal(2)) == 0) {
                pd = "寄货";
            }
            ((TraverHolder) holder).tv_pd.setText(pd);
        } else {
            ((TraverHolder) holder).tv_pd.setVisibility(View.GONE);
        }
        if (traverBean.getPeopleNum() != null) {
            ((TraverHolder) holder).tv_people.setText(traverBean.getPeopleNum() + " 人");
        } else {
            ((TraverHolder) holder).tv_people.setVisibility(View.GONE);
        }
        if (traverBean.getAmount() != null) {
            ((TraverHolder) holder).tv_amount.setText("￥ " + traverBean.getAmount());
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
