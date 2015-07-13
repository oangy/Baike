package com.mh2z.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mh2z.object.ListItem;

public class GetJsonUtils {
	
	
	public static List<ListItem> getJsonData(String url,String method){
		HttpURLConnection conn;
		InputStream is;
		try {
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setRequestMethod(method);
				is = conn.getInputStream();
				BufferedReader reader=new BufferedReader(new InputStreamReader(is));
				String line="";
				StringBuilder result = new StringBuilder();
				
				while((line = reader.readLine())!=null){
					result.append(line);
			}
				Log.i("Test", result.toString());
			if (result.toString().isEmpty()) {
				return null;
			} else {
				List<ListItem> list =  JsonParse(result.toString());
				return list;
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;			
}
	
	
	private static List<ListItem> JsonParse(String jsonData) {
		List<ListItem> listItems = new ArrayList<ListItem>();
//		String jsonData=(String) msg.obj;
//		Log.i("JsonDataHandler",jsonData);
		try {
			JSONObject object = new JSONObject(jsonData);
			int flag = object.getInt("flag");
//			Log.i("flag", String.valueOf(flag));
			
			
			if (0 == flag) {
				//表示返回的是子类数据
				JSONArray subcateArray = object.getJSONArray("cate");
				
				for(int i=0;i<subcateArray.length();i++)
				{
					JSONObject subcate = subcateArray.getJSONObject(i);
					int cid = subcate.getInt("cid");
//					int pid = subcate.getInt("pid");
					String name = subcate.getString("name");
					String url=subcate.getString("image");
					listItems.add(new ListItem(cid, name,0,url));				
				}
				
			} else if (flag == 1) {
				//返回的是词条数据
					JSONArray docArray = object.getJSONArray("docs");
				
					for(int i=0;i < docArray.length();i++)
					{
						JSONObject doc = docArray.getJSONObject(i);
						int did = doc.getInt("did");
						
						String title = doc.getString("title");
//						listDocs.add(new Doc(did,title));
						listItems.add(new ListItem(did, title,1));
					}
				
			}else {
					JSONArray subcateArray = object.getJSONArray("cate");
				
					for(int i=0;i<subcateArray.length();i++)
					{
						JSONObject subcate = subcateArray.getJSONObject(i);
						int cid = subcate.getInt("cid");
//						int pid = subcate.getInt("pid");
						String name = subcate.getString("name");
//						listCate.add(new Category(cid,pid,name));
						String url=subcate.getString("image");
						listItems.add(new ListItem(cid, name,0,url));				
					}
					
					JSONArray docArray = object.getJSONArray("docs");
					
					for(int i=0;i < docArray.length();i++)
					{
						JSONObject doc = docArray.getJSONObject(i);
						int did = doc.getInt("did");
						
						String title = doc.getString("title");
//						listDocs.add(new Doc(did,title));
						listItems.add(new ListItem(did, title,1));
					}
			}
			
			
			return listItems;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}
	
}
