package io.samsungsami.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class AccountsWebView extends WebView{
	
	public static final String TAG = AccountsWebView.class.getName();
    private static AccountsActivity accountsActivity;
    private static String latestUsername="";
    private static String latestPassword="";
    
    /**
	 * Don't use this constructor, here to remove a warning
	 * and will intentionally throw exception 
	 * @param context
	 */
    public AccountsWebView(Context context){
    	super(context);
    	throw new AssertionError();
    }
    
    /**
     * Creates a webview that is able to catch the Accounts credentials
     * @param oauth
     */
    @SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public AccountsWebView(AccountsActivity accountsActivity) {
    	super(accountsActivity);
    	
    	AccountsWebView.accountsActivity = accountsActivity;
        
    	setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus(View.FOCUS_DOWN);
        CookieManager.getInstance().removeAllCookie();
        CookieManager.getInstance().setAcceptCookie(true);
        WebSettings webSettings = getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setAppCacheEnabled(false);
        //this.setDescendantFocusability(WebView.FOCUS_BLOCK_DESCENDANTS);
        addJavascriptInterface(new JavascriptToAndroidInterface(), "myInterface"); 
        
        setWebViewClient(new AccountsWebClient());
        
        setWebChromeClient(new WebChromeClient() {
	        
        	public void onProgressChanged(WebView view, int progress)
	        {
        		if(progress == 0) {
	            	AccountsWebView.accountsActivity.setLoading(true);
	            } else if(progress == 100) {
	            	AccountsWebView.accountsActivity.setLoading(false);
	            }
	        }

	        @Override
	        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
	            result.confirm();
	            return true;
	        }
	    });
    }

    @Override
    public boolean onCheckIsTextEditor(){
        return true;
    }
    
    /**
     * This bugfixes android not displaying soft keyboard when touching a textbox
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev){
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                if (!hasFocus())
                    requestFocus();
            break;
        }

        return super.onTouchEvent(ev);
    }
    
    public void onLoginCompleted(String accessToken){
    	accountsActivity.onLoginCompleted(accessToken);
    }
	
    /**
     * Expose Java interface to Accounts webpage
     *
     */
    class JavascriptToAndroidInterface   
	{  
		@JavascriptInterface
	    public void setCredentials(String username, String password){  
			if(sanitize(username) != null && sanitize(password) != null){
				
				latestUsername = username;
				latestPassword = password;
				
			}
	    }
		
		/**
		 * Dejavascriptize value
		 * @param value
		 * @return
		 */
		public String sanitize(String value){
			if(value != null && (!value.equalsIgnoreCase("undefined")) && value.length() > 0){
				return value;
			}
			else{
				return null;
			}
		}
	}

    /**
     * Method useful to store the credentials after users/self
     * @return
     */
	public static String getLatestPassword() {
		return latestPassword;
	}
	
	/**
     * Method useful to store the credentials after users/self
     * @return
     */
	public static String getLatestUsername() {
		return latestUsername;
	}  
	
}
