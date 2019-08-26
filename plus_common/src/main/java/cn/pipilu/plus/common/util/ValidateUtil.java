package cn.pipilu.plus.common.util;

import java.lang.reflect.Field;

import cn.pipilu.plus.common.exception.AppException;
import cn.pipilu.plus.common.response.ResultCode;
import cn.pipilu.plus.common.validate.RegexType;
import cn.pipilu.plus.common.validate.RegexUtils;
import cn.pipilu.plus.common.validate.Validate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
@Component
public class ValidateUtil {

    public  void validObject(Object object) throws AppException {
        if (object == null) {
            return;
        }
        Class<? extends Object> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 对于private私有化的成员变量，通过setAccessible来修改器访问权限
            field.setAccessible(true);
            validField(field, object);
            // 重新设置会私有权限
            field.setAccessible(false);
        }
    }

    private void validField(Field field, Object object) throws AppException {
        String desc;
        Object value = null;
        // 获取成员的注解信息
        Validate annotation = field.getAnnotation(Validate.class);
        try {
            value = field.get(object);
        } catch (Exception e) {
            throw new AppException(ResultCode.VALIDATE_ERROR.code, field.getName() + "验证错误");
        }
        if (annotation == null) {
            return;
        }
        desc = annotation.desc().equals("") ? field.getName() : annotation.desc();

        // 校验为空
        if (!annotation.nullable()) {
            if (value == null || (value instanceof String && StringUtils.isEmpty(value.toString()))) {
                throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "不能为空");
            }
        }
        if (value == null || StringUtils.isEmpty(value.toString())) {
            return;
        }

        // 校验最大长度
        if (value.toString().length() > annotation.maxLength() && annotation.maxLength() != 0) {
            throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "长度不能超过" + annotation.maxLength());
        }

        // 校验最小长度
        if (value.toString().length() < annotation.minLength() && annotation.minLength() != 0) {
            throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "长度不能小于" + annotation.minLength());
        }

        // 校验格式
        if (annotation.regexType() != RegexType.NONE) {
            switch (annotation.regexType()) {
                case NONE:
                    break;
                case SPECIALCHAR:
                    if (RegexUtils.hasSpecialChar(value.toString())) {
                        throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "不能含有特殊字符");
                    }
                    break;
                case CHINESE:
                    if (RegexUtils.isChinese2(value.toString())) {
                        throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "不能含有中文字符");
                    }
                    break;
                case EMAIL:
                    if (!RegexUtils.isEmail(value.toString())) {
                        throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "地址格式不正确");
                    }
                    break;
                case IP:
                    if (!RegexUtils.isIp(value.toString())) {
                        throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "地址格式不正确");
                    }
                    break;
                case NUMBER:
                    if (!RegexUtils.isNumber(value.toString())) {
                        throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "不是数字");
                    }
                    break;
                case PHONENUMBER:
                    if (!RegexUtils.isPhoneNumber(value.toString())) {
                        throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "非法的手机号");
                    }
                    break;
                default:
                    break;
            }
        }
        if (!annotation.regexExpression().equals("")) {
            if (!value.toString().matches(annotation.regexExpression())) {
                throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "格式不正确");
            }
        }

        if (!annotation.regexExpression().equals("")) {
            if (value.toString().matches(annotation.filterRegexExpression())) {
                throw new AppException(ResultCode.VALIDATE_ERROR.code, desc + "格式不正确：存在空白字符");
            }
        }


    }
}