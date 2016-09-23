package com.example.drawerlayout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drawerlayout.model.ContentModel;
import com.example.drawerlayout.R;

import java.util.List;

/**
 * Created by CLF on 2016/8/2.
 */
public class ContentAdapter extends BaseAdapter {

    private Context context;
    private List<ContentModel> list;

    public ContentAdapter(Context context, List<ContentModel> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHold viewHold;

        if (view == null) {
            viewHold = new ViewHold();
            view = LayoutInflater.from(context).inflate(R.layout.content_item, null);
            view.setTag(viewHold);
        } else {
            viewHold = (ViewHold) view.getTag();
        }

        viewHold.imageView = (ImageView) view.findViewById(R.id.item_imageview);
        viewHold.textView = (TextView) view.findViewById(R.id.item_textview);

        viewHold.imageView.setImageResource(list.get(position).getImageView());
        viewHold.textView.setText(list.get(position).getText());
        return view;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    class ViewHold {
        public ImageView imageView;
        public TextView textView;
    }
}
