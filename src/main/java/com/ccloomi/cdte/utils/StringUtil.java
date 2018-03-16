package com.ccloomi.cdte.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ccloomi.cdte.core.Constant;

/**
 * 
 * @author Chenxj
 * 2017/04/15 23:01
 */
public class StringUtil implements Constant{

	public static List<String>split(String s,String regex,int limit){
		return split(s, Pattern.compile(regex),limit);
	}
	public static List<String>split(String s,String regex){
		return split(s, Pattern.compile(regex));
	}
	public static List<String>split(String s,Pattern pattern){
		List<String>result=new ArrayList<>();
		Matcher matcher=pattern.matcher(s);
		StringBuffer sb=new StringBuffer();
		while(matcher.find()){
			matcher.appendReplacement(sb, "");
			if(sb.length()>0){
				result.add(sb.toString());
			}
			result.add(matcher.group());
		}
		sb.delete(0, sb.length());
		matcher.appendTail(sb);
		if(sb.length()>0){
			result.add(sb.toString());
		}
		return result;
	}
	public static List<String>split(String s,Pattern pattern,int limit){
		List<String>result=new ArrayList<>();
		Matcher matcher=pattern.matcher(s);
		StringBuffer sb=new StringBuffer();
		int l=1;
		while(matcher.find()){
			if(l++<limit){
				matcher.appendReplacement(sb, "");
				if(sb.length()>0){
					result.add(sb.toString());
				}
				result.add(matcher.group());
			}
		}
		sb.delete(0, sb.length());
		matcher.appendTail(sb);
		if(sb.length()>0){
			result.add(sb.toString());
		}
		return result;
	}

	public static Map<String, byte[]> jsonString2map(String json){
		Map<String, byte[]>m=new HashMap<>();
		Matcher matcher=kvpattern.matcher(json);
		while(matcher.find()){
			String[]s=matcher.group().split(":");
			m.put(s[0], s[1].getBytes());
		}
		return m;
	}
	public static boolean startsWith(CharSequence sb,CharSequence prefix){
		return startsWith(sb, prefix, 0);
	}
	public static boolean endsWith(CharSequence sb,CharSequence prefix){
		return startsWith(sb, prefix, sb.length()-prefix.length());
	}
	private static boolean startsWith(CharSequence sb,CharSequence prefix, int toffset) {
        int to = toffset;
        int po = 0;
        int pc = prefix.length();
        if ((toffset < 0) || (toffset > sb.length() - pc)) {
            return false;
        }
        while (--pc >= 0) {
            if (sb.charAt(to++) != prefix.charAt(po++)) {
                return false;
            }
        }
        return true;
    }
	public static boolean emptyStr(CharSequence cs){
		Matcher mt=dispensable.matcher(cs);
		while(mt.find()){
			return false;
		}
		return true;
	}
	public static void main(String[] args) {
		String html="<p>Hi ${user.name} welcome.</p>";
		System.out.println(split(html, Constant.elpattern));
	}
	
}
