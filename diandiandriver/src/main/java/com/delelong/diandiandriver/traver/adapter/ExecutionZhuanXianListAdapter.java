package com.delelong.diandiandriver.traver.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.base.adapter.BaseListAdapter;
import com.delelong.diandiandriver.traver.bean.ExecutionZhuanXianBean;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/2/26.
 */

public class ExecutionZhuanXianListAdapter extends BaseListAdapter<ExecutionZhuanXianBean> {

    private OnChildItemClickListener childItemClickListener;

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_execution_zhuanxian_, parent, false);
        return new TraverHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        final ExecutionZhuanXianBean bean = getData().get(position);

        if (childItemClickListener != null) {
            ((TraverHolder) holder).btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childItemClickListener.onChildItemClick(v, position, new BigDecimal(0), bean);
                }
            });
            ((TraverHolder) holder).ly_nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childItemClickListener.onChildItemClick(v, position, new BigDecimal(0), bean);
                }
            });
            ((TraverHolder) holder).ly_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childItemClickListener.onChildItemClick(v, position, new BigDecimal(0), bean);
                }
            });
            ((TraverHolder) holder).ly_end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childItemClickListener.onChildItemClick(v, position, new BigDecimal(0), bean);
                }
            });
            ((TraverHolder) holder).ly_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childItemClickListener.onChildItemClick(v, position, new BigDecimal(0), bean);
                }
            });
        }
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, int realPosition, ExecutionZhuanXianBean traverBean) {

        ((TraverHolder) holder).tv_start_address
                .setText(traverBean.getStartAddress());
        ((TraverHolder) holder).tv_end_address
                .setText(traverBean.getEndAddress());
        if (traverBean.getRequiredTime() != null && !traverBean.getRequiredTime().isEmpty()) {
            ((TraverHolder) holder).tv_time
                    .setText(Html.fromHtml("<small>全程需要</small>" + "<font color='#Fe8a03'>" + traverBean.getRequiredTime() + "</font>"));
        } else {
            ((TraverHolder) holder).tv_time.setVisibility(View.INVISIBLE);
        }

        if (traverBean.getLineName() != null) {
            ((TraverHolder) holder).tv_lineName.setText(traverBean.getLineName().split("专线")[0]);
        } else {
            ((TraverHolder) holder).tv_lineName.setVisibility(View.INVISIBLE);
        }
//        if (traverBean.getCount() != null) {
//            ((TraverHolder) holder).tv_memberCount
//                    .setText(Html.fromHtml("已接 <font color='#Fe8a03'><big>" + traverBean.getCount() + "</big></font> 人"));
//        } else {
//            ((TraverHolder) holder).tv_memberCount.setVisibility(View.INVISIBLE);
//        }

        //订单状态(1.已接单 0.未接单)
        if (traverBean.getOrderStatus() != null
                && traverBean.getOrderStatus().compareTo(new BigDecimal(0)) == 0) {
            ((TraverHolder) holder).btn_cancel.setEnabled(true);
            //人数暂未设置，给接单状态使用
            ((TraverHolder) holder).tv_memberCount
                    .setText(Html.fromHtml("未接单"));
        } else {
            ((TraverHolder) holder).btn_cancel.setEnabled(false);
            ((TraverHolder) holder).tv_memberCount
                    .setText(Html.fromHtml("已接单"));
        }
        if (traverBean.getPinCheAmount() != null && !traverBean.getPinCheAmount().equals("") && !traverBean.getPinCheAmount().equals("0")) {
            ((TraverHolder) holder).amount_pinChe
                    .setText(Html.fromHtml(traverBean.getPinCheAmount() + " 元"));
        } else {
            ((TraverHolder) holder).ly_pinChe.setVisibility(View.GONE);
        }
        if (traverBean.getBaoCheAmount() != null && !traverBean.getBaoCheAmount().equals("") && !traverBean.getBaoCheAmount().equals("0")) {
            ((TraverHolder) holder).amount_baoChe
                    .setText(Html.fromHtml(traverBean.getBaoCheAmount() + " 元"));
        } else {
            ((TraverHolder) holder).ly_baoChe.setVisibility(View.GONE);
        }
        if (traverBean.getDistance() != null && traverBean.getDistance().compareTo(new BigDecimal(0)) != 0) {
            ((TraverHolder) holder).tv_distance
                    .setText(Html.fromHtml("<small>约</small>" + " <font color='#Fe8a03'>" + traverBean.getDistance() + "</font> km"));
        } else {
            ((TraverHolder) holder).ly_distance.setVisibility(View.GONE);
        }
    }

    class TraverHolder extends Holder {

        LinearLayout ly_content;
        TextView tv_time, tv_distance, tv_lineName, tv_memberCount;
        ImageButton btn_cancel;
        TextView tv_start_address, tv_end_address;
        LinearLayout ly_pinChe, ly_baoChe, ly_distance;
        TextView amount_pinChe, amount_baoChe;
        LinearLayout ly_nav, ly_start, ly_end;

        public TraverHolder(View itemView) {
            super(itemView);
            ly_content = (LinearLayout) itemView.findViewById(R.id.ly_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_lineName = (TextView) itemView.findViewById(R.id.tv_lineName);
            tv_memberCount = (TextView) itemView.findViewById(R.id.tv_memberCount);
            btn_cancel = (ImageButton) itemView.findViewById(R.id.btn_cancel);
            tv_start_address = (TextView) itemView.findViewById(R.id.tv_start_address);
            tv_end_address = (TextView) itemView.findViewById(R.id.tv_end_address);
            ly_pinChe = (LinearLayout) itemView.findViewById(R.id.ly_pinChe);
            ly_baoChe = (LinearLayout) itemView.findViewById(R.id.ly_baoChe);
            ly_distance = (LinearLayout) itemView.findViewById(R.id.ly_distance);
            amount_pinChe = (TextView) itemView.findViewById(R.id.amount_pinChe);
            amount_baoChe = (TextView) itemView.findViewById(R.id.amount_baoChe);
            tv_distance = (TextView) itemView.findViewById(R.id.tv_distance);
            ly_nav = (LinearLayout) itemView.findViewById(R.id.ly_nav);
            ly_start = (LinearLayout) itemView.findViewById(R.id.ly_start);
            ly_end = (LinearLayout) itemView.findViewById(R.id.ly_end);
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
