package t.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.ccloomi.cdte.spring.springboot.CDTEAutoConfiguration;

/**© 2015-2018 Chenxj Copyright
 * 类    名：SpringBootConfiguration
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月18日-下午3:14:06
 */
@Configuration
@Import({CDTEAutoConfiguration.class})
public class SpringBootConfiguration {
	
}
