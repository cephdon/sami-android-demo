package io.samsungsami.androidclient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.samihub.androidclient.R;

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
import android.widget.Toast;

public class MessagesActivity extends Activity implements ActionBar.TabListener, SamiClient {
	public static final int SECTION_DETAILS = 1;
	static MessagesActivity instance;
	Sami sami;
	// Message list
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
		case SECTION_DETAILS:
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
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.messages_section1).toUpperCase(l);
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
			
			if(SECTION_DETAILS == section ){
				rootView = inflater.inflate(R.layout.fragment_messages, container,
						false);
				
				messagesListView = (ListView) rootView
						.findViewById(R.id.listViewMessages);
				
				instance.refreshMessagesData();
				
			} else {
				rootView = inflater.inflate(R.layout.fragment_sami, container,
						false);
				
				TextView textView = (TextView) rootView
						.findViewById(R.id.section_label);
				textView.setText(R.string.messages_section1);
			}
			return rootView;
		}
	}
	
	/**
	 * Sets the context of the Sami wrapper
	 */
	void initSami(){
		sami = Sami.setInstance(this, new SamiStack(Config.APP_ID, Config.REDIRECT_URI));
	} 
	
	/**
	 * Populates the message addapter and passes it to the UI
	 */
	void refreshMessagesData(){
		instance.initSami();
		if(messagesListView != null){
			final String uid = sami.getCredentials().getId();
			final String mid = getIntent().getExtras().getString("mid");
			
			new Call(new Code() {
				
				@Override
				public Object run() {
					List<NormalizedMessage> messages = null;
	    			try {
	    				NormalizedMessagesEnvelope envelope = sami.getMessagesQueryApi()
	    						.getNormalizedMessages( 
	    								uid, null, mid, null, null, null, null, null, null, null);
	    						
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
						GenericAdapter adapter = new GenericAdapter(MessagesActivity.this, generateMessagesData(normalizedMessages));
						messagesListView.setAdapter(adapter);
					} else {
						Toast.makeText(MessagesActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
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
			
			items.add(new Item("MessageID", message.getMid()));
			String ts = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSZ").format(new Date(message.getTs().longValue()));
			items.add(new Item("Timestamp", ts));
			items.add(new Item("Source Device", message.getSdid()));
			Map<String, ?> data = message.getData();
			Iterator entries = data.entrySet().iterator();
			while (entries.hasNext()) {
				Entry thisEntry = (Entry) entries.next();
				Object key = thisEntry.getKey();
				Object keyValue = thisEntry.getValue();
				items.add(new Item("Data."+key.toString(), keyValue.toString()));
			}
			
			
		}
		return items;
	}
	
	/**
	 * These methods are not required in this activity
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
