package com.ccloomi.cdte.core;

import static com.ccloomi.cdte.CDTEConfigure.charset;
import static com.ccloomi.cdte.utils.StringUtil.jsonString2map;
import static com.ccloomi.cdte.utils.StringUtil.split;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.parser.FelNode;

/**© 2015-2018 Chenxj Copyright
 * 类    名：Vout
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年1月25日-下午7:25:58
 */
public class Vout implements Constant{
	private FelEngine fel=new FelEngineImpl();
	private VoutType type=VoutType.UNKNOWN;
	private FelNode condition;
	//else_if or else
	private Vout nextVout;
	private FelNode valueout;
	private FelNode loopout;
	private String attrName;
	private byte[] data;
	
	public String dt;//调试用 TODO
	public String cd;//调试用 TODO
	public String vo;//调试用 TODO
	public String lo;//调试用 TODO
	public String ad;//调试用 TODO
	
	private Map<String, byte[]>ccattrmap;
	private FelNode ccattrCondition;
	private List<Vout>vs;

	public void render(FelContext ctx,OutputStream out)throws Exception{
		switch (type) {
		case TEXT:
			out.write(data);
			break;
		case VALUE:
			out.write(String.valueOf(valueout.eval(ctx)).getBytes(charset));
			break;
		case FOR:case REPEAT:
			Object lo=loopout.eval(ctx);
			if(lo!=null) {
				int i=0;
				if(lo instanceof Collection) {
					for(Object o:(Collection<?>)lo) {
						ctx.set(attrName, o);
						ctx.set("index", i++);
						for(Vout v:vs) {
							v.render(ctx, out);
						}
					}
				}else if(lo.getClass().isArray()) {
					for(Object o:(Object[])lo) {
						ctx.set(attrName, o);
						ctx.set("index", i++);
						for(Vout v:vs) {
							v.render(ctx, out);
						}
					}
				}
			}
			break;
		case IF:case ELSE_IF:case ELSE:
			//ELSE 的conditon为null;
			if(condition==null||(boolean)condition.eval(ctx)) {
				if(vs!=null) {
					for(Vout v:vs) {
						v.render(ctx, out);
					}
				}
			}else {
				nextVout.render(ctx, out);
			}
			break;
		case CCATTR:
			String cd=String.valueOf(ccattrCondition.eval(ctx));
			if(ccattrmap.containsKey(cd)) {
				out.write(ccattrmap.get(cd));
			}else if(ccattrmap.containsKey("default")) {
				out.write(ccattrmap.get("default"));
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 描述：
	 * 作者：chenxj
	 * 日期：2018年3月12日 - 下午12:25:28
	 * @param type
	 * @param str
	 * @return
	 */
	public Vout addType(VoutType type,String str) {
		this.type=type;
		switch (type) {
		case TEXT:
			this.data=str.getBytes(charset);
			this.dt=str;//TODO
			break;
		case VALUE:
			valueout=fel.parse(str.substring(2, str.length()-1));
			vo=str;//TODO
			break;
		case FOR:case REPEAT:
			String[]ss=str.split(":", 2);
			attrName=ss[0];
			loopout=fel.parse(ss[1]);
			lo=ss[1];//TODO
			break;
		case IF:case ELSE_IF:case ELSE:
			condition=fel.parse(str);
			cd=str;//TODO
			break;
		case CCATTR:
			if(ccpattern.matcher(str).matches()) {
				List<String>ls=split(str, ccpattern_left, 2);
				ccattrmap=jsonString2map(ls.get(0));
				ccattrCondition=fel.parse(ls.get(1));
				ad=ls.get(1);//TODO
			}
			break;
		default:
			break;
		}
		return this;
	}
	public VoutType getType() {
		return type;
	}

	public void setType(VoutType type) {
		this.type = type;
	}

	public FelNode getCondition() {
		return condition;
	}

	public void setCondition(FelNode condition) {
		this.condition = condition;
	}

	public Vout getNextVout() {
		return nextVout;
	}
	
	public Vout setNextVout(Vout vout) {
		//例子 1 2 3 从1开始遍历2->1,3->1->2;
		if(this.nextVout==null) {
			this.nextVout=vout;
		}else {
			this.nextVout.setNextVout(vout);
		}
		return this;
	}

	public FelNode getValueout() {
		return valueout;
	}

	public void setValueout(FelNode valueout) {
		this.valueout = valueout;
	}

	public FelNode getLoopout() {
		return loopout;
	}

	public void setLoopout(FelNode loopout) {
		this.loopout = loopout;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public Map<String, byte[]> getCcattrmap() {
		return ccattrmap;
	}

	public void setCcattrmap(Map<String, byte[]> ccattrmap) {
		this.ccattrmap = ccattrmap;
	}

	public FelNode getCcattrCondition() {
		return ccattrCondition;
	}

	public void setCcattrCondition(FelNode ccattrCondition) {
		this.ccattrCondition = ccattrCondition;
	}

	public List<Vout> getVs() {
		return vs;
	}

	public void setVs(List<Vout> vs) {
		this.vs = vs;
	}
}
