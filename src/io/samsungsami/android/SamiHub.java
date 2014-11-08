package io.samsungsami.android;

import io.samsungsami.api.DevicesApi;
import io.samsungsami.api.DeviceTypesApi;
import io.samsungsami.api.MessagesApi;
import io.samsungsami.api.UsersApi;

public class SamiHub {

	/**
	 * Returns authenticated Swagger API for users
	 * @param url
	 * @param token
	 * @return
	 */
    public static UsersApi getUsersApi (String url, String token){
        UsersApi api = new UsersApi();
        api.setBasePath(url);
        api.getInvoker().addDefaultHeader("Authorization", "bearer "+token);
        return api;
    }

    /**
     * Returns authenticated Swagger API for devices
     * @param url
     * @param token
     * @return
     */
    public static DevicesApi getDevicesApi (String url, String token){
        DevicesApi api = new DevicesApi();
        api.setBasePath(url);
        api.getInvoker().addDefaultHeader("Authorization", "bearer "+token);
        return api;
    }

    /**
     * Returns authenticated Swagger API for device types
     * @param url
     * @param token
     * @return
     */
    public static DeviceTypesApi getDevicetypesApi (String url, String token){
        DeviceTypesApi api = new DeviceTypesApi();
        api.setBasePath(url);
        api.getInvoker().addDefaultHeader("Authorization", "bearer "+token);
        return api;
    }

    /**
     * Returns authenticated Swagger API for messages
     * @param url
     * @param token
     * @return
     */
    public static MessagesApi getMessagesApi (String url, String token){
        MessagesApi api = new MessagesApi();
        api.setBasePath(url);
        api.getInvoker().addDefaultHeader("Authorization", "bearer "+token);
        return api;
    }

}
