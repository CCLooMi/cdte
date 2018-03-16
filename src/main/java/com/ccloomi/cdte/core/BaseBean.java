package com.ccloomi.cdte.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**© 2015-2018 Chenxj Copyright
 * 类    名：BaseBean
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年1月27日-下午6:06:39
 */
@JsonInclude(Include.NON_NULL) 
public abstract class BaseBean {
	private ObjectMapper om=new ObjectMapper();
	public String toString(){
		try {
			return om.writeValueAsString(this);
		} catch (Exception e) {
			return "{}";
		}
	}
}
