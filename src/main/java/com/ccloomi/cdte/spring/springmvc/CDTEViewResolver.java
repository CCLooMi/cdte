package com.ccloomi.cdte.spring.springmvc;
import static com.ccloomi.cdte.CDTEConfigure.*;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

/**© 2015-2018 Chenxj Copyright
 * 类    名：CDTEViewResolver
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月18日-下午3:39:44
 */
public class CDTEViewResolver extends AbstractTemplateViewResolver{
	public CDTEViewResolver() {
		setPrefix(prefix);
		setSuffix(suffix);
		setViewClass(requiredViewClass());
	}
	@Override
	protected Class<?> requiredViewClass() {
		return CDTEView.class;
	}
}
