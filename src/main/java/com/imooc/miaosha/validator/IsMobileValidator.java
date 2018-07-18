package com.imooc.miaosha.validator;
import  javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.imooc.miaosha.util.ValidatorUtil;

/**
 * 自定义注解  约束
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

	private boolean required = false;

	/**
	 *
	 * @param constraintAnnotation  该注解的实体对象
	 */
	public void initialize(IsMobile constraintAnnotation) {
		required = constraintAnnotation.required();
	}

	/**
	 *
	 * @param value 标注注解的字段值
	 * @param context 约束的上下文
	 * @return  false 表示校验没有通过，会抛出绑定异常 BindException 给框架
	 */
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(required) {
			return ValidatorUtil.isMobile(value);
		}else {
			if(StringUtils.isEmpty(value)) {
				return true;
			}else {
				return ValidatorUtil.isMobile(value);
			}
		}
	}

}
