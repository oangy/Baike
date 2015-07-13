package com.mh2z.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {//FragmentPagerAdapter

//	private FragmentManager fm;
	private ArrayList<Fragment> fragments = null;
	@SuppressWarnings("unused")
	private Context context;

	public ViewPagerAdapter(Context context, FragmentManager fm, ArrayList<Fragment> fragments) {
		super(fm);
		this.context = context;
		this.fragments = fragments;
		notifyDataSetChanged();
	}
	
	public ViewPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
		
		notifyDataSetChanged();
//		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
//		Fragment fragment = new ColourFragment();
//		Bundle args = new Bundle();
//		args.putInt("title", arg0);
//		args.putSerializable("content",hotIssuesList.get(arg0));
//		fragment.setArguments(args);
//		return fragment;
		return fragments.get(arg0);
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return PagerAdapter.POSITION_NONE;
	}
	
	@Override
	public int getCount() {
		return fragments.size();//hotIssuesList.size();
	}
	
	
}