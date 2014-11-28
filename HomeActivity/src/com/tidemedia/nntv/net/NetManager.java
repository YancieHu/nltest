package com.tidemedia.nntv.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tidemedia.nntv.util.LogUtil;

public class NetManager {
	private static CookieStore mCookie = null;
	public static boolean stop = false;
	public static String doPost(String reqUrl, HashMap<String, String> hashMap) {
 		DefaultHttpClient httpclient = new DefaultHttpClient();
		
        if (mCookie == null) {
                mCookie = httpclient.getCookieStore();
        } else {
        	httpclient.setCookieStore(mCookie);
        }

		if(stop){
			stop = false;
			return "stop";
		}
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		HttpPost httppost = new HttpPost(reqUrl);
		InputStream content = null;
		String returnConnection = null;
		
		if (hashMap != null) {
			/*if(!hashMap.containsKey("serviceCode")){
				hashMap.put("serviceCode", APIContants.SERVICE_CODE);
			}*/
			Set<String> keys = hashMap.keySet();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String key = (String) i.next();
				String value = (String) hashMap.get(key);
				if(key == null || key.trim().length()==0 || value == null || value.trim().length()==0){
					continue;
				}
				pairs.add(new BasicNameValuePair(key, value));
			}
		}
		if(stop){
			stop = false;
			return "stop";
		}
		int responseCode=0;
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,	"utf-8");
			if(stop){
				stop = false;
				return "stop";
			}
			/** 灏哖OST鏁版嵁鏀惧叆http璇锋眰 */
			httppost.setEntity(p_entity);
			
			/** 鍙戝嚭瀹為檯鐨凥TTP POST璇锋眰 */
			if(stop){
				stop = false;
				return "stop";
			}
			HttpResponse response = httpclient.execute(httppost);
			if(stop){
				stop = false;
				return "stop";
			}
			responseCode=response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			content = entity.getContent();
			if(stop){
				stop = false;
				return "stop";
			}
			returnConnection = convertStreamToString(content);
			
		} catch (Exception uee) {
			uee.printStackTrace();
			return null;
		} 
		try {
			if(content!=null)
			{
				content.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if(stop){
			stop = false;
			return "stop";
		}
		httpclient.getConnectionManager().shutdown();
		return returnConnection;
	}
	
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

		public static boolean checkNetWork(Context context){
	        boolean isAvailable = netWorkIsAvailable(context);
	        if(!isAvailable){
	       	 openDialog(context);
	       	 return false;
	        }
	        return true;
		}
		
		public static boolean netWorkIsAvailable(Context context) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE ); 
			NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
			if(activeNetInfo!=null){
				if(activeNetInfo.isAvailable()){
					return true;
				}else{
					return false;
				}
			}
			return false;
		}
		
		private static void openDialog(final Context context) {
			final Builder builder=new AlertDialog.Builder(context);
			builder.setTitle("没有可用的网络");
	        builder.setMessage("请开启GPRS或WIFI网络连接");
	        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
		               Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
	                   context.startActivity(intent);

		}
	        }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	                dialog.cancel();
	            }
	        }).create().show();

	}
	
}
