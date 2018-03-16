package com.ccloomi.cdte.filewatch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**© 2015-2018 Chenxj Copyright
 * 类    名：DirWatch
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月16日-上午9:10:01
 */
public class DirWatch extends Thread{
	private WatchService watchService;
	private Map<WatchKey, Path>keys;
	private String baseDir;
	
	private FileAction fileAction;
	
	public DirWatch(FileAction fileAction) throws IOException {
		this.watchService=FileSystems.getDefault().newWatchService();
		this.keys=new HashMap<>();
		this.baseDir=System.getProperty("user.dir");
		this.fileAction=fileAction;
	}
	public DirWatch registerDir(Path dir) {
		try {
			WatchKey key=dir.register(watchService,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);
			keys.put(key, dir);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	public DirWatch registerDirs(String...dirs) {
		int dl=dirs.length;
		Path[]ps=new Path[dl];
		for(int i=0;i<dl;i++) {
			if(dirs[i].startsWith("/")) {
				ps[i]=Paths.get(dirs[i]);
			}else {
				ps[i]=Paths.get(baseDir,dirs[i]);
			}
		}
		registerDirs(ps);
		return this;
	}
	private DirWatch registerDirs(Path...dirs) {
		for(Path dir:dirs) {
			try {
				Files.walkFileTree(dir,new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						registerDir(dir);
						return FileVisitResult.CONTINUE;
					}
				});
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}
	@Override
	public void run() {
		while(true) {
			WatchKey key;
			try {
				//此处会等待事件发生
				key=watchService.take();
			}catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			Path dir=keys.get(key);
			if(dir==null) {
				continue;
			}
			for(WatchEvent<?>event:key.pollEvents()) {
				// 事件可能丢失或遗弃
				if(event.kind()==StandardWatchEventKinds.OVERFLOW) {
					continue;
				}
				// 目录内的变化可能是文件或者目录
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);
				if(event.kind()==StandardWatchEventKinds.ENTRY_MODIFY) {
					this.fileAction.modify(child.toFile());
				}else if(event.kind()==StandardWatchEventKinds.ENTRY_CREATE) {
					this.fileAction.create(child.toFile());
					try {
						if(Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
							registerDirs(child);
						}
					}catch (Exception e) {
					}
				}else if(event.kind()==StandardWatchEventKinds.ENTRY_DELETE) {
					this.fileAction.delete(child.toFile());
				}else {
					continue;
				}
				
				if(!key.reset()) {
					// 移除不可访问的目录
					// 因为有可能目录被移除，就会无法访问
					keys.remove(key);
				}
				
			}
		}
	}
//	public static void main(String[] args) throws Exception{
//		System.out.println(System.getProperty("user.dir"));
//		System.out.println(Paths.get("/a", "b"));
//	}
}
