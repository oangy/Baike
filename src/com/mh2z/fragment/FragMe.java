package com.mh2z.fragment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.mh2z.activity.R;
import com.mh2z.object.UserInfo;

public class FragMe extends Fragment {

	private View view;
	private EditText userNameEditText;
	private EditText pswEditText;
	private CheckBox rememberCheckBox;
	private CheckBox autoLoginCheckBox;
	private Button loginButton;
	private SharedPreferences sp;
	private UserInfo info;
	private CookieStore cookieStore;
	List<Cookie> cookies;

	private String cookieURL = "http://192.168.1.106/HDWiki/index.php?app-cookie";
	private String loginURL = "http://192.168.1.106/HDWiki/index.php?app-login";

	// String name;
	private String userName;
	private String passWord;

	// 定义一个回调接口
	public interface FragMeCallbacks {
		public void onChangeToFragInfo(UserInfo info,List<Cookie> cookies, CookieStore cookieStore);
	}

	private FragMeCallbacks mCallbacks = sDummyCallbacks;

	private static FragMeCallbacks sDummyCallbacks = new FragMeCallbacks() {
		@Override
		public void onChangeToFragInfo(UserInfo info,List<Cookie> cookies, CookieStore cookieStore) {
			
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof FragMeCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		mCallbacks = (FragMeCallbacks) activity;
		
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.frag_me, null);
		userNameEditText = (EditText) view.findViewById(R.id.username);
		pswEditText = (EditText) view.findViewById(R.id.psw);
		rememberCheckBox = (CheckBox) view.findViewById(R.id.remember);
		autoLoginCheckBox = (CheckBox) view.findViewById(R.id.autologin);
		loginButton = (Button) view.findViewById(R.id.login);
		

		Log.i("FragMeActivity", "FragMeActivity");
		// handler = new LoginHandler();

		sp = getActivity().getSharedPreferences("userinfo", 0);
		userName = sp.getString("username", "");
		passWord = sp.getString("password", "");

		boolean chooseRemember = sp.getBoolean("isremember", false);
		boolean chooseAutoLogin = sp.getBoolean("isautologin", false);

		if (chooseRemember) {
			userNameEditText.setText(userName);
			pswEditText.setText(passWord);
			rememberCheckBox.setChecked(true);
		}
		if (chooseAutoLogin) {
			autoLoginCheckBox.setChecked(true);
			// 登陆系统
			doLogin();
//			mCallbacks.onChangeToFragInfo(info,cookies,cookieStore);
		}

		rememberCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						SharedPreferences.Editor editor = sp.edit();

						if (rememberCheckBox.isChecked()) {
							editor.putBoolean("isremember", true);
						} else {
							editor.putBoolean("isremember", false);
						}
						editor.commit();
					}
				});

		autoLoginCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {

						SharedPreferences.Editor editor = sp.edit();
						if (autoLoginCheckBox.isChecked()) {
							editor.putBoolean("isautologin", true);
						} else {
							editor.putBoolean("isautologin", false);
						}

						editor.commit();

					}
				});

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				userName = userNameEditText.getText().toString();
				passWord = pswEditText.getText().toString();

				// 测试
				if (userName.equals("") || passWord.equals("")) {
					Toast.makeText(getActivity().getApplicationContext(),
							"用户名或密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}

				// 登录系统逻辑
				doLogin();

				if (flag == 1) {
					SharedPreferences.Editor editor = sp.edit();

					editor.putString("username", userName);
					editor.putString("password", passWord);

					if (rememberCheckBox.isChecked()) {
						editor.putBoolean("isremember", true);
					} else {
						editor.putBoolean("isremember", false);
					}
					if (autoLoginCheckBox.isChecked()) {
						editor.putBoolean("isautologin", true);
					} else {
						editor.putBoolean("isautologin", false);
					}

					editor.commit();
					
					Toast.makeText(getActivity().getApplicationContext(),
							"登录成功", Toast.LENGTH_SHORT).show();
					// mCallback.LoginClicked();

					// 登录成功，更换为用户信息界面
					mCallbacks.onChangeToFragInfo(info,cookies,cookieStore);

				} else {
					// 提示失败
					Toast.makeText(getActivity().getApplicationContext(),
							"登录失败", Toast.LENGTH_SHORT).show();
				}

			}
		});
		
		getFirstCookie();
		return view;
	}

	private void doLogin() {

		Thread subThread = new Thread(new Runnable() {

			@Override
			public void run() {

				NameValuePair pairName = new BasicNameValuePair("username",
						userName);
				NameValuePair pairPsw = new BasicNameValuePair("psw", passWord);
				List<NameValuePair> pairLsit = new ArrayList<NameValuePair>();
				pairLsit.add(pairName);
				pairLsit.add(pairPsw);

				try {

					HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
							pairLsit);

					HttpPost httpPost = new HttpPost(loginURL);
					httpPost.setEntity(requestHttpEntity);

					DefaultHttpClient httpClient = new DefaultHttpClient();
					httpClient.setCookieStore(cookieStore);
					HttpResponse response = httpClient.execute(httpPost);

					parseResponse(response);
					getCookie((DefaultHttpClient) httpClient);

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
	
	private void getFirstCookie() {

		Thread subThread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {					
					HttpPost httpPost = new HttpPost(cookieURL);
					HttpClient httpClient = new DefaultHttpClient();
					httpClient.execute(httpPost);
					getCookie((DefaultHttpClient) httpClient);
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
	
	 private void getCookie(DefaultHttpClient httpClient) {
		 cookieStore = httpClient.getCookieStore();
         cookies = cookieStore.getCookies();
         
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < cookies.size(); i++) {
             Cookie cookie = cookies.get(i);
             String cookieName = cookie.getName();
             String cookieValue = cookie.getValue();
             if (!TextUtils.isEmpty(cookieName)
                      && !TextUtils.isEmpty(cookieValue)) {
                 sb.append(cookieName + "=" );
                 sb.append(cookieValue + ";" );
            }
        }
        Log. e( "cookie", sb.toString());
	 }
	 
	private int flag;  //检测登录是否成功的标志，成功flag为1，否则为0

	private void parseResponse(HttpResponse response) {
		if (null == response) {
			return;
		}

		HttpEntity httpEntity = response.getEntity();
		String result = new String();
		try {
			InputStream inputStream = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			result = "";
			String line = "";
			while (null != (line = reader.readLine())) {
				result += line;
			}
			Log.e("FragMe", result);
			JSONObject object = new JSONObject(result);
			flag = object.getInt("flag");

			if (flag == 1) {
				String nameString = object.getString("username");
				int gender = object.getInt("gender");
				// Date birthday = object.get
				String imageUrl = object.getString("image");
				info = new UserInfo(nameString, gender, imageUrl);
			} else {
				flag = 0;
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
