package io.samsungsami.android;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AccountsWebClient extends WebViewClient{
	
	public static final String TAG = AccountsWebClient.class.getName();
	private static final String FORM_SELECTOR = "document.querySelectorAll('form[id*=\"input\"], form[id*=\"signin\"]')[0].id";
	private static final String FORM_USERNAME_SELECTOR = "document.querySelectorAll('input[id*=\"User\"], input[id*=\"user\"]')[0].id";
	private static final String FORM_PASSWORD_SELECTOR = "document.querySelectorAll('input[id*=\"Pass\"], input[id*=\"pass\"]')[0].id";
	
	public AccountsWebClient(){}
	
	/**
	 * Everything is handled by the webview, true to handle download manually
	 */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
    	if(isAuthorizeUrl(url)) {
    		Log.d(TAG, "onPageFinished code page URL: "+url);
    		
    		String[] segments = url.split("=");
    		String accessToken = "";
    		for(int i = 0; i< segments.length; i++){
    			if(segments[i].contains("access_token")){
    				accessToken = segments[i+1];
    				break;
    			}
    		}
    		
    		//TODO: clear the window, but this triggers another page load event
    		//view.loadData("","text/plain","utf-8"); 
    		view.clearView();
    		String currentToken = Sami.getInstance().getToken();
    		if(Sami.getInstance().isVisible() && (currentToken == null 
    			|| !currentToken.equalsIgnoreCase(accessToken))){
    			Sami.getInstance().setVisible(false);
    			((AccountsWebView) view).onLoginCompleted(accessToken);
    		}
        }
    	return false;
    }
    
    /**
     * Guess if the url is the login form
     * @param url
     * @return
     */
    public boolean isLoginForm(String url){
    	return ((url.contains("response_type=") || url.contains("check.do")) && !url.contains("signin"));
    }
    
    /**
     * Guess if the url belongs to SA service
     * @param url
     * @return
     */
    public boolean isSALoginForm(String url){
    	return (url.contains("check.do") && !url.contains("signin"));
    }
    
    /**
     * Guess if the url is the one that contains the access token
     * @param url
     * @return
     */
    public boolean isAuthorizeUrl(String url){
		return url.contains("access_token=");
	}
    
    public void onPageFinished(WebView view, String url) {
    	onPageFinishedAccounts((AccountsWebView)view, url);
    }
    
    /**
     * Injects javascript to get the user credentials on the login form
     * @param view
     * @param url
     */
    @SuppressLint("NewApi")
	public void onPageFinishedAccounts(AccountsWebView view, String url) {
    	if (isLoginForm(url)){
    		String captureScript = 
    				"try{" +
	    				"document.getElementById("+FORM_SELECTOR+").onsubmit=function(){" +
	    					"var usernameFieldId = " + FORM_USERNAME_SELECTOR + ";" +
	    					"var passwordFieldId = " + FORM_PASSWORD_SELECTOR + ";" +
	    					"window.myInterface.setCredentials(" +
	            			"document.getElementById(usernameFieldId).value," +
	            			"document.getElementById(passwordFieldId).value" +
	            			");" +
	    				"};" +
    				"}catch(e){}";
        	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { 
        		view.loadUrl("javascript:"+captureScript);
        	}
        	else{
        		view.evaluateJavascript(captureScript, null);
        	}
    		
    		//Try to auto fill the login form
    		String username = "";
    		String password = "";
    		if(Sami.getInstance().getCredentials() != null){
    			username = Sami.getInstance().getCredentials().getEmail();
    			password = Sami.getInstance().getCredentials().getPassword();
    		}
    		String setScript = "try{" +
    				"var usernameFieldId = " + FORM_USERNAME_SELECTOR + ";" +
					"var passwordFieldId = " + FORM_PASSWORD_SELECTOR + ";" +
    				"document.getElementById(usernameFieldId).value = '"+username+"';" +
    				"document.getElementById(passwordFieldId).value = '"+password+"';" +
    				"}catch(e){}";
    		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { 
    			view.loadUrl("javascript:"+setScript); 
    		}
    		else{
    			view.evaluateJavascript(setScript, null);
    		}
    		
    	}
    	else{
    		Log.d(TAG, "onPageFinished URL: "+url);
    	}
    	
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus(View.FOCUS_DOWN);
    }

    
}
