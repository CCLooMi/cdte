package t.controller;

import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**© 2015-2018 Chenxj Copyright
 * 类    名：TestController
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月18日-下午3:47:20
 */
@Controller
public class TestController {
	@RequestMapping("/")
	public ModelAndView test() {
		ModelAndView mv=new ModelAndView("test");
		mv.addObject("title", "CDTE TEST!!!");
		mv.addObject("hello", "Hello CDTE.");
		mv.addObject("users", Arrays.asList("Seemie","Tommy"));
		return mv;
	}
}
