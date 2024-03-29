package cn.pipilu.plus.common.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    /**
     * 判断是否是正确的IP地址
     *
     * @param ip
     * @return boolean true,通过，false，没通过
     */
    public static boolean isIp(String ip) {
        if (null == ip || "".equals(ip))
            return false;
        String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        return ip.matches(regex);
    }

    /**
     * 判断是否是正确的邮箱地址
     *
     * @param email
     * @return boolean true,通过，false，没通过
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email))
            return false;
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }

    /**
     * 判断是否含有中文，仅适合中国汉字，不包括标点
     *
     * @param text
     * @return boolean true,通过，false，没通过
     */
    public static boolean isChinese(String text) {
        if (null == text || "".equals(text))
            return false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(text);
        return m.find();
    }

    /***
     * 中文当作3个字符
     * @param text
     * @return
     */
    public static int strLen(String text) {
        if (null == text || "".equals(text))
            return 0;
        int len = 0;
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                len++;
            }
        }
        return len * 2 + text.length();
    }

    /***
     * 字符串截取，1个中文当3个字符
     *
     * @param text
     * @param len
     */
    public static String subStr(String text, int len) {
        if (null == text || "".equals(text))
            return "";
        StringBuilder result = new StringBuilder();
        int index = 0;
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                index += 3;
            } else {
                index++;
            }
            if (len < index)
                break;
            result.append(c);
        }
        return result.toString();
    }

    /***
     * 字符串截取，1个中文当3个字符是否补省略号
     * @param text
     * @param len
     * @param ellipsis
     * @return
     */
    public static String subStr(String text, int len, boolean ellipsis) {
        if (null == text || "".equals(text))
            return "";
        if (ellipsis) {
            len = len - 3;
        }
        StringBuilder result = new StringBuilder();
        int index = 0;
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                index += 3;
            } else {
                index++;
            }
            if (len < index)
                break;
            result.append(c);
        }
        if (ellipsis) {
            result.append("...");
        }
        return result.toString();
    }

    /**
     * 判断是否正整数
     *
     * @param number 数字
     * @return boolean true,通过，false，没通过
     */
    public static boolean isNumber(String number) {
        if (null == number || "".equals(number))
            return false;
        String regex = "[0-9]*";
        return number.matches(regex);
    }

    /**
     * 判断几位小数(正数)
     *
     * @param decimal 数字
     * @param count   小数位数
     * @return boolean true,通过，false，没通过
     */
    public static boolean isDecimal(String decimal, int count) {
        if (null == decimal || "".equals(decimal))
            return false;
        String regex = "^(-)?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){" + count + "})?$";
        return decimal.matches(regex);
    }

    /**
     * 判断是否是手机号码
     *
     * @param phoneNumber 手机号码
     * @return boolean true,通过，false，没通过
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        if (null == phoneNumber || "".equals(phoneNumber))
            return false;
        String regex = "^1[3|4|7|5|8][0-9]\\d{8}$";
        return phoneNumber.matches(regex);
    }

    /**
     * 判断是否含有特殊字符
     *
     * @param text
     * @return boolean true,通过，false，没通过
     */
    public static boolean hasSpecialChar(String text) {
        if (null == text || "".equals(text))
            return false;
        if (text.replaceAll("[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0) {
            // 如果不包含特殊字符
            return false;
        }
        return true;
    }

    /**
     * 适应CJK（中日韩）字符集，部分中日韩的字是一样的
     */
    public static boolean isChinese2(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(strLen("我是中文123,"));
        System.out.println(subStr("我1是中文123,", 5));
    }
}