package com.example.cameratest;

import android.app.Activity;
import android.os.Bundle;
import android.app.ActionBar;
import android.content.Intent;


public class FragmentTabs extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		
		actionBar.addTab(actionBar.newTab().setText("사진검색").setTabListener(new TabListener<FragmentTab1>(this,"Tag1",FragmentTab1.class)),0,false);
		
		actionBar.addTab(actionBar.newTab().setText("text검색").setTabListener(new TabListener<FragmentTab2>(this,"Tag2",FragmentTab2.class)),1,true);
		
		actionBar.addTab(actionBar.newTab().setText("History").setTabListener(new TabListener<FragmentTab3>(this,"Tag3",FragmentTab3.class)),2,false);
		
		if(savedInstanceState != null){
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt("selectedTab",0));
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selectedTab", getActionBar().getSelectedNavigationIndex());
	}
	
	
}
