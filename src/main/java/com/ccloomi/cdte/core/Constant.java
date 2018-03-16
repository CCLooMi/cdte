package com.ccloomi.cdte.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**@类名 CCTEConstant
 * @说明 
 * @作者 Chenxj
 * @邮箱 chenios@foxmail.com
 * @日期 2017年4月6日-下午2:23:08
 */
public interface Constant {
	public static final String _if_="if";
	public static final String _else_if_="else_if";
	public static final String _else_="else";
	public static final String _for_="for";
	public static final String _repeat_="repeat";
	public static final String _cc_="cc_";
	/**匹配值输出*/
	public static final Pattern elpattern=Pattern.compile("\\$\\{.+?\\}");
	/**匹配cc_判断多功能赋值代码<br>{([^"{},]+:[^"{},]+,?)+}\([^"]+\)*/
	public static final Pattern ccpattern=Pattern.compile("\\{([^\"{},]+:[^\"{},]+,?)+\\}\\([^\"]+\\)");
	public static final Pattern ccpattern_left=Pattern.compile("\\{[^\"{}]+\\}");
	/**匹配k:v*/
	public static final Pattern kvpattern=Pattern.compile("([^\"{},]+:[^\"{},]+)");
	/**可有可无的字符*/
	public static final Pattern dispensable=Pattern.compile("[^\n\t ]+");;
	public static final Set<String>voidTags=new HashSet<>(Arrays
			.asList("!doctype","frame","device","br","hr","img","input","link","meta",
			"area","base","col","command","embed","keygen","param","source","track","wbr"));
	public static final Set<String>srcTags=new HashSet<>(Arrays
			.asList("pre","style","script","textarea","plaintext"));
	public static final Set<String>commandSet=new HashSet<>(Arrays
			.asList("for","repeat","if","else_if","else"));
}
