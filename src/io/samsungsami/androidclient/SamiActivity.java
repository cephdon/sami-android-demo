package io.samsungsami.androidclient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.samihub.androidclient.R;

import io.samsungsami.model.Device;
import io.samsungsami.model.DeviceArray;
import io.samsungsami.model.DevicesEnvelope;
import io.samsungsami.model.User;
import io.samsungsami.android.Sami;
import io.samsungsami.android.SamiClient;
import io.samsungsami.android.SamiStack;
import io.samsungsami.android.api.Call;
import io.samsungsami.android.api.Callback;
import io.samsungsami.android.api.Code;
import io.samsungsami.client.ApiException;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SamiActivity extends Activity implements ActionBar.TabListener, SamiClient {
	public static final int SECTION_USER_SELF = 1;
	public static final int SECTION_DEVICES = 2;
	public static final int SECTION_LOGOUT = 3;
	static SamiActivity instance;
	Sami sami;
	
	// User information
	static TextView txtUid;
	static TextView txtName;
	static TextView txtEmail;
	static TextView txtFullName;
	static TextView txtModified;
	static TextView txtCreated;
	
	// Devices list
	static GenericAdapter devicesAdapter;
	static ListView devicesListView; 

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_sami);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sami, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent (this, RegisterActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		int section = tab.getPosition()+1;
		switch (section) {
		case SECTION_LOGOUT:
			sami.deleteCredentials();
			finish();
			break;
		case SECTION_DEVICES:
			setDevicesListAdapter();
			break;
		case SECTION_USER_SELF:
			
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView;
			int section = getArguments().getInt(
					ARG_SECTION_NUMBER);
			
			if(SECTION_USER_SELF == section ){
				rootView = inflater.inflate(R.layout.fragment_user_self, container,
						false);
				
				txtUid = (TextView) rootView.findViewById(R.id.uid);
				txtName = (TextView) rootView.findViewById(R.id.name);
				txtFullName = (TextView) rootView.findViewById(R.id.fullName);
				txtEmail = (TextView) rootView.findViewById(R.id.email);
				txtCreated = (TextView) rootView.findViewById(R.id.created);
				txtModified = (TextView) rootView.findViewById(R.id.modified);
				
				instance.refreshUserData();
			} else if(SECTION_DEVICES == section ){
				rootView = inflater.inflate(R.layout.fragment_devices, container,
						false);
				
				devicesListView = (ListView) rootView
						.findViewById(R.id.listViewDevices);
				
				devicesListView.setOnItemClickListener(new OnItemClickListener() {
					  @Override
					  public void onItemClick(AdapterView<?> parent, View view,
					    int position, long id) {
						String did = ((TextView) view.findViewById(R.id.label)).getText().toString();
						String dtid = ((TextView) view.findViewById(R.id.value)).getText().toString();
					    Intent intent = new Intent(instance, DevicesActivity.class);
					    intent.putExtra("did", did);
					    intent.putExtra("dtid", dtid);
					    startActivity(intent);
					  }
					}); 
				
			} else {
				rootView = inflater.inflate(R.layout.fragment_sami, container,
						false);
				
				TextView textView = (TextView) rootView
						.findViewById(R.id.section_label);
				textView.setText(R.string.title_section3);
			}
			return rootView;
		}
	}
	
	 
	@Override
	protected void onResume() {
		super.onResume();
		initSami();
	}
	
	/**
	 * Passes the context to SAMI wrapper
	 */
	void initSami(){
		sami = Sami.setInstance(this, new SamiStack(Config.APP_ID, Config.REDIRECT_URI));
	} 
	
	/**
	 * After a login or users/self call, refresh the data on UI
	 */
	void refreshUserData(){
		if(!sami.hasValidToken() || sami.getUser() == null){
			sami.login();
		} else {
			User user = sami.getUser();
			txtUid.setText(user.getId());
			txtName.setText(user.getName());
			txtFullName.setText(user.getFullName());
			txtEmail.setText(user.getEmail());
			String createdOn = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSZ").format(new Date(user.getCreatedOn().longValue()));
			txtCreated.setText(createdOn);
			String modifiedOn = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSZ").format(new Date(user.getModifiedOn().longValue()));
			txtModified.setText(modifiedOn);
			
			getDevicesIntoAdapter();
		}
	}
	
	/**
	 * Passes the device data adapter to the list view on UI
	 */
	public void setDevicesListAdapter(){
		if(devicesListView != null){
			devicesListView.setAdapter(devicesAdapter);
		}
	}
	
	/**
	 * Populates the devices adapter with data comming from SAMI
	 */
	public void getDevicesIntoAdapter(){
		
		new Call(new Code() {
			
			@Override
			public Object run() {
				DeviceArray deviceArray = null;
    			try {
    				DevicesEnvelope devicesEnvelope = sami.getUsersApi()
    						.getUserDevices(0, 100, false, sami.getCredentials().getId());
    				deviceArray = devicesEnvelope.getData();
    			} catch (ApiException e) {
    				e.printStackTrace();
    			}
    			return deviceArray;
			}
		}, new Callback() {
			
			@Override
			public void onApiResult(Object result) {
				DeviceArray deviceArray = (DeviceArray) result;
				devicesAdapter = new GenericAdapter(SamiActivity.this, addaptDeviceData(deviceArray));
			}
		}).execute();
		
		
	}
	
	/**
	 * Addapts the data comming from SAMI android client to a list adapter 
	 * This has nothing to do with SAMI, just UI work to display a list view
	 * @param deviceArray
	 * @return
	 */
	private ArrayList<Item> addaptDeviceData(DeviceArray deviceArray){
		ArrayList<Item> items = new ArrayList<Item>();
		
		for(Device device : deviceArray.getDevices()){
			items.add(new Item(device.getId(), device.getDtid()));
		}
		
		return items;
	}

	/**
	 * On a successful login, refresh user data
	 */
	@Override
	public void onLoginCompleted(String accessToken) {
		refreshUserData();
	}

	@Override
	public void onLoginCanceled() {
	}

	@Override
	public void onInvalidCredentials() {
	}


}
