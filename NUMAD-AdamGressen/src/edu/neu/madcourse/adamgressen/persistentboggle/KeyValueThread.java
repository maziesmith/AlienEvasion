package edu.neu.madcourse.adamgressen.persistentboggle;

import java.util.Date;
import edu.neu.mobileclass.apis.KeyValueAPI;
import android.os.AsyncTask;
import android.util.Log;

public class KeyValueThread extends AsyncTask<PersistentBoggleState, Void, PersistentBoggleState> {


	private static final String TEAM = "persistence";
	private static final String PASSWORD = "p3rs1st3nc3";


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected PersistentBoggleState doInBackground(PersistentBoggleState... states) {
		PersistentBoggleState state = states[0];

		if(state.getMode()== PersistentBoggleState.GET_MODE){

			Log.d("KEYVALUETHREAD", "GET_MODE");
			try {
				if (KeyValueAPI.isServerAvailable()) {
					String val = KeyValueAPI.get(TEAM, PASSWORD, state.getKey());
					if (val == "" || val==null){
						state.setValue(state.getDefval());
						return state;
					}
					else{
						state.setValue(val);
						return state;
					}

				}
				else
					return state;
			}
			catch (Exception e) {
				return state;
			}

		}
		else if(state.getMode() == PersistentBoggleState.SET_MODE){
			Log.d("KEYVALUETHREAD", "SET_MODE");
			try{
				if (KeyValueAPI.isServerAvailable()) {
					KeyValueAPI.put(TEAM, PASSWORD, state.getKey(), state.getValue());
					KeyValueAPI.put(TEAM, PASSWORD, PersistentBoggle.SERVER_WORLD_TIME_KEY, String.valueOf(new Date().getTime()));
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return state;
		}
		else if(state.getMode() == PersistentBoggleState.CLEAR_MODE){
			try{
				if (KeyValueAPI.isServerAvailable())
					KeyValueAPI.clearKey(TEAM, PASSWORD, state.getKey());
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		return state;
	}
	@Override
	protected void onPostExecute(PersistentBoggleState result) {
		super.onPostExecute(result);
		Log.d("KEYVALUETHREAD_OnPostExecute", result.getMode());
		if(result.getMode() == PersistentBoggleState.GET_MODE){
			switch(result.field){

			case BOARD:
				result.getCallable().setBoard(result.getValue());
				break;
			case OPPONENT:
				result.getCallable().setOpponent(result.getValue());
				break;
			case SCORE:
				result.getCallable().setScore(Integer.parseInt(result.getValue()));
				break;
			case USERID:
				result.getCallable().setUserID(result.getValue());
				break;
			case REMOTETIME:
				result.getCallable().setRemoteTime(Long.valueOf(result.getValue()));
				Log.d("KEYVALUETHREAD_PostExecute", "Setting the remote time as: " + result.getValue());
				break;
			case SERVERTIME:
				result.getCallable().setTime(Integer.parseInt(result.getValue()));
				break;
			case USEDWORDS:
				result.getCallable().setUsedWordString(result.getValue());
				break;
			default:

			}
		}
	}


}
