/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.smallvideo.maiguo.aliyun.media;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.smallvideo.maiguo.R;

import java.io.File;

/**
 * Created by Administrator on 2016/5/18.
 */
public class GalleryItemViewHolder extends RecyclerView.ViewHolder {

    //右上角小圆圈
    private ImageView rightTopCircle;
    //视频缩略图
    private ImageView thumbImage;
    //视频时长
    private TextView duration;
    //视频时长布局
    private View durationLayoput;
    private ThumbnailGenerator thumbnailGenerator;
    //当前选择的图片所在位置
    private int mSelectIndex = -1;


    public GalleryItemViewHolder(View itemView, ThumbnailGenerator thumbnailGenerator) {
        super(itemView);

        this.thumbnailGenerator = thumbnailGenerator;
        //右上角是否选择小圆圈
        rightTopCircle = (ImageView)itemView.findViewById(R.id.iv_right_top_circle);
        //视频缩略图
        thumbImage = (ImageView) itemView.findViewById(R.id.draft_thumbnail);
        //视频时长
        duration = (TextView) itemView.findViewById(R.id.draft_duration);
        //视频时长布局
        durationLayoput = itemView.findViewById(R.id.duration_layoput);

        itemView.setTag(this);
    }

    public void setData(final MediaInfo info,boolean actived){
        if(actived){
            rightTopCircle.setBackgroundResource(R.mipmap.video_right_top_sel);
        }else{
            rightTopCircle.setBackgroundResource(R.mipmap.video_right_top_nor);
        }
        if(info == null){
            return;
        }
        //有缩略图显示，没有就显示默认图
        if(info.thumbnailPath != null && onCheckFileExsitence(info.thumbnailPath)) {
            String uri = "file://" + info.thumbnailPath;
            Glide.with(thumbImage.getContext()).load(uri).into(thumbImage);
        }else {
            thumbImage.setImageDrawable(new ColorDrawable(Color.GRAY));
            thumbnailGenerator.generateThumbnail(info.type, info.id,0,
                    new ThumbnailGenerator.OnThumbnailGenerateListener() {
                        @Override
                        public void onThumbnailGenerate(int key, Bitmap thumbnail) {
                            int currentKey = ThumbnailGenerator.generateKey(info.type, info.id);
                            if(key == currentKey){
                                thumbImage.setImageBitmap(thumbnail);
                            }
                        }
                    });
        }
        //当时长大于0时，显示时长
        int du = info.duration;
        if(du == 0){
            durationLayoput.setVisibility(View.GONE);
        }else{
            durationLayoput.setVisibility(View.VISIBLE);
            onMetaDataUpdate(duration, du);
        }

    }

    public void onBind(MediaInfo info, boolean actived){
        setData(info,actived);
        itemView.setActivated(actived);
    }

    private boolean onCheckFileExsitence(String path) {
        Boolean res = false;
        if(path == null) {
            return res;
        }

        File file = new File(path);
        if(file.exists()) {
            res = true;
        }

        return res;
    }


    private void onMetaDataUpdate(TextView view, int duration) {
        if (duration == 0) {
            return;
        }

        int sec = Math.round((float) duration / 1000);
        int min = sec / 60;
        sec %= 60;

        view.setText(String.format(String.format("%d:%02d", min, sec)));
    }

}
