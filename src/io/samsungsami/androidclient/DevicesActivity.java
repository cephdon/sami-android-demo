package io.samsungsami.androidclient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.samihub.androidclient.R;

import io.samsungsami.model.ManifestProperties;
import io.samsungsami.model.ManifestPropertiesEnvelope;
import io.samsungsami.model.NormalizedMessage;
import io.samsungsami.model.NormalizedMessagesEnvelope;
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
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DevicesActivity extends Activity implements ActionBar.TabListener, SamiClient {
	public static final int SECTION_MANIFEST = 1;
	public static final int SECTION_MESSAGES = 2;
	public static final int SECTION_TEST = 3;
	static DevicesActivity instance;
	Sami sami;
	
	// Manifest fields list
	static ExpandableAdapter manifestAdapter;
	static ExpandableListView manifestListView;
	List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
	
    // Messages list
	static GenericAdapter messagesAdapter;
	static ListView messagesListView;

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
		setContentView(R.layout.activity_devices);

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
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		int section = tab.getPosition()+1;
		switch (section) {
		case SECTION_MANIFEST:
			setManifestListAdapter();
			break;
		case SECTION_MESSAGES:
			refreshMessagesData();
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
				return getString(R.string.devices_section1).toUpperCase(l);
			case 1:
				return getString(R.string.devices_section2).toUpperCase(l);
			case 2:
				return getString(R.string.devices_section3).toUpperCase(l);
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
			
			if(SECTION_MANIFEST == section ){
				rootView = inflater.inflate(R.layout.fragment_manifest, container,
						false);
				
				manifestListView = (ExpandableListView) rootView
						.findViewById(R.id.listViewManifest);
				instance.initManifestList();
				
			} else if(SECTION_MESSAGES == section ){
				rootView = inflater.inflate(R.layout.fragment_messages, container,
						false);
				
				messagesListView = (ListView) rootView
						.findViewById(R.id.listViewMessages);
				instance.initMessagesList();
				
			} else {
				rootView = inflater.inflate(R.layout.fragment_sami, container,
						false);
				
				TextView textView = (TextView) rootView
						.findViewById(R.id.section_label);
				textView.setText(R.string.devices_section3);
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
	 * Sets the context of the Sami wrapper
	 */
	void initSami(){
		sami = Sami.setInstance(this, new SamiStack(Config.APP_ID, Config.REDIRECT_URI));
	} 
	
	/**
	 * Initializes the manifest's fields list
	 */
	void initManifestList(){
		instance.getManifestIntoAdapter();
	}
	
	/**
	 * Initializes the messages list
	 */
	void initMessagesList(){
		messagesListView.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
				  String mid = ((TextView) view.findViewById(R.id.label)).getText().toString();
				  Intent intent = new Intent(instance, MessagesActivity.class);
				  intent.putExtra("mid", mid);
				  startActivity(intent);
			  }
		}); 
	}
	
	/**
	 * Passes the manifest data adapter to the UI list
	 */
	void setManifestListAdapter(){
		if(manifestListView != null){
			manifestListView.setAdapter(manifestAdapter);
		}
	}
	
	/**
	 * Populates the manifest data adapter
	 */
	void getManifestIntoAdapter(){
		final String dtid = getIntent().getExtras().getString("dtid");
		
		new Call(new Code() {
			
			@Override
			public Object run() {
				try {
					ManifestPropertiesEnvelope envelope = sami.getDeviceTypesApi().
							getLatestManifestProperties(dtid);
	    			ManifestProperties deviceProperties = envelope.getData();
    				return deviceProperties;
    			} catch (ApiException e) {
    				e.printStackTrace();
    			}
    			return null;
			}
		}, new Callback() {
			
			@Override
			public void onApiResult(Object result) {
				if(result != null){
					ManifestProperties deviceProperties = (ManifestProperties) result;
					addaptManifestData(deviceProperties);
					manifestAdapter = new ExpandableAdapter(DevicesActivity.this, listDataHeader, listDataChild);
				}
				instance.setManifestListAdapter();
			}
		}).execute();
		
		
	}
	
	/**
	 * Addapts the data comming from SAMI android client to a list adapter 
	 * This has nothing to do with SAMI, just UI work to display a list view
	 * @param manifest
	 */
	private void addaptManifestData(ManifestProperties manifest){
		
		listDataHeader = new ArrayList<String>();
	    listDataChild = new HashMap<String, List<String>>();
		
		ArrayList<Item> items = new ArrayList<Item>();
		try{
			Map<String, ?> fields = manifest.getProperties();
			Map<String, ?> properties = (Map<String, ?>) fields.get("fields");
			
			Iterator entries = properties.entrySet().iterator();
			while (entries.hasNext()) {
			  Entry thisEntry = (Entry) entries.next();
			  Object key = thisEntry.getKey();
			  Map<String, ?> value = (Map<String, String>) thisEntry.getValue();
			  listDataHeader.add(key.toString());
			  
			  List<String> fieldDetails = new ArrayList<String>();
		        fieldDetails.add("Type: " +value.get("type"));
		        fieldDetails.add("Unit: " + value.get("unit"));
		        fieldDetails.add("Collection: " + value.get("isCollection"));
			  listDataChild.put(key.toString(), fieldDetails);
			  
			}
		} catch(NullPointerException npe){
			npe.printStackTrace();
		}
	}
	
	/**
	 * Populates the messages data adapter and passes it to the list view
	 */
	void refreshMessagesData(){
		if(messagesListView != null){
			final String did = getIntent().getExtras().getString("did");
			
			new Call(new Code() {
				
				@Override
				public Object run() {
					List<NormalizedMessage> messages = null;
	    			try {
	    				NormalizedMessagesEnvelope envelope = sami.getMessagesQueryApi()
	    						.getLastNormalizedMessages( 
	    								20, did, null);
	    						
	    				messages = envelope.getData();
	    			} catch (ApiException e) {
	    				e.printStackTrace();
	    			}
	    			return messages;
				}
			}, new Callback() {
				
				@Override
				public void onApiResult(Object result) {
					if(result != null){
						List<NormalizedMessage> normalizedMessages = (List<NormalizedMessage>) result;
						GenericAdapter adapter = new GenericAdapter(DevicesActivity.this, generateMessagesData(normalizedMessages));
						messagesListView.setAdapter(adapter);
					} else {
						Toast.makeText(DevicesActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
					}
				}
			}).execute();
			
			
		}
	}
	
	/**
	 * Addapts the data comming from SAMI android client to a list adapter 
	 * This has nothing to do with SAMI, just UI work to display a list view
	 * @param normalizedMessages
	 * @return
	 */
	private ArrayList<Item> generateMessagesData(List<NormalizedMessage> normalizedMessages){
		ArrayList<Item> items = new ArrayList<Item>();
		
		for(NormalizedMessage message : normalizedMessages){
			String ts = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSZ").format(new Date(message.getTs().longValue()));
			items.add(new Item(message.getMid(), ts));
		}
		
		return items;
	}

	/**
	 * Methods not required on this activity
	 */
	
	@Override
	public void onLoginCompleted(String accessToken) {
	}

	@Override
	public void onLoginCanceled() {
	}

	@Override
	public void onInvalidCredentials() {
	}

}
