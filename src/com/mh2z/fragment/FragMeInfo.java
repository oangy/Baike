package com.mh2z.fragment;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mh2z.activity.R;
import com.mh2z.object.UserInfo;
import com.mh2z.utils.GetUrlImage;

@SuppressLint("ValidFragment")
public class FragMeInfo extends Fragment {

	private View view;
	private ImageView image;
	private TextView name;
	private TextView gender;
	// private EditText birthday;
	private Button exitBtn;
	private UserInfo userInfo;
	private CookieStore cookieStore;
	private String logoutURL = "http://192.168.1.106/hdwiki/index.php?app-logout";

	public interface FragMeInfoCallbacks {
		public void onChangeToFragMe();
	}

	private FragMeInfoCallbacks mCallbacks = new FragMeInfoCallbacks() {

		@Override
		public void onChangeToFragMe() {

		}
	};

	private static FragMeInfoCallbacks sDummyCallbacks = new FragMeInfoCallbacks() {
		@Override
		public void onChangeToFragMe() {

		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof FragMeInfoCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
			mCallbacks = (FragMeInfoCallbacks) activity;

	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	// private SharedPreferences sp;
	// public FragMeInfo(){
	//
	// }
	//
	public FragMeInfo(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	private Handler handle = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				System.out.println("111");
				Bitmap bmp = (Bitmap) msg.obj;
				image.setImageBitmap(bmp);
				break;
			}
		};
	};

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.user_info, null);

		init();
		exitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				logout();
				mCallbacks.onChangeToFragMe();

			}
		});

		return view;
	}

	private void logout() {
		Log.e("Logout", "Logout");
		Thread subThread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {

					HttpPost httpPost = new HttpPost(logoutURL);

					DefaultHttpClient httpClient = new DefaultHttpClient();
					httpClient.setCookieStore(cookieStore);
					httpClient.execute(httpPost);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		subThread.start();
		try {
			subThread.join();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	private void init() {

		image = (ImageView) view.findViewById(R.id.info_image);
		name = (TextView) view.findViewById(R.id.info_name);
		gender = (TextView) view.findViewById(R.id.info_gender);

		exitBtn = (Button) view.findViewById(R.id.info_exit);

		userInfo = getArguments().getParcelable("userinfo");
		// cookieStore = getArguments().getParcelable("cookiestore");
		name.setText(userInfo.getName());
		if (userInfo.getGender() == 1) {
			gender.setText("男");
		} else {
			gender.setText("女");
		}

		// final String url = "http://mhbb.mhedu.sh.cn:8080/HDWiki/" +
		// userInfo.getImageUrl();
		final String url = "http://192.168.1.106/HDWiki/"
				+ userInfo.getImageUrl();
		Log.i("TAG", url);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Bitmap bmp = GetUrlImage.getUrlimage(url);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = bmp;
				handle.sendMessage(msg);
			}
		}).start();
		exitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				logout();
				mCallbacks.onChangeToFragMe();
			}
		});
	}

	/*
	 * private Bitmap getURLimage(String url) { Bitmap bmp = null; try { URL
	 * myurl = new URL(url); // 获得连接 HttpURLConnection conn =
	 * (HttpURLConnection) myurl.openConnection();
	 * conn.setConnectTimeout(6000);// 设置超时 conn.setDoInput(true);
	 * conn.setUseCaches(false);// 不缓存 conn.connect(); InputStream is =
	 * conn.getInputStream();// 获得图片的数据流 bmp = BitmapFactory.decodeStream(is);
	 * is.close(); } catch (Exception e) { e.printStackTrace(); } return bmp; }
	 */

}
