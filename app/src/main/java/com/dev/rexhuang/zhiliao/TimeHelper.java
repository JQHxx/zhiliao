package com.dev.rexhuang.zhiliao;

/**
 * *  created by RexHuang
 * *  on 2019/9/16
 */
public class TimeHelper {
    public static String ms2HMS(long _ms) {
        StringBuilder stringBuilder = new StringBuilder();
        _ms /= 1000;
        int hour = (int) (_ms / 3600);
        int mint = (int) ((_ms % 3600) / 60);
        int sed = (int) (_ms % 60);
        if (hour > 0) {
            String hourStr = String.valueOf(hour);
            if (hour < 10) {
                hourStr = "0" + hourStr;
            }
            stringBuilder.append(hourStr).append(":");
        }
        if (mint >= 0) {
            String mintStr = String.valueOf(mint);
            if (mint < 10) {
                mintStr = "0" + mintStr;
            }
            stringBuilder.append(mintStr).append(":");
        }
        if (sed >= 0) {
            String sedStr = String.valueOf(sed);
            if (sed < 10) {
                sedStr = "0" + sedStr;
            }
            stringBuilder.append(sedStr);
        }
        return stringBuilder.toString();
    }
}
