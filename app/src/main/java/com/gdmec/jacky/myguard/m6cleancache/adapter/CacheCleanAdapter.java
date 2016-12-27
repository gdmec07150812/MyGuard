package com.gdmec.jacky.myguard.m6cleancache.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdmec.jacky.myguard.R;
import com.gdmec.jacky.myguard.m6cleancache.entity.CacheInfo;

import java.util.List;

public class CacheCleanAdapter extends BaseAdapter {
    private Context context;
    private List<CacheInfo> cacheInfos;

    public CacheCleanAdapter(Context context, List<CacheInfo> cacheInfos) {
        super();
        this.context = context;
        this.cacheInfos = cacheInfos;
    }

    @Override
    public int getCount() {
        return cacheInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return cacheInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder hodlder = null;
        if (convertView == null) {
            hodlder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_cacheclean_list, null);
            hodlder.mAppIconImgv = (ImageView) convertView.findViewById(R.id.imgv_appicon_cacheclean);
            hodlder.mAppNameTV = (TextView) convertView.findViewById(R.id.tv_appname_cacheclean);
            hodlder.mCacheSizeTV = (TextView) convertView.findViewById(R.id.tv_appsize_cacheclean);
            convertView.setTag(hodlder);
        } else {
            hodlder = (ViewHolder) convertView.getTag();
        }
        CacheInfo cacheInfo = cacheInfos.get(position);
        hodlder.mAppIconImgv.setImageDrawable(cacheInfo.appIcon);
        hodlder.mAppNameTV.setText(cacheInfo.appName);
        hodlder.mCacheSizeTV.setText(Formatter.formatFileSize(context, cacheInfo.cacheSize));
        return convertView;
    }

    static class ViewHolder {
        ImageView mAppIconImgv;
        TextView mAppNameTV;
        TextView mCacheSizeTV;
    }
}
