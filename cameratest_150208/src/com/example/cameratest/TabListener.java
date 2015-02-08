package com.example.cameratest;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.widget.Toast;

public class TabListener<T extends Fragment> implements ActionBar.TabListener {

	private Activity mActivity;
	private String mTag;
	private Class<T> mClass;
	private Fragment mFragment;

	public TabListener(FragmentTabs fragmentTabs, String tag, Class<T> class1) {
		mActivity = fragmentTabs;
		mTag = tag;
		mClass = class1;
		mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
		if (mFragment != null && !mFragment.isDetached()) {
			FragmentTransaction fragmentTranscation = mActivity
					.getFragmentManager().beginTransaction();
			fragmentTranscation.detach(mFragment);
			fragmentTranscation.commit();
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (mFragment == null) {
			mFragment = Fragment.instantiate(mActivity, mClass.getName(), null);
			ft.add(android.R.id.content, mFragment, mTag);
		} else {
			ft.attach(mFragment);
		}

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (mFragment != null) {
			ft.detach(mFragment);
		}

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		Toast.makeText(mActivity, "onTabReselected!", Toast.LENGTH_SHORT)
				.show();
	}
}
