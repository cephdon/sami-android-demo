package io.samsungsami.android.api;

import java.util.concurrent.ExecutionException;


public class Call {
	
	private Code code;
	private Callback callback;
	
	public Call(final Code code){
		this.code = code;
		this.callback = null;
	}
	
	public Call(Code code, Callback callback){
		this.code = code;
		this.callback = callback;
	}
	
	/**
	 * Async mode. Executes callback code after the task is finished
	 */
	public void execute(){
		new Task(code, callback).execute();
	}
	
	/**
	 * Sync mode. Blocking call until it returns the result
	 * @return
	 */
	public Object executeInSync(){
		try {
			return new Task(code).execute().get();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
}
