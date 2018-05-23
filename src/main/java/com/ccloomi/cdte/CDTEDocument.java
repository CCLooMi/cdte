package com.ccloomi.cdte;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccloomi.cdte.core.Token;

/**© 2015-2018 Chenxj Copyright
 * 类    名：CDTEDocument
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月14日-下午10:41:58
 */
public class CDTEDocument {
	private Logger log=LoggerFactory.getLogger(getClass());
	//源tokens
	private Stack<Token>oTokens;
	private Stack<Token>tokens;
	//依赖的snippet列表
	private Set<String>refSet;
	private String filePath;
	/**是否是代码片段,根据文件名是否包含snippet进行判断*/
	private boolean isSnippet=false;
	public CDTEDocument(Stack<Token>tokens,String filePath) {
		this.oTokens=tokens;
		this.tokens=new Stack<>();
		this.refSet=new HashSet<>();
		this.filePath=filePath;
		if(filePath.contains("snippet")) {
			this.isSnippet=true;
		}
		for(Token token:oTokens) {
			if(token.isInclude()) {
				this.refSet.add(token.getAttr("src"));
			}
		}
	}
	public boolean belongDir(String dir) {
		return filePath.startsWith(dir);
	}
	public boolean relySnippet(String snippet) {
		return this.refSet.contains(snippet);
	}
	public CDTEDocument repareRefAndToTree(Map<String, CDTEDocument>cdtedocMap) {
		if(hasRef()) {
			this.tokens.clear();
			for(Token token:oTokens) {
				if(token.isInclude()) {
					CDTEDocument d=cdtedocMap.get(token.getAttr("src"));
					if(d!=null) {
						tokens.addAll(d.getoTokens());
					}else {
						log.warn("include file [{}] missing.", token.getAttr("src"));
					}
				}else {
					tokens.add(token);
				}
			}
		}else {
			this.tokens.addAll(getoTokens());
		}
		return this.toTree();
	}
	private CDTEDocument toTree() {
		for(Token token:tokens) {
			token.reset();
		}
		Stack<Token> ts=new Stack<>();
		for(Token token:tokens){
			if(token.getType()<0){
				Stack<Token>temps=new Stack<>();
				Token t=ts.pop();
				while(!token.getName().equals(t.getName())){
					temps.add(t);
					t=ts.pop();
				}
				//需要反转列表
				Collections.reverse(temps);
				t.addChilds(temps);
				ts.add(t);
			}else if(token.getType()!=3){
				ts.push(token);
			}
		}
		tokens.clear();
		tokens=ts;
		return this;
	}
	public Stack<Token> getoTokens() {
		return oTokens;
	}
	public void setoTokens(Stack<Token> oTokens) {
		this.oTokens = oTokens;
	}
	public Stack<Token> getTokens() {
		return tokens;
	}
	public void setTokens(Stack<Token> tokens) {
		this.tokens = tokens;
	}
	public boolean hasRef() {
		return !refSet.isEmpty();
	}
	public String getFilePath() {
		return filePath;
	}
	public boolean isSnippet() {
		return isSnippet;
	}
}
