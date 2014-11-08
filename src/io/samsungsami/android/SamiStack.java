package io.samsungsami.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SamiStack {
	/**
	 * Default stack configuration
	 */
	private String clientId = "";
	private String redirectUri = "";
	private String connectorsUrl = "https://api.samsungsami.io/v1.1";
	private String accountsUrl = "https://accounts.samsungsami.io";
	private String gaiaUrl= "https://api.samsungsami.io/v1.1";
	private String chronosUrl= "https://api.samsungsami.io/v1.1";
	private String websocketUrl = "wss://api.samsungsami.io/v1.1";
	private String echoUrl = "wss://api.samsungsami.io/v1.1";
	// ---------------------------------------------------------------
	private String ACCOUNTS_QUERY_STRING_WEB_LOGIN = "";
	private String ACCOUNTS_QUERY_STRING_HIDDEN_LOGOUT= "";
	
	/**
	 * Creates an object that represents the SAMI stack
	 * This constructor is PROD-only
	 * @param clientId
	 * @param redirectUri
	 */
	public SamiStack(String clientId, String redirectUri){
		this.clientId = clientId;
		this.redirectUri = redirectUri;
		init();
	}
	
	/**
	 * Creates an object that represents the SAMI stack
	 * You need to use this constructor on LOCALHOST because Connectors, 
	 * Chronos, Echo and Gaia run on diferent ports
	 * @param clientId unique app id for SAMI Accounts service
	 * @param redirectUri URL redirection after login for this application, as specified in SAMI server
	 * @param connectorsUrl the URL to the API, example https://someserver/v1.1
	 * @param accountsUrl the URL to access Accounts web service
	 * @param gaiaUrl the base URL to access the internal API. 
	 * @param chronosUrl the base URL to access the Chronos API.
	 * @param websocketUrl the Connectors base URL for websocket connections
	 * @param echoUrl the Echo base URL for websocket connections.
	 */
	public SamiStack(String clientId, 
			String redirectUri, 
			String connectorsUrl, 
			String accountsUrl, 
			String gaiaUrl,
			String chronosUrl,
			String websocketUrl,
			String echoUrl){
		this.clientId = clientId;
		this.redirectUri = redirectUri;
		this.connectorsUrl = connectorsUrl;
		this.accountsUrl = accountsUrl;
		this.websocketUrl = websocketUrl;
		this.gaiaUrl = gaiaUrl;
		this.chronosUrl = chronosUrl;
		this.echoUrl = echoUrl;
		init();
	}
	
	/**
	 * Sets internal URLs
	 */
	void init(){
		ACCOUNTS_QUERY_STRING_WEB_LOGIN = "/authorize?response_type=token&client_id="+clientId+"&client=mobile";
		try {
			ACCOUNTS_QUERY_STRING_HIDDEN_LOGOUT = "/logout?redirect_uri="+URLEncoder.encode(redirectUri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public String getLoginUrl() {
		return ACCOUNTS_QUERY_STRING_WEB_LOGIN;
	}
	
	public String getLogoutUrl(){
		return ACCOUNTS_QUERY_STRING_HIDDEN_LOGOUT;
	}
	
	public String getClientId() {
		return clientId;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public String getConnectorsUrl() {
		return connectorsUrl;
	}
	
	/**
	 * Use this getter if you don't need to connect to localhost
	 * @return
	 */
	public String getUrl() {
		return gaiaUrl;
	}
	
	public String getGaiaUrl() {
		return gaiaUrl;
	}

	public String getAccountsUrl() {
		return accountsUrl;
	}

	public String getWebsocketUrl() {
		return websocketUrl;
	}
	
	public String getChronosUrl() {
		return chronosUrl;
	}
	
	public String getLiveUrl() {
		return echoUrl;
	}

}
