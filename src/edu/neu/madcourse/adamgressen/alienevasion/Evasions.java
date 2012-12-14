package edu.neu.madcourse.adamgressen.alienevasion;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		storedEvasions = readStoredEvasions(getApplicationContext());
		String currentEvasion = getSavedEvasionName(getApplicationContext());
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
		listView.setBackgroundColor(0x000000);


		setListAdapter(new ArrayAdapter<String>(this, R.layout.alien_evasion_evasions, storedArray));

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, final long id) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Make your selection");
				builder.setItems(options, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// Do something with the selection

						String fileName = storedEvasions.get(position);
						
						switch(item){
						case 0:
							int score = getStoredEvasion(fileName);
							Toast.makeText(context,
									"Score: " + score, Toast.LENGTH_SHORT).show();
							finish();
							break;
						case 1:
							deleteEvasion(fileName);
							Toast.makeText(context,
									"File Deleted!", Toast.LENGTH_SHORT).show();
							finish();
							break;
						}

					}

					private void deleteEvasion(String fileName) {

						new StoredEvasion(fileName).deleteEvasion(getApplicationContext());
						
					}

					private int getStoredEvasion(String fileName) {
						
						StoredEvasion se = new StoredEvasion(fileName).read(getApplicationContext());
						return se.totalEvaded;
						
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

	public static String getSavedEvasionName(Context context){
		return new StoredEvasion().getSavedEvasionName(context);
	}

	public static List<String> readStoredEvasions(Context context) {

		List<String> evasions;
		try {
			evasions = new StoredEvasion().readAll(context);
			return evasions;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Sounds.playMusic(this, R.raw.alien_evasion_main);
	}

	@Override
	protected void onPause() {
		super.onPause();

		Sounds.stop(this);
	}

}
