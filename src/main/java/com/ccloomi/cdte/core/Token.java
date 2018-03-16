package com.ccloomi.cdte.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Token extends BaseBean implements Constant{
	private String name;
	private int type;
	private boolean selfClose=false;
	private boolean unclose=false;
	private String data;
	private Map<String, String>attrs=new HashMap<>();
	private Map<String, String>commands=new HashMap<>();
	private List<Token>childs=new LinkedList<>();
	
	private boolean isInclude=false;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name.toLowerCase();
		this.unclose=voidTags.contains(this.name);
		this.isInclude="include".equals(name);
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public boolean isSelfClose() {
		return selfClose;
	}
	public void setSelfClose(boolean selfClose) {
		this.selfClose = selfClose;
	}
	public boolean isUnclose() {
		return unclose;
	}
	public void setUnclose(boolean unclose) {
		this.unclose = unclose;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Map<String, String> getAttrs() {
		return attrs;
	}
	public void setAttrs(Map<String, String> attrs) {
		this.attrs = attrs;
	}
	public Map<String,String> getCommands(){
		return this.commands;
	}
	public List<Token> getChilds() {
		return childs;
	}
	public void setChilds(List<Token> childs) {
		this.childs = childs;
	}
	
	public boolean isInclude() {
		return isInclude;
	}
	public void setInclude(boolean isInclude) {
		this.isInclude = isInclude;
	}
	public Token addAttrs(String attrStr) {
		String[]s=attrStr.split("=",2);
		if(s.length<2){
			if(commandSet.contains(s[0])||s[0].startsWith(_cc_)) {
				this.commands.put(s[0], "");
			}else {
				this.attrs.put(s[0], "");
			}
		}else{
			int sl=s[1].length();
			if(s[1].charAt(0)=='"' && s[1].charAt(sl-1)=='"'){
				if(commandSet.contains(s[0])||s[0].startsWith(_cc_)) {
					this.commands.put(s[0], s[1].substring(1, s[1].length()-1));
				}else {
					this.attrs.put(s[0], s[1].substring(1, s[1].length()-1));
				}
			}else{
				if(commandSet.contains(s[0])||s[0].startsWith(_cc_)) {
					this.commands.put(s[0], s[1]);
				}else{
					this.attrs.put(s[0], s[1]);
				}
			}
		}
		return this;
	}
	public Token addChild(Token Token){
		this.childs.add(Token);
		return this;
	}
	public Token addChilds(Collection<Token>Tokens){
		this.childs.addAll(Tokens);
		return this;
	}
	public boolean hasChilds(){
		return !this.childs.isEmpty();
	}
	public boolean isText(){
		return this.data!=null;
	}
	public boolean hasCommands() {
		return !this.commands.isEmpty();
	}
	public boolean hasCommand(String command) {
		return this.commands.containsKey(command);
	}
	public String getCommand(String command) {
		return this.commands.get(command);
	}
	public void removeCommand(String command) {
		this.commands.remove(command);
	}
	public String getAttr(String attr) {
		return attrs.get(attr);
	}
}
