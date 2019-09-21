package com.dev.rexhuang.zhiliao_core.api;

import com.dev.rexhuang.zhiliao_core.entity.ZhiliaoEntity;

/**
 * *  created by RexHuang
 * *  on 2019/9/20
 */
public class ZhiliaoApiHelper {

    public static boolean isSuccess(ZhiliaoEntity entity) {
        if (entity.getCode() != 0) {
            return false;
        }
        return true;
    }
}
