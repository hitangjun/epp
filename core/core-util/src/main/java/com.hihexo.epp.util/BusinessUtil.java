package com.hihexo.epp.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class BusinessUtil {

	private static String localIp="";

	static {
	   try {
			 localIp= Inet4Address.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
	 }

	public static void writeInterfaceLog(String content) {
		String path = "/mnt/logs/interfaceLog/";
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		String nowDate=DateUtil.getNowYYYYMMDD();
		path = path + "interface_"+nowDate + ".log";
		writeFile(path, content);
	}

	public static void writeFile(String path, String content) {
		FileWriter fw=null;
		try {
			fw = new FileWriter(path,true);
			fw.write(content+"\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
