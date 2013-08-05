/*  Copyright (C) Roberto Calvo Palomino
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

package es.rocapal.app.wtl;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import es.rocapal.wtl.api.Response;
import es.rocapal.wtl.api.WTLManager;

public class WTLMainFragment extends Fragment {
	
	private final String TAG = getClass().getSimpleName();
	
	private View myView;
	private Switch mSwitch;
	private TextView mTvStatus;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);	
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		myView = inflater.inflate(R.layout.wtl_main, container, false);
		mSwitch	= (Switch) myView.findViewById(R.id.wtlcontrol);
		mTvStatus = (TextView) myView.findViewById(R.id.tv_wtlstatus); 
				
		
		
		return myView;
	}
	
	
	@Override
	public void onResume() 
	{			
		super.onResume();	

		WTLManager.getInstance().setAddress("10.0.0.1", 8001);
		WTLAsyncTask task = new WTLAsyncTask();
		task.setPhase(task.STATUS);
		task.execute();
				
		mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				WTLAsyncTask task = new WTLAsyncTask();
				task.setPhase(task.STATUS);
				
				if (isChecked)
					task.setPhase(task.START);
				else
					task.setPhase(task.STOP);
				
				task.execute();
					
			}
		});
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}
	
	public class WTLAsyncTask extends AsyncTask<Void, Void, Response>
	{
		Integer STATUS = 0;
		Integer START  = 1;
		Integer STOP   = 2;
		
		ProgressDialog mPd = null;
		
		private Integer mPhase = 0;
		
		public void setPhase(Integer phase)
		{
			mPhase = phase;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			if (mPhase == START)
				mPd = ProgressDialog.show(getActivity(), getString(R.string.pd_wtl_title), getString(R.string.pd_wtl_msg_start));
			else if (mPhase == STOP)
				mPd = ProgressDialog.show(getActivity(), getString(R.string.pd_wtl_title), getString(R.string.pd_wtl_msg_stop));
		}
		
		@Override
		protected Response doInBackground(Void... params) {
			
			Response response = null;
			
			if (mPhase == STATUS)
				response = WTLManager.getInstance().getStatus();
			else if (mPhase == START)
				response = WTLManager.getInstance().start();
			else if (mPhase == STOP)
				response = WTLManager.getInstance().stop();

			
			return response;
		}
		
		@Override
		protected void onPostExecute(Response result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (mPd != null && mPd.isShowing())
				mPd.dismiss();
			
			if (result != null)
			{
				mTvStatus.setText(result.getMessage());
				
				if (mPhase == STATUS)
					mSwitch.setChecked(result.getCode() == 1);
			}
			else
				Toast.makeText(getActivity(), getString(R.string.toast_wtl_error), Toast.LENGTH_LONG).show();
		}
	}
}