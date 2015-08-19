package com.mh2z.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.mh2z.adapter.CateListViewAdapter;
import com.mh2z.fragment.ChangeColorIconWithTextView;
import com.mh2z.fragment.FragHome;
import com.mh2z.fragment.FragMe;
import com.mh2z.fragment.FragMe.FragMeCallbacks;
import com.mh2z.fragment.FragMeInfo;
import com.mh2z.fragment.FragMeInfo.FragMeInfoCallbacks;
import com.mh2z.object.ListItem;
import com.mh2z.object.UserInfo;
import com.mh2z.utils.GetJsonUtils;

public class MainActivity extends FragmentActivity implements OnClickListener,
		OnPageChangeListener, FragMeCallbacks, FragMeInfoCallbacks {
	private int TOP_LEVEL = 0;
	private int SUB_LEVEL = 1;
	private int THIRD_LEVEL = 2;
	
	private final String TAG = "MainActivity";
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;

	private ListView topcateLV;
	private ListView subcateLV;
	private ListView thirdcateLV;

	private CateListViewAdapter top_adapter;
	private CateListViewAdapter sub_adapter;
	private CateListViewAdapter third_adapter;

	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mTabs;
	private List<ChangeColorIconWithTextView> mTabIndicator;

	private List<ListItem> topcateList;
	private List<ListItem> subcateList;
	private List<ListItem> thirdcaList;
	private String devbaseURL_1 = "http://192.168.1.106/HDWiki/index.php";
	private String devbaseURL = "http://58.198.177.38:8080/HDWiki/index.php";
	private String proBaseURL = "http://mhbb.mhedu.sh.cn:8080/hdwiki/index.php";

	// 初始化
	private void init() {
		mTitle = getTitle();
		topcateLV = (ListView) findViewById(R.id.topcateListView);
		subcateLV = (ListView) findViewById(R.id.subcateListView);
		thirdcateLV = (ListView) findViewById(R.id.thirdcateListview);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTabs = new ArrayList<Fragment>();
		mTabIndicator = new ArrayList<ChangeColorIconWithTextView>();

		topcateList = new ArrayList<ListItem>();
		subcateList = new ArrayList<ListItem>();
		thirdcaList = new ArrayList<ListItem>();

		initTabIndicator();

		Fragment frag1 = new FragHome();
		Fragment frag2 = new FragMe();

		mTabs.add(frag1);
		mTabs.add(frag2);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return mTabs.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return mTabs.get(arg0);
			}
		};
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
	}

	private void initTabIndicator() {
		ChangeColorIconWithTextView one = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_one);
		ChangeColorIconWithTextView two = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_two);
		ChangeColorIconWithTextView three = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_three);

		mTabIndicator.add(one);
		 mTabIndicator.add(two);
		mTabIndicator.add(three);

		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);

		one.setIconAlpha(1.0f);
	}

	public void onClick(View v) {

		resetOtherTabs();

		switch (v.getId()) {
		case R.id.id_indicator_one:
			mTabIndicator.get(0).setIconAlpha(1.0f);
			mDrawerLayout.openDrawer(Gravity.LEFT);
			break;
			
		case R.id.id_indicator_two:			
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(0, false);
			FragHome fHome = (FragHome) mTabs.get(0);
			fHome.visitHome();
			break;
		case R.id.id_indicator_three:
			mTabIndicator.get(2).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(2, false);
			break;
		}

	}

	private void resetOtherTabs() {
		for (int i = 0; i < mTabIndicator.size(); i++) {
			mTabIndicator.get(i).setIconAlpha(0);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.main_layout);

		// 初始化
		init();

		// 获取顶级分类的Json数据，并处理之，存到topList中
		getTopCatList();

		// 为顶级分类设置adapter
		top_adapter = new CateListViewAdapter(this, topcateList, TOP_LEVEL);
		topcateLV.setAdapter(top_adapter);

		// 单击一级分类显示二级分类
		topcateLV.setOnItemClickListener(topListener);

		// 单击二级分类显示三级分类
		subcateLV.setOnItemClickListener(subListener);

		// 单击三级，跳转到相应页面
		thirdcateLV.setOnItemClickListener(thirdListener);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer_me, R.string.app_name, R.string.auto_login) {

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle("分类");
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				subcateLV.setVisibility(View.GONE);
				thirdcateLV.setVisibility(View.GONE);
			}

		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void getTopCatList() {

		Thread subThread = new Thread(new Runnable() {

			@Override
			public void run() {

				List<ListItem> list = GetJsonUtils.getJsonData(devbaseURL
						+ "?app-get_top_cate", "GET");
				if (list != null) {
					topcateList = list;
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

	private void getSubCatList(final int cid, final int level) {

		Thread subThread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (level == 1) {
					List<ListItem> list = GetJsonUtils.getJsonData(devbaseURL
							+ "?app-get_sub_cate-" + String.valueOf(cid), "GET");
					if (list != null) {
						subcateList = list;
					} else {
						subcateList = new ArrayList<ListItem>();
					}
				} else {
					List<ListItem> list = GetJsonUtils.getJsonData(devbaseURL
							+ "?app-get_sub_cate-" + String.valueOf(cid), "GET");
					if (list != null) {
						thirdcaList = list;
					} else {
						thirdcaList = new ArrayList<ListItem>();
					}
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

	public void onPageSelected(int arg0) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (positionOffset > 0) {
			ChangeColorIconWithTextView left = mTabIndicator.get(position);
			ChangeColorIconWithTextView right = mTabIndicator.get(position + 1);

			left.setIconAlpha(1 - positionOffset);
			right.setIconAlpha(positionOffset);
		}

	}

	public void onPageScrollStateChanged(int state) {

	}

	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		} else {
			super.onBackPressed();
		}
	}

	@SuppressLint("ResourceAsColor")
	private OnItemClickListener topListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {

//			// 设置背景色
			top_adapter.setSelectposition(position);
			top_adapter.notifyDataSetChanged();

			// 获取点击的ListItem对象，并获取ID、name等...
			ListItem itemSelected = topcateList.get(position);

			int cid = itemSelected.getId();

			// 获取对应ID分类下的子分类和词条信息
			getSubCatList(cid, 1);
			sub_adapter = new CateListViewAdapter(MainActivity.this,
					subcateList, SUB_LEVEL);
			subcateLV.setAdapter(sub_adapter);
			subcateLV.setVisibility(View.VISIBLE);

//			subcateLV.setBackgroundResource(R.color.ic_pressed);
			System.out.println("_---------------------");
			thirdcateLV.setVisibility(View.GONE);
		}

	};

	@SuppressLint("ResourceAsColor")
	private OnItemClickListener subListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {

			sub_adapter.setSelectposition(position);
			sub_adapter.notifyDataSetChanged();

			// 获取点击的ListItem对象，并获取ID、name等...
			ListItem itemSelected = subcateList.get(position);
			int cid = itemSelected.getId();
			int flag = itemSelected.getFlag();

			// 如果flag为0获取对应ID分类下的子分类和词条信息
			if (flag == 0) {
				getSubCatList(cid, 2);
				third_adapter = new CateListViewAdapter(MainActivity.this,
						thirdcaList, THIRD_LEVEL);
				thirdcateLV.setAdapter(third_adapter);
				thirdcateLV.setVisibility(View.VISIBLE);


			} else {
				// 访问词条信息
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				FragHome fHome = (FragHome) mTabs.get(0);
				fHome.visitDoc(cid);
			}

		}
	};

	private OnItemClickListener thirdListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {

			// 获取点击的ListItem对象，并获取ID、name等...
			ListItem itemSelected = thirdcaList.get(position);
			int cid = itemSelected.getId();
			int flag = itemSelected.getFlag();

			mDrawerLayout.closeDrawer(Gravity.LEFT);
			// 如果flag为0获取对应ID分类下的子分类和词条信息
			if (flag == 0) {
				// 跳转到分类页面
				FragHome fHome = (FragHome) mTabs.get(0);
				fHome.visitCate(cid);
			} else {
				// 访问词条信息
				FragHome fHome = (FragHome) mTabs.get(0);
				fHome.visitDoc(cid);
			}

		}
	};

	@Override
	public void onChangeToFragInfo(UserInfo info, List<Cookie> cookies,
			CookieStore cookieStore) {

		/*
		 * 1.跳转到登陆成功的界面FragMeInfo
		 */
		Bundle arguments = new Bundle();
		arguments.putParcelable("userinfo", info);

		Fragment fragmeInfo = new FragMeInfo(cookieStore);
		fragmeInfo.setArguments(arguments);
		FragmentManager manager = getSupportFragmentManager(); // 获取FragmentManger对象
		FragmentTransaction transaction = manager.beginTransaction(); // 获取fragment的事务操作
		transaction.replace(R.id.fragme, fragmeInfo);
		transaction.addToBackStack(null);
		transaction.commit();
		/*
		 * 2.传递cookies到FragHome
		 */
		FragHome fHome = (FragHome) mAdapter.instantiateItem(mViewPager, 0);
		fHome.getCookies(cookies);
	}

	/*
	 * 实现callback接口方法，退出登录时切换到登录界面
	 */
	public void onChangeToFragMe() {
		Log.i(TAG, "Execute");
		Fragment fragme = new FragMe();

		FragmentManager manager = getSupportFragmentManager(); // 获取FragmentManger对象
		FragmentTransaction transaction = manager.beginTransaction(); // 获取fragment的事务操作
		transaction.replace(R.id.frag_userinfo, fragme);
		transaction.commit();
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			}
		}
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
