package io.samsungsami.android;



public interface SamiClient {
	/**
	 * Called when a successful login is done, the login window is dismissed and the auth token has been obtained.
	 */
	void onLoginCompleted(String accessToken);
	
	/**
	 * Called when the login window is dismissed by cancel button, touching outside, back or any other reason.
	 */
	void onLoginCanceled();
	
	/**
	 * Called when the API Call hits a 401 unauthorized response header
	 */
	void onInvalidCredentials();

}
