package com.tuxt.item.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.netbeans.lib.cvsclient.CVSRoot;
import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;
import org.netbeans.lib.cvsclient.commandLine.BasicListener;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.connection.ConnectionFactory;

/**
 * 整理代码列表
 * 
 * 对于代码列表未提交CVS、代码版本高于最新版本/强制版本 和代码提错工程等问题，可以给予提示，见生成文件。
 * 
 * 
 * 需要配置前几行参数。 localPath 中存放需要整理的代码列表，以文本文件存放，不能以CVS开头， 不同工程代码放在不同文件中。
 * 代码路径中必须包含工程路径，比如客服代码必须包含 hacs_java 生成的代码列表会放在 “CVS+文件名” 这样的文件中。
 * 默认会记录CVS的最新版本，如果上线版本不是最新版本，后面加“:” 代表用当前版本 比如：
 * products\ICB\src\haicb_java\html\boss\icb\cust\IsfStarNetTest.jsp,1.1:
 * 
 * 
 * 
 * isTipLatest 版本低于最新版本时，是否提示。
 * 
 * 
 * @author guoxc
 *
 */
public class CvsUtil {

	private static GlobalOptions globalOptions = new GlobalOptions();
	// 代码文件所在路径 不同工程放在不同文件中 如果提交的不是最新版本，版本号后增加“:”，否则取CVS最新版本。
	private static final String localPath = "C:/Users/asus/Desktop/codelist";
	private static final CVSRoot csvroot = CVSRoot.parse(":pserver:tuxt:txt123@10.87.30.41:2401/cvsdata"); // CVS配置
	private static final String rootPathName = "products"; // 代码根目录路径
	private static final boolean isTipLatest = false; // 代码列表中的版本低于最新版本/强制版本时，是否提示
	private static Map keyPathMap = new HashMap();
	static {
		keyPathMap.put("haicb_java", "products/ICB/src/"); // 工程名称和工程路径。
		keyPathMap.put("hacs_java", "products/NGBOSS/HACS/src/");
		keyPathMap.put("hacs_inter", "products/NGBOSS/HACS/src/");
		keyPathMap.put("icbroute", "products/ICB/src/");
		keyPathMap.put("ol_java", "products/OL/src/");
		keyPathMap.put("rgsh", "products/OL/src/");
		keyPathMap.put("rgshcore", "products/OL/src/");
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// modifyCvsPath(new
		// File("E:/workspace/hacs_java/config/com.asiainfo.ol/ngcs/business/businessaccept/provbusi"));

		// 下载代码
		chechOutCode(new File(localPath));
		/*String modulename="products/OL/src/rgshcore/src/main/java/com/ai/rgshcore/service/IWorkOrderService.java,1.17";
		checkout(modulename);*/
		//downloadFileFromCvsServer();
	}

	private static void chechOutCode(File file) throws Exception {
		if (file.exists()) {
			if (file.getName().startsWith(rootPathName)) {
				return;
			}
			if (file.isFile()) {
				if (!file.getName().endsWith(".bak") && !file.getName().startsWith("cvs")) {
					chechOutFileCode(file);
				}
			} else {
				File[] subFiles = file.listFiles();
				for (File subFile : subFiles) {
					chechOutCode(subFile);
				}
			}
		}
	}

	private static void chechOutFileCode(File file) throws Exception {

		List<String> list = readFileXml(file);
		Map map = null;
		List<String> orignVerList = new ArrayList();
		List<String> errMsgList = new ArrayList();
		List filterList = new ArrayList();
		String prefix = ""; // 根据首行关键字获取前缀
		String key = "";
		if (list.size() > 0) {
			String firstPath = list.get(0);

			for (Iterator it = keyPathMap.keySet().iterator(); it.hasNext();) {
				String tempKey = (String) it.next();
				if (firstPath.indexOf(tempKey) > -1) {
					key = tempKey;
					prefix = (String) keyPathMap.get(key);
				}
			}

			if ("".equals(key)) {
				System.out.println("error : 没有检测到系统关键字和路径");
				return;
			} else {
				filterList = filterLines(list, key, orignVerList, errMsgList); // 过滤掉重复代码列表
			}
		}

		Collections.sort(filterList);

		//清除文件
		clearFile(new File(localPath + "/" + rootPathName));

		//下载文件到本地
		checkout(filterList, prefix);

		List<String> maxFileList = new ArrayList();
		getFileList(new File(localPath + "/" + rootPathName), maxFileList);

		map = getMaxVersionMap(maxFileList);

		PrintStream ps = new PrintStream(new FileOutputStream(new File(file.getParent() + "/cvs" + file.getName())));
		PrintStream console = System.out;
		System.setOut(ps);

		if (errMsgList.size() > 0) {
			for (String line : errMsgList) {
				System.out.println(line);
			}

		}

		for (String line : orignVerList) {
			if (line.indexOf(",") > 0) {
				String[] lineCol = line.split(",");
				if (map.containsKey(prefix + lineCol[0])) {
					String cvsVersion = (String) map.get(prefix + lineCol[0]);
					int compareInt = compareVersion(lineCol[1], cvsVersion, lineCol[0]);
					if (compareInt > 0) {
						System.out.println("版本号高于下载：" + line);
					} else if (compareInt < 0) {
						if (isTipLatest) {
							System.out.println("版本号低于下载：" + line);
						}
					}
				} else {
					System.out.println("代码没有下载到：" + line);
				}
			}
		}

		System.out.println("**************代码列表：");
		for (String line : maxFileList) {
			line = line.replace("/", "\\");
			System.out.println(line);
		}
		ps.flush();
		ps.close();

		System.setOut(console);
		//清除文件
		//clearFile(new File(localPath + "/" + rootPathName));
	}

	private static Map getMaxVersionMap(List<String> list) {

		Map map = new HashMap();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				String line = list.get(i);
				String[] lineCol = line.split(",");
				if (lineCol.length > 1) {
					map.put(lineCol[0], lineCol[1]);
				} else {
					System.out.println("Error getMaxVersionMap:" + line);
				}

			}
		}
		return map;
	}

	private static int compareVersion(String version1, String version2, String line) {

		int result = 0;
		int offset = version1.lastIndexOf(".");
		String vern1 = version1.substring(offset + 1);
		String preVern1 = version1.substring(0, version1.lastIndexOf("."));
		offset = version2.lastIndexOf(".");
		String vern2 = version2.substring(offset + 1);
		String preVern2 = version2.substring(0, version2.lastIndexOf("."));
		if (preVern1.equals(preVern2)) {
			result = Integer.valueOf(vern1) - Integer.valueOf(vern2);

		} else {
			System.out.println("版本号前路径不一致：" + line);
		}
		return result;
	}
	/**
	 * 删除文件或目录
	 * @param file
	 */
	private static void clearFile(File file) {

		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else {
				File[] subFiles = file.listFiles();
				if (subFiles != null) {
					for (File subFile : subFiles)
						clearFile(subFile);
				}
				file.delete();
			}
		}
	}

	private static void checkout(List<String> list, String prefix) throws Exception {		
		for (int i = 0; i < list.size(); i++) {
			checkout(prefix + list.get(i));
		}
		
	}

	public static List filterLines(List<String> list, String key, List OrignVerLine, List errMsg) throws Exception {
		Map forceVerMap = new HashMap();
		List result = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			String line = list.get(i);
			line = line.replace("\\", "/");
			if (line.indexOf(key) > -1) {
				line = line.substring(line.indexOf(key));
				int offset = line.indexOf(",");
				if (offset == (line.length() - 1)) {
					System.out.println("不能以逗号结尾 ：" + line);
					continue;
				}
				if (offset > 0 && offset != (line.length() - 1)) { // 去掉不含逗号和以逗号结尾的
					if (line.indexOf(":") > 0) {
						String temp = line;
						line = line.substring(0, line.indexOf(":"));
						OrignVerLine.add(line);
						String lineKey = temp.substring(0, offset);
						String version = temp.substring(offset + 1, temp.indexOf(":"));
						if (forceVerMap.containsKey(lineKey)) {
							String beforeVersion = (String) forceVerMap.get(lineKey);
							if (!beforeVersion.equals(version)) {
								errMsg.add("强制版本不一致 ：" + lineKey);
							}
							continue;
						} else if (result.contains(lineKey)) {
							errMsg.add("强制版本和最新版本重复：" + line);
							continue;
						} else {
							forceVerMap.put(lineKey, version);
						}
					} else {
						OrignVerLine.add(line);
						line = line.substring(0, line.indexOf(","));
						if (forceVerMap.containsKey(line)) {
							errMsg.add("强制版本和最新版本重复：" + line);
						}
					}
				} else {
					OrignVerLine.add(line);
				}
				if (!result.contains(line)) {
					result.add(line);
				}

			} else {
				System.out.println("不含有关键字" + key + ": " + line);
			}
		}
		return result;

	}

	/**
	 * 取得文件的路径和版本号，两者以逗号分隔
	 * @param file
	 * @param list
	 * @throws Exception
	 */
	public static void getFileList(File file, List list) throws Exception {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {

			File[] subFile = file.listFiles();
			if (subFile != null) {
				for (int i = 0; i < subFile.length; i++) {
					if ("CVS".equals(subFile[i].getName())) {
						continue;
					}
					getFileList(subFile[i], list);
				}
			}
		} else {
			String version = getVersion(file);
			String path = file.getAbsolutePath();
			path = path.substring(path.indexOf(rootPathName));
			path = path + "," + version;
			path = path.replace("\\", "/");
			list.add(path);
		}
	}
	/**
	 * 从cvs仓库下载到本地的文件的同级目录/CVS/Entries中获取版本号
	 * @param file
	 * @return
	 */
	private static String getVersion(File file) {
		String result = "";
		if (file.isFile())
			try {
					String name = file.getName();
					RandomAccessFile tempFile = new RandomAccessFile(file.getParent() + "/CVS/Entries", "rw");
					String line = tempFile.readLine();
					while (line != null && line != "") {
						if (line.indexOf("/" + name + "/") >= 0) {
							result = line.split("/")[2];
							break;
						}
						line = tempFile.readLine();
					}

					tempFile.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return result;

	}
	/**
	 * 按行读取文件中的内容放入List
	 */
	public static List readFileXml(File file) throws Exception {
		List list = new ArrayList();
		InputStream is = new FileInputStream(file);// Thread.currentThread().getContextClassLoader().getResourceAsStream("test/"+fileName);
		InputStreamReader reader = new InputStreamReader(is, "gb2312");

		BufferedReader rFile = new BufferedReader(reader);
		String line = rFile.readLine();

		while (line != null) {
			if (!"".equals(line.trim())) {
				list.add(line.trim());
			}
			line = rFile.readLine();

		}

		rFile.close();

		return list;
	}

	public static void modifyCvsPath(File file) throws Exception {
		if (file.isDirectory()) {
			if ("CVS".equals(file.getName())) {
				File newFile1 = new File(file.getAbsolutePath() + "/Root");
				newFile1.delete();
				RandomAccessFile newFile = new RandomAccessFile(file.getAbsolutePath() + "/Root", "rw");
				newFile.write(":pserver:tuxt@10.87.30.41:2401/cvsdata".getBytes());
				newFile.close();
			} else {
				File[] files = file.listFiles();
				for (File subFile : files) {
					modifyCvsPath(subFile);
				}
			}
		}

	}
	/**
	 * 检出指定模块的文件
	 * @param modulename 模块名。<br>模块名后可加“,版本号”，将为你检出指定版本的文件。
	 * @throws Exception
	 */
	public static void checkout(String modulename) throws Exception {
		Connection connection = ConnectionFactory.getConnection(csvroot);
		Client client = new Client(connection, new StandardAdminHandler());
		client.setLocalPath(localPath);//检出文件存放的目录
		client.getEventManager().addCVSListener(new BasicListener());

		CheckoutCommand command = new CheckoutCommand();
		
		command.setRecursive(true);
		if (modulename.indexOf(",") > 0) {
			String[] moduCols = modulename.split(",");
			command.setModule(moduCols[0]);
			command.setCheckoutByRevision(moduCols[1]);
		} else {
			command.setModule(modulename);
		}

		client.executeCommand(command, globalOptions);
		connection.close();

	}
	/**
	 * 根据文件cvscodelist.txt中的路径和版本号下载相应的文件到本地
	 * @throws Exception
	 */
	public static void downloadFileFromCvsServer() throws Exception {
		String pathname="C:/Users/asus/Desktop/codelist/cvscodelist.txt";
		File file=new File(pathname);
		List<String> files=readFileXml(file);
		boolean filterFirstLine=true;
		for (String string : files) {
			if (filterFirstLine) {
				filterFirstLine=false;
				continue;
			}
			System.out.println(string);
			string=string.replaceAll("\\\\", "/");
			checkout(string);
		}
	}
	
	public static void printUpLine() throws Exception {
		String last="C:/Users/asus/Desktop/codelist/history/last.txt";
		String now="C:/Users/asus/Desktop/codelist/history/now.txt";
		File lastFile=new File(last);
		File nowFile=new File(now);
		List<String> lastStrings=readFileXml(lastFile);
		List<String> nowStrings=readFileXml(nowFile);
		for (String strlast : lastStrings) {
			for (String strnow : nowStrings) {
				if (!strlast.equals(strnow)) {
					
				}
			}
		}
		
	}
}
