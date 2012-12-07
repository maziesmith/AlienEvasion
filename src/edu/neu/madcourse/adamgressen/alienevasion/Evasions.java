package edu.neu.madcourse.adamgressen.alienevasion;


import java.util.List;

import edu.neu.madcourse.adamgressen.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Evasions extends ListActivity {
	
	List<String> storedEvasions;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		readStoredEvasions();
		String currentEvasion = getSavedEvasionName();
		storedEvasions.remove(currentEvasion);
		
		final CharSequence[] options={"View Score","Delete"};
		
		final Context context = this;
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.alien_evasion_evasions, storedEvasions));
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
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
				
			}

		});
		


	}
	
	private String getSavedEvasionName(){
		return new StoredEvasion().getSavedEvasionName(this);
	}

	private void readStoredEvasions() {
		
		List<String> evasions = new StoredEvasion().readAll(this);
		storedEvasions = evasions;
	}
	
}
