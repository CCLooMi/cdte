package com.ccloomi.cdte;
import static com.ccloomi.cdte.utils.StringUtil.emptyStr;
import static com.ccloomi.cdte.utils.StringUtil.endsWith;
import static com.ccloomi.cdte.utils.StringUtil.startsWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Matcher;

import com.ccloomi.cdte.core.Constant;
import com.ccloomi.cdte.core.Token;
import com.ccloomi.cdte.core.Vout;
import com.ccloomi.cdte.core.VoutType;

/**© 2015-2018 Chenxj Copyright
 * 类    名：CDTEParser
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月14日-下午9:35:04
 */
public class CDTEParser implements Constant{
	
	private List<Vout> tokensToVoutList(Collection<Token>tokens){
		Token[]ts=new Token[tokens.size()];
		tokens.toArray(ts);
		return tokensToVoutList(ts);
	}
	private List<Vout> tokensToVoutList(Token...tokens){
		StringBuilder sb=new StringBuilder();
		List<Vout>ls=new ArrayList<>();
		tokensToVoutList(sb,ls,tokens);
		if(sb.length()>0) {
			ls.add(new Vout().addType(VoutType.TEXT, sb.toString()));
		}
		sb.delete(0, sb.length());
		return ls;
	}
	private void tokensToVoutList(StringBuilder sb,List<Vout>ls,Collection<Token>tokens){
		Token[]ts=new Token[tokens.size()];
		tokens.toArray(ts);
		tokensToVoutList(sb, ls, ts);
	}
	private void tokensToVoutList(StringBuilder sb,List<Vout>ls,Token...tokens){
		for(Token token:tokens){
			if(token.isText()){
				//sb.append(token.getData());
				processOutValue(token.getData(), sb, ls);
			}else if(token.hasCommands()) {
				Vout vo=new Vout();
				if(token.hasCommand(_if_)) {
					vo.addType(VoutType.IF, token.getCommand(_if_));
					token.removeCommand(_if_);
					vo.setVs(tokensToVoutList(token));
					ls.add(vo);
				}else if(token.hasCommand(_else_if_)) {
					if(ls.size()>0
							&&(ls.get(ls.size()-1).getType()==VoutType.IF
							||ls.get(ls.size()-1).getType()==VoutType.ELSE_IF)) {
						vo.addType(VoutType.ELSE_IF, token.getCommand(_else_if_));
						token.removeCommand(_else_if_);
						vo.setVs(tokensToVoutList(token));
						ls.get(ls.size()-1).setNextVout(vo);
					}
				}else if(token.hasCommand(_else_)) {
					if(ls.size()>0
							&&(ls.get(ls.size()-1).getType()==VoutType.IF
							||ls.get(ls.size()-1).getType()==VoutType.ELSE_IF)) {
						vo.addType(VoutType.ELSE, token.getCommand(_else_));
						token.removeCommand(_else_);
						vo.setVs(tokensToVoutList(token));
						ls.get(ls.size()-1).setNextVout(vo);
					}
				}else if(token.hasCommand(_repeat_)) {
					vo.addType(VoutType.REPEAT, token.getCommand(_repeat_));
					token.removeCommand(_repeat_);
					vo.setVs(tokensToVoutList(token));
					ls.add(vo);
				}else {
					if(token.hasCommand(_for_)) {
						vo.addType(VoutType.FOR, token.getCommand(_for_));
						token.removeCommand(_for_);
						
						//处理for的token中所有属性输出
						sb.append('<')
						.append(token.getName());
						attrsToString(token.getAttrs(), sb, ls);
						//处理cc_*输出
						if(token.hasCommands()) {
							ccAttrsToString(token.getCommands(), sb, ls);
						}
						if(token.isSelfClose()){
							sb.append('/');
						}
						sb.append('>');
						
						ls.add(new Vout().addType(VoutType.TEXT, sb.toString()));
						sb.delete(0, sb.length());
						
						ls.add(vo);

						if(!token.isUnclose()){
							if(token.hasChilds()){
								vo.setVs(tokensToVoutList(token.getChilds()));
							}
							if(!token.isSelfClose()){
								sb.append('<').append('/')
								.append(token.getName())
								.append('>');
							}
						}
					}else {
						sb
						.append('<')
						.append(token.getName());
						attrsToString(token.getAttrs(),sb,ls);
						if(token.hasCommands()) {//_cc_*
							ccAttrsToString(token.getCommands(), sb, ls);
						}
						if(token.isSelfClose()){
							sb.append('/');
						}
						sb.append('>');
						if(!token.isUnclose()){
							if(token.hasChilds()){
								tokensToVoutList(sb,ls,token.getChilds());
							}
							if(!token.isSelfClose()){
								sb.append('<').append('/')
								.append(token.getName())
								.append('>');
							}
						}
					}
				}
			}else {
				sb
				.append('<')
				.append(token.getName());
				attrsToString(token.getAttrs(),sb,ls);
				if(token.isSelfClose()){
					sb.append('/');
				}
				sb.append('>');
				if(!token.isUnclose()){
					if(token.hasChilds()){
						tokensToVoutList(sb,ls,token.getChilds());
					}
					if(!token.isSelfClose()){
						sb.append('<').append('/')
						.append(token.getName())
						.append('>');
					}
				}
			}
		}
	}
	private void ccAttrsToString(Map<String, String>ccAttrs,StringBuilder sb,List<Vout>ls) {
		for(Entry<String, String>entry:ccAttrs.entrySet()) {
			sb.append(' ')
			.append(entry.getKey().replaceFirst("cc_", ""))
			.append('=').append('"');

			ls.add(new Vout().addType(VoutType.TEXT, sb.toString()));
			sb.delete(0, sb.length());
			
			Vout vo=new Vout();
			vo.addType(VoutType.CCATTR, entry.getValue());
			ls.add(vo);
			
			sb.append('"');
		}
		ccAttrs.clear();
	}
	private void attrsToString(Map<String, String>attrs,StringBuilder sb,List<Vout>ls){
		StringBuilder sbb=new StringBuilder();
		for(Entry<String, String>entry:attrs.entrySet()){
			sbb
			.append(' ')
			.append(entry.getKey());
			if(!"".equals(entry.getValue())){
				sbb
				.append('=')
				.append('"')
				.append(entry.getValue())
				.append('"');
			}
		}
		processOutValue(sbb.toString(), sb, ls);
	}

	private void processOutValue(String s,StringBuilder sb,List<Vout>ls){
		Matcher matcher=elpattern.matcher(s);
		StringBuffer bs=new StringBuffer();
		while(matcher.find()){
			matcher.appendReplacement(bs, "");
			if(bs.length()>0){
				sb.append(bs);
			}
			ls.add(new Vout().addType(VoutType.TEXT, sb.toString()));
			sb.delete(0, sb.length());
			
			ls.add(new Vout().addType(VoutType.VALUE, matcher.group()));
		}
		bs.delete(0, bs.length());
		matcher.appendTail(bs);
		if(bs.length()>0){
			sb.append(bs);
		}
	}
	public CDTemplate parser(CDTEDocument doc,Map<String, CDTEDocument>cdtedocMap) {
		//process include and toTree
		doc.repareRefAndToTree(cdtedocMap);
		CDTemplate template=new CDTemplate();
		template.setVoutList(tokensToVoutList(doc.getTokens()));
		//to save memories
		doc.getTokens().clear();
		return template;
	}
	public Stack<Token>parser(String html){
		return parser(html.toCharArray());
	}
	public Stack<Token>parser(char[]html){

		Stack<Token>sk=new Stack<>();

		StringBuilder sb=new StringBuilder();
		boolean stringFlag=false;
		boolean attrFlag=false;
		boolean commFlag=false;
		boolean srcFlag=false;
		char refChar=0;
		Token refToken=new Token();
		
		for(int i=0;i<html.length;i++){
			char c=html[i];
			if(!commFlag){
				if(!stringFlag){
					if(c=='<'){
						if(sb.length()!=0){
							
							if(srcFlag||!emptyStr(sb)){
								Token t=new Token();
								t.setType(0);//"TEXT";
								t.setData(sb.toString());
								
								if(!sk.isEmpty()){
									sk.peek().addChild(t);
								}
							}
							sb.delete(0, sb.length());
							sb.append(c);
							continue;
						}
					}else if(c=='>'){
						sb.append(c);
						
						Token t=stringToToken(sb.toString());
						sk.add(t);
						sb.delete(0, sb.length());
						attrFlag=false;
						
						if(t.getType()>0&&srcTags.contains(t.getName())){
							srcFlag=true;
						}else if(t.getType()<0 && t.getName().equals(refToken.getName())){
							srcFlag=false;
						}
						refToken=t;
						
						continue;
					}else if(c=='='){
						attrFlag=true;
					}
				}
				if(c=='\\'){
					sb.append(html[++i]);
					continue;
				}else if((c=='"'||c=='\'')&&attrFlag){
					if(refChar!=c&&!stringFlag){
						stringFlag=true;
						refChar=c;
					}else if(refChar==c){
						stringFlag=false;
						refChar=0;
					}
				}
			}
			sb.append(c);
			if(!commFlag && startsWith(sb,"<!--")){
				commFlag=true;
			}else if(commFlag && endsWith(sb,"-->")){
				Token t=new Token();
				t.setType(3);//"COMM";
				t.setData(sb.substring(4, sb.length()-3));
				sk.add(t);
				sb.delete(0, sb.length());
				commFlag=false;
			}
		}
		return sk;
	}
	private Token stringToToken(String Token){
		Token t=new Token();
		int l = Token.length();
		t.setType(1);//"OPEN";
		StringBuilder sb=new StringBuilder();
		char refChar=0;
		boolean stringFlag=false;
		for(int i=0;i<l;i++){
			char c=Token.charAt(i);
			if(!stringFlag){
				if(c=='<'){
					refChar = c;
					continue;
				}else if(c==' '){
					if(sb.length()>0){
						if(refChar=='<'){
							t.setName(sb.toString());
						}else {
							t.addAttrs(sb.toString());
						}
						sb.delete(0, sb.length());
					}
					refChar=c;
					continue;
				}else if(c=='>'){
					if(sb.length()>0){
						if(t.getName()!=null){
							t.addAttrs(sb.toString());
						}else{
							t.setName(sb.toString());
						}
					}
					sb.delete(0, sb.length());
					refChar=c;
					continue;
				}else if(c=='/'){
					if(refChar=='<'&&sb.length()==0){
						t.setType(-1);//"CLOSE";
						refChar=c;
						continue;
					}else {
						if(sb.length()>0){
							if(t.getName()!=null){
								t.addAttrs(sb.toString());
							}else{
								t.setName(sb.toString());
							}
						}
						t.setSelfClose(true);
						sb.delete(0, sb.length());
						refChar=c;
						continue;
					}
				}
			}
			if(c=='\\'){
				sb.append(Token.charAt(++i));
				continue;
			}else if((c=='"'||c=='\'')){
				if(refChar!=c&&!stringFlag){
					stringFlag=true;
					refChar=c;
				}else if(refChar==c){
					stringFlag=false;
					refChar=0;
				}
			}
			sb.append(c);
		}
		return t;
	}
}
