package com.dev.rexhuang.zhiliao.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * *  created by RexHuang
 * *  on 2019/12/9
 */
public class BitmapUtils {
    /**
     * @param inSampleSize 图片像素的 1/n*n
     */
    public static Drawable createBlurredImageFromBitmap(Bitmap bitmap, int inSampleSize) {

        RenderScript rs = RenderScript.create(Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name()));
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);

        final Allocation input = Allocation.createFromBitmap(rs, blurTemplate);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(24f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);
        rs.destroy();

        return new BitmapDrawable(((Context) Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name())).getResources(), blurTemplate);
    }
}
