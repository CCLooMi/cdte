package com.ccloomi.cdte.filewatch;

import java.io.File;

/**© 2015-2018 Chenxj Copyright
 * 类    名：FileAction
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月16日-下午1:30:33
 */
public interface FileAction {
	public void delete(File file);
	public void modify(File file);
	public void create(File file);
}
