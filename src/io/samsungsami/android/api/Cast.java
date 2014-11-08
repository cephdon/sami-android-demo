package io.samsungsami.android.api;

public class Cast {
	
	/**
	 * Serves as conversion between the Object based Api Task
	 * and the models from the Swagger client
	 * @param clazz
	 * @param result
	 * @return
	 */
	public static <T> T as(Class<T> clazz, Object result){
		try {
			if(result != null){
				return (T) result;
			} else {
				return null;
			}
		} catch (ClassCastException e){
			e.printStackTrace();
			return null;
		}
	}
}
