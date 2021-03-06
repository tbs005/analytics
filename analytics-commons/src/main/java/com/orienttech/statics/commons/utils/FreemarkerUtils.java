package com.orienttech.statics.commons.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;

import sun.misc.BASE64Encoder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerUtils {
	private static Configuration configuration = null;
	private static String templatePath = PropertiesConstants.getString(PropertiesConstants.USER_HOME) + "/uploads/analytics/templatePath";
	public static String wordPath = PropertiesConstants.getString(PropertiesConstants.USER_HOME) + "/uploads/analytics/wordPath";
	private final static String ENCODING="UTF-8";
	static{
		configuration = new Configuration();
		configuration.setDefaultEncoding(ENCODING);
		//configuration.setClassForTemplateLoading(FreemarkerUtils.class, "/template");
		
		String temp = PropertiesConstants.getString(PropertiesConstants.TEMPLATE_PATH);
		try {
			if (StringUtils.isNoneBlank(temp)) {
				templatePath = PropertiesConstants.getString(PropertiesConstants.USER_HOME) + temp;
			} 
			configuration.setDirectoryForTemplateLoading(new File(templatePath));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		temp = PropertiesConstants.getString(PropertiesConstants.WORD_PATH);
		if (StringUtils.isNoneBlank(temp)) {
			wordPath = PropertiesConstants.getString(PropertiesConstants.USER_HOME) + temp;
		}
		File dir = new File(wordPath);
		if(!FileUtils.isExists(dir)){
			try {
				FileUtils.forceMkdir(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @param templateName
	 * @param outFileName
	 * @param dataMap
	 * @throws UnsupportedEncodingException 
	 */
	public static void createDoc(String templateName,String outFileName,Object dataMap) throws UnsupportedEncodingException{
		Template t = null;
		try {
			//test.ftl为要装载的模板
			t = configuration.getTemplate(templateName, ENCODING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File outFile = new File(wordPath + File.separator + outFileName);
		//输出文档路径及名称
		Writer out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		 
        try {
			t.process(dataMap, out);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(out!=null)
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @param templateName
	 * @param outputStream
	 * @param dataMap
	 */
	public static void createDoc(String templateName,OutputStream outputStream,Object dataMap){
		Template t=null;
		try {
			//test.ftl为要装载的模板
			t = configuration.getTemplate(templateName,ENCODING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//输出文档路径及名称
		Writer out = null;
        try {
        	out = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
			t.process(dataMap, out);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(out!=null)
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getImageStr(String imgFile) {
		InputStream in = null;
		byte[] data = null;
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}
}
