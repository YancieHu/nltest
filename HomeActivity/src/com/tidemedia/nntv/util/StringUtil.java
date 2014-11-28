package com.tidemedia.nntv.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

import com.google.gson.GsonBuilder;


public class StringUtil {

	public static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static boolean isEmpty( String input ) 
	{
		if ( input == null || "".equals( input ) )
			return true;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isNotEmpty(String s) {
		return (s != null && s.trim().length() > 0);
	}
	
	public static String readAssets(Context context,String fileName) {
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		if(fileName == null || fileName.equals("")){
			return null;
		}
		try {
			is = context.getAssets().open(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8")); 
			
			sb.append(br.readLine());
		
			String line ;
			while((line = br.readLine())!=null){
//				temp+=line;
				sb.append(line);
			}
		} catch (Exception e) {
			return null;
		}
		return sb.toString();
	}
	
	public static String readFromFile(Context context,String fileName) {
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		if(fileName == null || fileName.equals("")){
			return null;
		}
		try {
			is = context.getAssets().open(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8")); 
			
			sb.append(br.readLine());
		
			String line ;
			while((line = br.readLine())!=null){
//				temp+=line;
				sb.append(line);
			}
		} catch (Exception e) {
			return null;
		}
		return sb.toString();
	}
	
	public static int getChineseLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 1;
            }
        }
        return valueLength;
    }

	/*public static <T> String toJson(T obj){
		return new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter())
    			.create().toJson(obj);
		}	*/
	
	public static <T> T fromJson(String str,Type type)
	{
		return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss") 
		.create().fromJson(str,type); 
	}
	
	public static String toUnicode(String s) {
		String s1 = "";
		for (int i = 0; i < s.length(); i++) {
			s1 += "\\u" + Integer.toHexString(s.charAt(i) & 0xffff);
		}
		return s1;
	}
	
    public static String replaceUrlWithPlus(String url) {
        if (url != null) {
            return url.replaceAll("http://(.)*?/", "").replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
        return null;
    }

    public static String md5(String string) {

        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            //throw new RuntimeException("Huh, MD5 should be supported?", e);
            return string;
        } catch (UnsupportedEncodingException e) {
            //throw new RuntimeException("Huh, UTF-8 should be supported?", e);
            return string;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }

        return hex.toString();
    }
    
    public static int getLength(String content){
		String anotherString = "";
		try {
			anotherString = new String(content.getBytes("GBK"), "ISO8859_1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return anotherString.length();
    }
    
    public static String getFromAsset(Context mContext, String fileName){  
        String result="";  
        try{  
            InputStream in = mContext.getResources().getAssets().open(fileName);     //从Assets中的文件获取输入流  
            int length = in.available();                        //获取文件的字节数  
            byte [] buffer = new byte[length];                  //创建byte数组  
            in.read(buffer);                    //将文件中的数据读取到byte数组中  
            result = EncodingUtils.getString(buffer, "UTF-8");     //将byte数组转换成指定格式的字符串  
        }  
        catch(IOException e){  
            e.printStackTrace();                        //捕获异常并打印  
        }  
        catch(Exception e){  
            e.printStackTrace();  
        }  
        return result;  
       }
    
}
