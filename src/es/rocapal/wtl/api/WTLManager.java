/*
 *
 *  Copyright (C) Roberto Calvo Palomino
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Roberto Calvo Palomino <rocapal at gmail dot com>
 *
 */

package es.rocapal.wtl.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import es.rocapal.utils.Download.HTTPActions;

public class WTLManager {

	private final String TAG = getClass().getSimpleName();
	private final String CODE_KEY = "code";
	private final String MSG_KEY = "message";
	
	private static WTLManager mInstance = null;
	private String mIp;
	private Integer mPort;
	private String mUriRest;
	
	private WTLManager() {}

	public synchronized static WTLManager getInstance()
	{
		if (mInstance == null)
			mInstance = new WTLManager();
		
		return mInstance;
	}
	
	public void setAddress (String ip, Integer port)
	{
		mIp = ip;
		mPort = port;
		
		mUriRest = "http://" + mIp + ":" + mPort.toString() + "/api"; 
	}
	
	public Response getStatus()
	{
		String jsonResponse = HTTPActions.doGetPetition(mUriRest + "/status");
		return parseResponse(jsonResponse);
	}
	
	public Response start()
	{
		String jsonResponse = HTTPActions.doGetPetition(mUriRest + "/start");
		return parseResponse(jsonResponse);
	}
	
	public Response stop()
	{
		String jsonResponse = HTTPActions.doGetPetition(mUriRest + "/stop");
		return parseResponse(jsonResponse);
	}
	
	private Response parseResponse (String jsonResponse)
	{
		JSONObject json;
		
		if (jsonResponse == null)
			return null;
			
		try {
			json = new JSONObject(jsonResponse);
			if (json.has(CODE_KEY) && json.has(MSG_KEY))
				return new Response( json.getInt(CODE_KEY), json.getString(MSG_KEY));
			else
				return null;
			
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
		
		
		
	}
	
}
