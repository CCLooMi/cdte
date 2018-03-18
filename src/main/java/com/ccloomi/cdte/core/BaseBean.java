package com.ccloomi.cdte.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**© 2015-2018 Chenxj Copyright
 * 类    名：BaseBean
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月18日-下午7:18:28
 */
public abstract class BaseBean {
	private ObjectMapper om=new ObjectMapper();
	@Override
	public String toString() {
		try {
			return om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
