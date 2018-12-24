package com.ccloomi.cdte.utils;

import java.io.File;

public class Paths {
	public static String get(String base,String...ps) {
		StringBuilder sb=new StringBuilder();
		sb.append(base);
		for(String s:ps) {
			if(sb.length()==0) {
				sb.append(s);
			}else if(sb.charAt(sb.length()-1)=='/') {
				if(s.charAt(0)=='/') {
					sb.append(new String(s.substring(1, s.length())));
				}else {
					sb.append(s);
				}
			}else {
				if(s.charAt(0)=='/') {
					sb.append(s);
				}else {
					sb.append('/').append(s);
				}
			}
		}
		return sb.toString();
	}
	public static String getBaseOnUserDir(String...ps) {
		return get(System.getProperty("user.dir"), ps);
	}
	public static File getFile(String base,String...ps) {
		return new File(get(base, ps));
	}
	public static File getUserDirFile(String...ps) {
		return new File(getBaseOnUserDir(ps));
	}
}
