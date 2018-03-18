package com.ccloomi.cdte;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.ccloomi.cdte.core.Vout;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.context.MapContext;

/**© 2015-2018 Chenxj Copyright
 * 类    名：CDTemplate
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月14日-下午10:05:04
 */
public class CDTemplate {
	private List<Vout>voutList;
	public void render(Map<String, Object> model,OutputStream out) {
		render(new MapContext(model),out);
	}

	public void render(FelContext ctx, OutputStream out) {
		//TODO
		System.out.println(voutList);
		try {
			for (Vout v : voutList) {
				v.render(ctx, out);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public List<Vout> getVoutList() {
		return voutList;
	}
	public void setVoutList(List<Vout> voutList) {
		this.voutList = voutList;
	}
	
}
