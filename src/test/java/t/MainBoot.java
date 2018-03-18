package t;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**© 2015-2018 Chenxj Copyright
 * 类    名：MainBoot
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月17日-下午12:03:05
 */
@EnableAutoConfiguration
@ComponentScan({"t.config","t.controller"})
public class MainBoot {
	public static void main(String[] args) {
		SpringApplication.run(MainBoot.class, args);
	}
}
