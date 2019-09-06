package com.dev.rexhuang.zhiliao_core.music_hall.banner;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.wenjian.loopbanner.LoopAdapter;

/**
 * *  created by RexHuang
 * *  on 2019/7/29
 */
public class BannerAdapter extends LoopAdapter<String> {
    @Override
    protected void onBindView(ViewHolder holder, String data, int position) {
        ImageView imageView = (ImageView) holder.itemView;
        Glide.with(holder.getContext())
                .load(data)
                .apply(new RequestOptions()
                        .transform(
                                new CenterCrop(), new RoundedCorners(20)
                        )).into(imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.getContext(), "position=$position", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
