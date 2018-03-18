package com.ccloomi.cdte;

import java.nio.charset.Charset;

/**© 2015-2018 Chenxj Copyright
 * 类    名：CDTEConfigure
 * 类 描 述：CDTE配置类
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月14日-下午10:35:50
 */
public class CDTEConfigure {
	public static Charset charset;
	public static String[] templateLoadPath;
	public static String suffix;
	public static String prefix;
	static {
		charset=Charset.forName("UTF-8");
		templateLoadPath="templates".split(",|;");
		suffix=".html";
	}
}
