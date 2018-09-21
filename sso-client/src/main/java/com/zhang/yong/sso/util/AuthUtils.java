package com.zhang.yong.sso.util;

import java.util.regex.Pattern;

public class AuthUtils {
    public static boolean checkExclude(String requestUrl,Pattern[] excludePattern) {
        boolean flag = false;
        if (null == excludePattern || excludePattern.length == 0) {
            return flag;
        }
        for (Pattern pat : excludePattern) {
            if (pat.matcher(requestUrl).matches()){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
