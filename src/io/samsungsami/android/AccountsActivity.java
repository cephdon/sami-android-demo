package io.samsungsami.android;

import com.samihub.androidclient.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class AccountsActivity extends Activity {
	public static final String TAG = AccountsActivity.class.getName();
	RelativeLayout layout;
	private AccountsWebView mWebView;
	MenuItem loader;
	private boolean loginCompleted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accounts);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				loadWebView();
			}
		}, 300);
	}
	
	/**
	 * Inserts the webview in the current layout
	 */
	public void loadWebView(){
		loginCompleted = false;
		layout = (RelativeLayout) findViewById(R.id.rootElement);
		FrameLayout.LayoutParams rl= new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, 
				FrameLayout.LayoutParams.MATCH_PARENT);
	    mWebView = new AccountsWebView(this);
	    mWebView.setId(0X100);
	    mWebView.setLayoutParams(rl);
	    layout.addView(mWebView);
	    
	    String url = getIntent().getExtras().getString("url");
		mWebView.clearCache(true);
		mWebView.loadUrl(url);
		Log.d(TAG, "AccountsActivity: "+url);
	}
	
	/**
	 * Exits sucessfully
	 */
	public void onLoginCompleted(String accessToken){
		loginCompleted = true;
		Sami.getInstance().onLoginCompleted(accessToken);
		finish();
	}
	
	/**
	 * Exits with cancel condition
	 */
	public void onLoginCanceled(){
		finish();
	}

	/**
	 * Context menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.accounts, menu);
		
		loader = menu.findItem(R.id.loading_indicator);
        
		return true;
	}
	
	/**
	 * Controls the loader animation
	 * @param loading
	 */
	public void setLoading(boolean loading){
		if(loading){
			loader.setActionView(R.layout.loading_indicator);
		}
		else{
			loader.setActionView(null);
		}
	}

	/**
	 * Menu handler
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_cancel) {
			onLoginCanceled();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_accounts,
					container, false);
			return rootView;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Propagates the login cancelled if there was no success
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(!loginCompleted){
			Sami.getInstance().onLoginCanceled();
		}
	}

}
