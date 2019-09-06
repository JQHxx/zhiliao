package com.dev.rexhuang.zhiliao_core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.orhanobut.logger.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * *  created by RexHuang
 * *  on 2019/8/7
 */
public class BitmapHelper {
    private static final String TAG = BitmapHelper.class.getSimpleName();
    private static final int MAX_READ_LIMIT_PER_IMG = 1024 * 1024;

    public static Bitmap scaleBitmap(Bitmap src, int maxWidth, int maxHeight) {
        double scaleFactor = Math.min(((double) maxWidth) / src.getWidth(),
                ((double) maxHeight) / src.getHeight());
        return Bitmap.createScaledBitmap(src, (int) (src.getWidth() * scaleFactor),
                (int) (src.getHeight() * scaleFactor), false);
    }

    public static Bitmap scaleBitmap(int scaleFactor, InputStream is) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeStream(is, null, bmOptions);
    }

    public static int findScaleFactor(int targetW, int targetH, InputStream is) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, bmOptions);
        int actualW = bmOptions.outWidth;
        int actualH = bmOptions.outHeight;

        return Math.min(actualW / targetW, actualH / targetH);
    }

    public static Bitmap fetchAndRescaleBitmap(String uri, int width, int height) throws IOException {
        URL url = new URL(uri);
        BufferedInputStream is = null;
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(urlConnection.getInputStream());
            is.mark(MAX_READ_LIMIT_PER_IMG);
            int scaleFactor = findScaleFactor(width, height, is);
            Logger.t(TAG).d("Scaling bitmap ", uri, " by factor ", scaleFactor, " to support ",
                    width, "x", height, "requested dimension");
            is.reset();
            return scaleBitmap(scaleFactor, is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}