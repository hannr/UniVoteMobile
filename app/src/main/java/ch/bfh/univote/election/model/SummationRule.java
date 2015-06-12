package ch.bfh.univote.election.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.HashSet;
import java.util.Map;

import ch.bfh.univote.election.controllers.ElectionDataController;
import ch.bfh.univote.election.interfaces.Rule;

public class SummationRule implements Rule {

    private Context context;
    private int id;
    private String type;
    private HashSet<Integer> optionIds;
    private int lowerBound;
    private int upperBound;

    public SummationRule(Context context, int id, String type, JSONArray optionIds, int lowerBound, int upperBound) throws Exception {
        this.context = context;
        this.id = id;
        this.type = type;
        this.optionIds = new HashSet<>();
        for (int i = 0; i < optionIds.length(); i++) {
            this.optionIds.add(optionIds.getInt(i));
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public boolean isAllowed(Context context, int optionId) {
        // get number of current votes in sharedprefs which correspond to this rule

        if (this.optionIds.contains(optionId)) {
            int counter = 0;
            for(Map.Entry<String,?> entry : ElectionDataController.getInstance(context).getAllEntriesFromSharedPrefs()){
                // check if key is numeric
                if (isNumeric(entry.getKey())) {
                    // check if option corresponds to this rule
                    if (this.optionIds.contains(Integer.valueOf(entry.getKey()))) {
                        counter += (Integer) entry.getValue();
                    }
                }
            }
            if (counter >= this.lowerBound && counter < this.upperBound) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean optionIdsContainsId(int id) {
        return this.optionIds.contains(id);
    }

    private boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
