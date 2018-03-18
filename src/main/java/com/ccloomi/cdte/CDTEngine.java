package com.ccloomi.cdte;
import static com.ccloomi.cdte.CDTEConfigure.charset;
import static com.ccloomi.cdte.CDTEConfigure.suffix;
import static com.ccloomi.cdte.CDTEConfigure.templateLoadPath;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccloomi.cdte.filewatch.DirWatch;
import com.ccloomi.cdte.filewatch.FileAction;

/**© 2015-2018 Chenxj Copyright
 * 类    名：CDTEngine
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月14日-下午6:06:58
 */
public class CDTEngine {
	private Logger log=LoggerFactory.getLogger(getClass());
	
	private Map<String,CDTemplate>templatesMap;
	//用来处理include指令
	private Map<String, CDTEDocument>cdtedocMap;
	private CDTEParser parser;
	private DirWatch dw;
	public CDTEngine() {
		this.templatesMap=new HashMap<>();
		this.cdtedocMap=new HashMap<>();
		this.parser=new CDTEParser();
		try {
			this.dw=new DirWatch(new FileAction() {
				@Override
				public void modify(File file) {
					if(!file.isDirectory()&&file.getName().endsWith(suffix)){
						log.debug("modify template file:\t",file.getAbsolutePath());
						CDTEDocument doc=cdtedocMap.get(file.getName());
						parserFile(file,doc);
						if(doc.isSnippet()) {
							reParserRelySnippet(file.getName());
						}else {
							templatesMap.put(file.getName(),parser.parser(doc,cdtedocMap));
						}
					}else {
						log.debug("modify file:\t",file.getAbsolutePath());
					}
				}
				@Override
				public void delete(File file) {
					if(!file.isDirectory()&&file.getName().endsWith(suffix)){
						log.debug("delete template file:\t{}",file.getAbsolutePath());
						cdtedocMap.remove(file.getName());
						templatesMap.remove(file.getName());
					}else {
						log.debug("delete file:\t{}",file.getAbsolutePath());
						//因为监控不到删除文件夹下的文件，故如此处理
						List<String>deletes=new ArrayList<>();
						for(Entry<String, CDTEDocument>entry:cdtedocMap.entrySet()) {
							CDTEDocument doc=entry.getValue();
							if(doc.belongDir(file.getAbsolutePath())) {
								templatesMap.remove(entry.getKey());
								deletes.add(entry.getKey());
								if(doc.isSnippet()) {
									reParserRelySnippet(file.getName());
								}
							}
						}
						for(String del:deletes) {
							cdtedocMap.remove(del);
						}
						
					}
				}
				@Override
				public void create(File file) {
					if(!file.isDirectory()&&file.getName().endsWith(suffix)){
						log.debug("create template file:\t{}",file.getAbsolutePath());
						CDTEDocument doc=parserFile(file);
						if(doc.isSnippet()) {
							reParserRelySnippet(file.getName());
						}else {
							templatesMap.put(file.getName(),parser.parser(doc,cdtedocMap));
						}
					}else {
						log.debug("create file:\t{}",file.getAbsolutePath());
					}
				}
			});
		}catch (Exception e) {
			//有些系统不支持文件监控
			e.printStackTrace();
		}
	}
	public CDTEngine compileTemplates() {
		return this.scanTemplates().pretreatment();
	}
	/**
	 * 描述：snippet的创建修改删除，都有将这些依赖这个snippet的template重新parser
	 * 作者：chenxj
	 * 日期：2018年3月16日 - 下午9:22:57
	 * @param snippetName
	 */
	private void reParserRelySnippet(String snippetName) {
		for(Entry<String, CDTEDocument>entry:cdtedocMap.entrySet()) {
			if(entry.getValue().relySnippet(snippetName)) {
				templatesMap.put(entry.getKey(), parser.parser(entry.getValue(), cdtedocMap));
			}
		}
	}
	private CDTEngine scanTemplates() {
		int pathsl=templateLoadPath.length;
		try{
			for(int i=0;i<pathsl;i++){
				Path p=templateLoadPath[i].charAt(0)=='/'?Paths.get(templateLoadPath[i]):Paths.get(System.getProperty("user.dir"), templateLoadPath[i]);
				Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						File f=file.toFile();
						if(f.getName().endsWith(suffix)) {
							parserFile(f);
						}
						return FileVisitResult.CONTINUE;
					}
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						if(dw!=null) {
							dw.registerDir(dir);
						}
						return FileVisitResult.CONTINUE;
					}
				});
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	private CDTEDocument parserFile(File file,CDTEDocument...docs) {
		log.debug("Parser template file [{}]", file.getName());
		try {
//			FileReader fr=new FileReader(file);
//			char[]c=new char[(int)file.length()];
//			fr.read(c);
//			fr.close();
			FileInputStream in=new FileInputStream(file);
			int bufferSize=60000;
	        byte[] buffer = new byte[bufferSize];
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream(bufferSize);
	        int read;
	        int remaining = bufferSize;
	        try{
	        	while (!Thread.interrupted()) {
	                read = in.read(buffer);
	                if (read == -1) break;
	                if (read > remaining) {
	                    outStream.write(buffer, 0, remaining);
	                    break;
	                }
	                remaining -= read;
	                outStream.write(buffer, 0, read);
	            }
	        }catch (Exception e) {
	        	e.printStackTrace();
			}
	        byte[]bytes=outStream.toByteArray();
	        in.close();
	        String html=new String(bytes,charset);
	        
			if(docs.length>0) {
				docs[0].setoTokens(parser.parser(html));
			}else {
				CDTEDocument doc=new CDTEDocument(parser.parser(html),file.getAbsolutePath());
				cdtedocMap.put(file.getName(),doc);
				return doc;
			}
		}catch (Exception e) {
			log.error("Parser template file [{}] error:\t{}", file.getName(),e);
		}
		return null;
	}
	private CDTEngine pretreatment() {
		for(Entry<String, CDTEDocument>entry:cdtedocMap.entrySet()) {
			//2template
			if(!entry.getValue().isSnippet()) {
				templatesMap.put(entry.getKey(),parser.parser(entry.getValue(),cdtedocMap));
			}
		}
		//start dir watch service
		dw.start();
		return this;
	}
	public boolean checkTemplate(String url) {
		return templatesMap.containsKey(url);
	}

	public CDTemplate findTemplate(String url) {
		return templatesMap.get(url);
	}
	
	public void testInit(String url) {
		if(!templatesMap.containsKey(url)) {
			File file=Paths.get(System.getProperty("user.dir"),url).toFile();
			CDTEDocument doc=parserFile(file);
			templatesMap.put(url,parser.parser(doc, cdtedocMap));
		}
	}
	
	public static void main(String[] args) throws Exception {
		CDTEngine eng=new CDTEngine();
		eng.compileTemplates();
		CDTemplate tp=eng.findTemplate("test.html");
		Map<String, Object>model=new HashMap<>();
		model.put("title", "CDTE TEST!!!");
		model.put("hello", "Hello CDTE.");
		model.put("users", Arrays.asList("Seemie","Tommy"));
		tp.render(model, System.out);
		
//		eng.testInit("templates/cdte.tpl");
//		eng.findTemplate("templates/cdte.tpl").render(model, System.out);
	}
}
