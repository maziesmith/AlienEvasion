package edu.neu.madcourse.adamgressen.alienevasion;


import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.neu.madcourse.adamgressen.R;

public class Evasions extends ListActivity {
	
	List<String> storedEvasions;
	String[] storedArray;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		readStoredEvasions();
		String currentEvasion = getSavedEvasionName();
		storedEvasions.remove(currentEvasion);
		
		storedArray = new String[storedEvasions.size()];
		for(int i = 0;i<storedEvasions.size();i++){
			storedArray[i] = storedEvasions.get(i);
		}
		
		formatEvasions();
		
		final CharSequence[] options={"View Score","Delete"};
		
		final Context context = this;
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.alien_evasion_evasions, storedArray));
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, final long id) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Make your selection");
				builder.setItems(options, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				         // Do something with the selection
				    	
				    	switch(item){
				    	case 0:
							

				    		break;
				    	case 1:
				    		break;
				    	}
				    	
				    	}
				    		
				    });
				AlertDialog alert = builder.create();
				alert.show();
				
				return true;
			}
		
		
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(context, SavedEvasions.class);
				Bundle b = new Bundle();
				b.putString("EVASION_NAME", storedEvasions.get((int)id));
				intent.putExtras(b);
				startActivity(intent);
				
			}

		});
		


	}
	
	private void formatEvasions() {
		int index;
		for(int i = 0; i<storedArray.length;i++){
			
			if((index = storedArray[i].indexOf('@')) != -1){
				String time = storedArray[i].substring(index+1).replace("@", ":");
				String ntime = time.substring(0,time.lastIndexOf(":")) + time.substring(time.lastIndexOf(":")+1);
				String evasion = storedArray[i].substring(0, index).replace("-", "/") + "   " +  ntime;
				System.out.println(evasion);
				storedArray[i] = evasion;
			}
				
		}
		
	}

	private String getSavedEvasionName(){
		return new StoredEvasion().getSavedEvasionName(getApplicationContext());
	}

	private void readStoredEvasions() {
		
		List<String> evasions;
		try {
			evasions = new StoredEvasion().readAll(this);
			storedEvasions = evasions;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
