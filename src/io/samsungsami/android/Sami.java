package io.samsungsami.android;

import io.samsungsami.android.api.Call;
import io.samsungsami.android.api.Code;
import io.samsungsami.android.api.Cast;
import io.samsungsami.api.DeviceTypesApi;
import io.samsungsami.api.DevicesApi;
import io.samsungsami.api.MessagesApi;
import io.samsungsami.api.UsersApi;
import io.samsungsami.client.ApiException;
import io.samsungsami.model.User;
import io.samsungsami.model.UserEnvelope;

import sami.utils.FileUtils;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Sami implements SamiClient {
	public static final String TAG = Sami.class.getName();
	private static final Sami instance = new Sami();
	private static Context context;
	private static final String BEARER_FILE = "session.data";
	private static boolean isVisible;
	private static boolean isRunningInBackground = false;
	private static Credentials credentials;
	private static SamiStack samiStack;
	
	/**
	 * This is now a single instance class
	 */
	private Sami(){}
	
	/**
	 * Sets the configuration for the SAMI auth instance
	 * @param context the activity o service implementing SamiClient interface
	 * @param samiStack the Sami server configuration object
	 * @return an object that lets you interact with SAMI
	 */
	public static Sami setInstance(Context context, SamiStack samiStack){
		getInstance().setSamiStack(context, samiStack);
		return getInstance();
	} 
	
	/**
	 * Changes the configuration an reload the credentials for the current instance
	 * @param context the activity o service implementing SamiClient interface
	 * @param samiStack the Sami stack configuration object
	 */
	private void setSamiStack(Context context, SamiStack samiStack){
		if(Service.class.isAssignableFrom(context.getClass())){
			isRunningInBackground = true;
		}
		Sami.context = context;
		Sami.samiStack = samiStack;
		if(!loadCredentials()){
			Log.d(TAG, "Credentials and token are not set.");
		}
	}
	
	/**
	 * Saves the current credentials to a file
	 * Useful to make them persist between sessions
	 * @return
	 */
	public boolean saveCredentials(){
		return FileUtils.savePrivateFile(context, BEARER_FILE, 
				Credentials.toJson(credentials));
	}
	
	/**
	 * Removes credentials file from device
	 * @return
	 */
	public boolean deleteCredentials(){
		credentials = null;
		return FileUtils.deletePrivateFile(context, Sami.BEARER_FILE);
	}
	
	/**
	 * Loads the current stored credentials
	 * @return
	 */
	public boolean loadCredentials(){
		credentials = null;
		String fileContent = FileUtils.readPrivateFile(context, BEARER_FILE);
		if(fileContent != null){
			credentials = Credentials.fromJson(fileContent);
		}
		return (credentials != null);
	}
	
	/**
	 * Sets the desired credentials, and saves them to persist
	 * @param credentials
	 */
	public void setCredentials(Credentials credentials){
		Sami.credentials = credentials;
		saveCredentials();
		loadCredentials();
	}

	/**
	 * Gets the current logged in user credentials
	 * @return
	 */
	public Credentials getCredentials(){
		return credentials;
	}
	
	/**
	 * Gets the current logged in user 
	 * @return
	 */
	public User getUser(){
		User user = null;
		if(credentials != null){
			user = userSelf(credentials.getToken());
		}
		return user;
	}
	
	/**
	 * Gets the current access token
	 * @return
	 */
	public String getToken(){
		if(credentials != null){
			return credentials.getToken();
		} else {
			return null;
		}
	}
	
	/**
	 * Displays the login activity, controls flood of login windows
	 * Calls onLoginCompleted after login
	 * Calls onLoginCancelled if user cancels the dialog
	 */
	public void login(){
		if(isVisible){
			return;
		}
		isVisible = true;
		String url = samiStack.getAccountsUrl()+samiStack.getLoginUrl();
		
		Intent intent = new Intent(context, AccountsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("url", url);
		context.startActivity(intent);
	}
	
	/**
	 * Triggered after a successful login
	 * Obtains the User model using the new access token
	 */
	@Override
	public void onLoginCompleted(final String accessToken) {
		User user = userSelf(accessToken);
		if(user != null){
			setCredentials(new Credentials(accessToken, 
											user.getId(), 
											user.getName(), 
											user.getEmail(), 
											AccountsWebView.getLatestPassword()
											));
			((SamiClient)context).onLoginCompleted(accessToken);
		} else {
			((SamiClient)context).onInvalidCredentials();
		}
	}
	
	/**
	 * Called from the webview if the screen is cancelled
	 */
	public void onLoginCanceled() {
		isVisible = false;
		((SamiClient)context).onLoginCanceled();
	}
	
	/**
	 *  Returns true if session and token is still valid, sync call. 
	 */
    public boolean hasValidToken() {
    	return (getUser() != null);
    }
    
	/**
	 *  Returns User model if token is valid, sync call. 
	 */
    public User userSelf(final String accessToken) {
    	
    	Object result = new Call(new Code() {
			
			@Override
			public Object run() {
				UsersApi usersApi = SamiHub.getUsersApi(samiStack.getGaiaUrl(), accessToken);
    			try {
    				UserEnvelope userEnvelope = usersApi.self();
    				User user = userEnvelope.getData();
    				return user;
    			} catch (ApiException e) {
    				e.printStackTrace();
    			}
    			return null;
			}
		}).executeInSync();
    	
    	return Cast.as(User.class, result);
    	
    }

    /**
     * Triggered if access token is not valid anymore
     */
	@Override
	public void onInvalidCredentials() {
		((SamiClient)context).onInvalidCredentials();
	}

	/**
	 * Returns whether the login for is currently displayed or not 
	 * @return
	 */
	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		Sami.isVisible = isVisible;
	}
	
	/**
	 * Returns current configuration
	 * @return
	 */
	public SamiStack getSamiStack(){
		return samiStack;
	}

	/**
	 * Returns the context which is binded to this library instance
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Returns if the dialogs will popup from a background process
	 * @return
	 */
	public boolean isRunningInBackground() {
		return isRunningInBackground;
	}

	/**
	 * Sets if the dialogs should popup with special permissions
	 * to be displayed from a background process
	 * @param isRunningInBackground
	 */
	public void setRunningInBackground(boolean isRunningInBackground) {
		Sami.isRunningInBackground = isRunningInBackground;
	}

    /**
     * Returns the default instance of the authentication library
     * @return
     */
    public static Sami getInstance(){
    	return Sami.instance;
    }
	
    /**
     * For now this removes client side credentials
     * @return
     */
    public boolean logout(){
    	return deleteCredentials();
    }
    
    /**
     * Gets Chronos API invoker for current configuration
     * @return
     */
    public MessagesApi getMessagesQueryApi(){
    	return SamiHub.getMessagesApi(samiStack.getChronosUrl(), getToken());
    }
    
    /**
     * Gets Connectors API invoker for current configuration
     * @return
     */
    public MessagesApi getMessagesPostApi(){
    	return SamiHub.getMessagesApi(samiStack.getConnectorsUrl(), getToken());
    }
    
    /**
     * Gets Gaia API invoker for current configuration
     * @return
     */
    public UsersApi getUsersApi(){
    	return SamiHub.getUsersApi(samiStack.getGaiaUrl(), getToken());
    }
    
    /**
     * Gets Gaia API invoker for current configuration
     * @return
     */
    public DevicesApi getDevicesApi(){
    	return SamiHub.getDevicesApi(samiStack.getGaiaUrl(), getToken());
    }
    
    /**
     * Gets Gaia API invoker for current configuration
     * @return
     */
    public DeviceTypesApi getDeviceTypesApi(){
    	return SamiHub.getDevicetypesApi(samiStack.getGaiaUrl(), getToken());
    }

}
