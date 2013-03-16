package com.zsoft.SignalA.Hubs;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.zsoft.SignalA.SendCallback;

public class HubProxy implements IHubProxy {
    private String mHubName;
    private HubConnection mConnection;
    //private Map<string, JSONObject> _state = new Dictionary<string, JToken>(StringComparer.OrdinalIgnoreCase);
    private Map<String, HubOnDataCallback> mSubscriptions = new HashMap<String, HubOnDataCallback>();

	public HubProxy(HubConnection hubConnection, String hubName) {
		mConnection = hubConnection;
		mHubName = hubName;
	}

	
	// Executes a method on the server asynchronously
	@Override
	public void Invoke(String method, JSONObject args,
			HubInvokeCallback callback) {

		if (method == null)
        {
            throw new IllegalArgumentException("method");
        }

        if (args == null)
        {
            throw new IllegalArgumentException("args");
        }

        String callbackId = mConnection.RegisterCallback(callback);

        HubInvocation hubData = new HubInvocation(mHubName, method, args, callbackId);

        String value = hubData.Serialize();

        mConnection.Send(value, new SendCallback() 
        {
			@Override
			public void OnSent(CharSequence messageSent) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnError(Exception ex) {
				// TODO Cancel the callback
				
			}
        });
    }

	

	public void On(String eventName, HubOnDataCallback callback) 
	{
		Subscribe(eventName, callback);
	}
	
	
	public void Subscribe(String eventName, HubOnDataCallback callback)
	{
		if(eventName==null) throw new IllegalArgumentException("eventName can not be null");
		if(callback==null) throw new IllegalArgumentException("callback can not be null");

		if(!mSubscriptions.containsKey(eventName))
		{
			mSubscriptions.put(eventName, callback);
		}
	}
	
	// K�r event lokalt som anropas fr�n servern och som registrerats i ON-metod.
    public void InvokeEvent(String eventName, JSONObject args)
    {
        HubOnDataCallback subscription;
        if(mSubscriptions.containsKey(eventName))
        {
        	subscription = mSubscriptions.get(eventName);
            subscription.OnReceived(args);
        }
    }


}
