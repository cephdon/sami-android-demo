package io.samsungsami.androidclient;

import com.samihub.androidclient.R;

import io.samsungsami.android.Sami;
import io.samsungsami.android.SamiClient;
import io.samsungsami.android.SamiStack;
import io.samsungsami.android.api.Call;
import io.samsungsami.android.api.Callback;
import io.samsungsami.android.api.Code;
import io.samsungsami.client.ApiException;
import io.samsungsami.model.Device;
import io.samsungsami.model.DeviceEnvelope;
import io.samsungsami.model.DeviceType;
import io.samsungsami.model.DeviceTypeArray;
import io.samsungsami.model.DeviceTypesEnvelope;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements SamiClient {
	static RegisterActivity instance;
	Sami sami;
	
	// UI members
	static ArrayAdapter<String> spinnerArrayAdapter;
	static Spinner spinnerDeviceType;
	static TextView textDeviceName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
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
			View rootView = inflater.inflate(R.layout.fragment_register,
					container, false);
			return rootView;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initSami();
	}
	
	/**
	 * Sets SAMI client context and some UI vars
	 */
	void initSami(){
		sami = Sami.setInstance(this, new SamiStack(Config.APP_ID, Config.REDIRECT_URI));
		spinnerDeviceType = (Spinner) findViewById(R.id.deviceType);
		textDeviceName = (TextView) findViewById(R.id.deviceName); 
		getDeviceTypesIntoSpinner();
	} 
	
	/**
	 * Populates the spinner of device types
	 */
	void getDeviceTypesIntoSpinner(){
		
		new Call(new Code() {
			
			@Override
			public Object run() {
				DeviceTypeArray deviceTypeArray = null;
    			try {
    				DeviceTypesEnvelope envelope = sami.getUsersApi()
    						.getUserDeviceTypes(0, 100, true, sami.getCredentials().getId());
    				deviceTypeArray = envelope.getData();
    			} catch (ApiException e) {
    				e.printStackTrace();
    			}
    			return deviceTypeArray;
			}
		}, new Callback() {
			
			@Override
			public void onApiResult(Object result) {
				DeviceTypeArray deviceTypeArray = (DeviceTypeArray) result;
				
				String[] spinnerArray = new String[deviceTypeArray.getDeviceTypes().size()];
				int i = 0;
				for (DeviceType deviceType : deviceTypeArray.getDeviceTypes()){
					spinnerArray[i++] = deviceType.getUniqueName() + "|" + deviceType.getId();
				}
				spinnerArrayAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, spinnerArray); 
				spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerDeviceType.setAdapter(spinnerArrayAdapter);
			}
		}).execute();
		
	}

	/**
	 * Called from the OK button, as specified in the fragment XML
	 * if creation results ok, closes this activity and refreshes the parent
	 * @param v
	 */
	public void createDevice(View v){
		final String dtid = spinnerDeviceType.getSelectedItem().toString().split("\\|")[1];
		final String name = textDeviceName.getEditableText().toString();
		final String uid = sami.getCredentials().getId();
		
		new Call(new Code() {
			
			@Override
			public Object run() {
				io.samsungsami.model.Device device = new io.samsungsami.model.Device();
				device.setDtid(dtid);
				device.setName(name);
				device.setUid(uid);
				try {
    				DeviceEnvelope envelope = sami.getDevicesApi()
    						.addDevice(device);
    				device = envelope.getData();
    			} catch (ApiException e) {
    				e.printStackTrace();
    			}
    			return device;
			}
		}, new Callback() {
			
			@Override
			public void onApiResult(Object result) {
				Device device = (Device) result;
				if(device != null) {
					Toast.makeText(RegisterActivity.this, "Created a "+device.getDtid()+" device with ID: "+device.getId(), Toast.LENGTH_LONG).show();
					SamiActivity.instance.getDevicesIntoAdapter();
					SamiActivity.instance.setDevicesListAdapter();
					finish();
				} else{
					Toast.makeText(RegisterActivity.this, "Error creating device.", Toast.LENGTH_LONG).show();
				}
			}
		}).execute();
		
		
	}

	/**
	 * Methods not required in this activity
	 */
	
	@Override
	public void onLoginCompleted(String accessToken) {
	}

	@Override
	public void onLoginCanceled() {
	}

	@Override
	public void onInvalidCredentials() {
	}

}
