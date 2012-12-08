package edu.neu.madcourse.adamgressen.alienevasion;

import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

import edu.neu.madcourse.adamgressen.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class AchAdapter extends ArrayAdapter<String>{
	
	List<String> unlocks;
	Context context;
	String[] achievementarray;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
        

            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.achievement_item_list, null);
        
        String item = achievementarray[position];

        
        View rowview = vi.inflate(R.layout.achievement_item_list, parent,false);
        //rowview.getBackground().setAlpha(120);
        rowview.setEnabled(true);
            //checkbox
            TextView tx = (TextView) v.findViewById(R.id.label);
            CheckBox cbEnabled = (CheckBox)v.findViewById(R.id.checkBox);
            cbEnabled.setClickable(false);
            if(cbEnabled != null){
            	tx.setText(item);
            	if(unlocks.contains(achievementarray[position]))
            		cbEnabled.setChecked(true);
            	else 
            		cbEnabled.setChecked(false);

            }
                
        return v;
	}

	
	public AchAdapter(Context context,
			int simpleListItemMultipleChoice, String[] objects) {
		super(context, simpleListItemMultipleChoice, objects);
		
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public void setUnlocledList(List<String> unlocks,String[] achievementarray){
		
		this.unlocks = unlocks;
		this.achievementarray = achievementarray;
	}


	
	
	
	


	
	

}
