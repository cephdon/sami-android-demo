package io.samsungsami.android.api;

import android.os.AsyncTask;
import android.util.Log;

public class Task extends AsyncTask<Void, Void, Object> {
	public static final String TAG = Task.class.getName();
	Code code;
	Callback callback;
	
	public Task(final Code code){
		this.code = code;
		this.callback = null;
	}
	
	public Task(final Code code, Callback callback){
		this.code = code;
		this.callback = callback;
	}
    
	@Override
	protected void onCancelled() {
		Log.d(TAG, "Async task cancelled.");
		super.onCancelled();
	}

	@Override
	protected Object doInBackground(Void... params) {
		Object ret = null;
		if(code != null){
			ret = code.run();
		} 
		return ret;
	}	
	 
    /**
     * Sends callback to the context activity
     * @param result
     */
    protected void onPostExecute(Object result) {
    	if(callback != null){
    		callback.onApiResult(result);
    	}
    }
}
