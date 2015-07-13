package com.mh2z.adapter;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mh2z.activity.R;
import com.mh2z.object.ListItem;

public class CateListViewAdapter extends BaseAdapter {
	private int TOP_LEVEL = 0;
	private int SUB_LEVEL = 1;
	private int THIRD_LEVEL = 2;
	
	private Context context;
	private List<ListItem> catList;
	private int selectposition = -1;
	private ImageView imageView;
	private int level = -1;
	
	public CateListViewAdapter() {
		super();
		catList = new ArrayList<ListItem>();
	}

	public CateListViewAdapter(Context context, List<ListItem> list, int level) {
		super();
		this.context = context;
		catList = list;
		this.level = level;
	}

	@Override
	public int getCount() {
		return catList.size();
	}

	@Override
	public Object getItem(int position) {
		return catList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.cate_item, null);
		}
		
		TextView cateTitle = (TextView) convertView
				.findViewById(R.id.cate_name);
		imageView = (ImageView) convertView.findViewById(R.id.image);

		ListItem item = catList.get(position);

		String urlName = item.getPicurl();
		final String url = "http://192.168.1.106/HDWiki/" + urlName;

		// Bitmap bitmap=GetUrlImage.getUrlimage(url);
		Log.i("ashiuh", url);
		int flag = item.getFlag();
		if (flag == 0) {
			 imageView.setImageResource(R.drawable.ic_cate);
			// imageView.setImageBitmap(bmp);
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					Bitmap bmp = GetUrlImage.getUrlimage(url);
//					Message msg = new Message();
//					msg.what = 0;
//					msg.obj = bmp;
//					handle.sendMessage(msg);
//				}
//			}).start();
		} else {
			imageView.setImageResource(R.drawable.ic_doc);
		}
		if (level == TOP_LEVEL) {
			convertView.setBackgroundColor(Color.parseColor("#ffffff"));
			if (selectposition == position) {
				convertView.setBackgroundColor(Color.parseColor("#E0E0E0"));
				cateTitle.setTextColor(Color.BLUE);
			} else {
				convertView.setBackgroundColor(Color.parseColor("#ffffff"));
				cateTitle.setTextColor(Color.BLACK);
			}
			
		}else if(level == SUB_LEVEL){
			convertView.setBackgroundColor(Color.parseColor("#E0E0E0"));
			if (selectposition == position) {
				convertView.setBackgroundColor(Color.parseColor("#C0C0C0"));
				cateTitle.setTextColor(Color.BLUE);
			} else {
				convertView.setBackgroundColor(Color.parseColor("#E0E0E0"));
				cateTitle.setTextColor(Color.BLACK);
			}
		}else if (level == THIRD_LEVEL) {
			convertView.setBackgroundColor(Color.parseColor("#C0C0C0"));
			if (selectposition == position) {
				convertView.setBackgroundColor(Color.parseColor("#00edfc"));
				cateTitle.setTextColor(Color.BLUE);
			} else {
				convertView.setBackgroundColor(Color.parseColor("#C0C0C0"));
				cateTitle.setTextColor(Color.BLACK);
			}
		}else {
			convertView.setBackgroundColor(Color.parseColor("#ffffff"));
			if (selectposition == position) {
				convertView.setBackgroundColor(Color.parseColor("#00edfc"));
				cateTitle.setTextColor(Color.BLUE);
			} else {
				convertView.setBackgroundColor(Color.parseColor("#ffffff"));
				cateTitle.setTextColor(Color.BLACK);
			}
		}

		cateTitle.setText(item.getName());
		return convertView;

	}

	public void setSelectposition(int position) {
		this.selectposition = position;
	}

	private Handler handle = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				System.out.println("111"+msg.obj);
				Bitmap bmp = (Bitmap) msg.obj;
				imageView.setImageBitmap(bmp);
				break;
			}
		};
	};
	/*
	 * private Bitmap getUrlimage(String url) { Bitmap bmp = null; try { URL
	 * myurl = new URL(url); // 获得连接 HttpURLConnection conn =
	 * (HttpURLConnection) myurl.openConnection();
	 * conn.setConnectTimeout(6000);// 设置超时 conn.setDoInput(true);
	 * conn.setUseCaches(false);// 不缓存 conn.connect(); InputStream is =
	 * conn.getInputStream();// 获得图片的数据流 bmp = BitmapFactory.decodeStream(is);
	 * is.close(); } catch (Exception e) { e.printStackTrace(); } return bmp; }
	 */
}
