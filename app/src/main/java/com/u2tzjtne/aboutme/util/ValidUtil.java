package com.u2tzjtne.aboutme.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JK on 2017/12/2.
 */

public class ValidUtil {

    //邮箱格式校验
    public static boolean isEmailValid(String email) {
        String EMAIL_PATTERN = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //密码格式校验
    public static boolean isPasswordValid(String password) {
        //至少八个字符，至少一个字母和一个数字
        String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    //用户名格式校验
    public static boolean isNicknameValid(String nickname) {
        //只含有汉字、数字、字母、下划线不能以下划线开头和结尾
        String NICKNAME_PATTERN = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        Pattern pattern = Pattern.compile(NICKNAME_PATTERN);
        Matcher matcher = pattern.matcher(nickname);
        return nickname.length() > 4;
    }
}
