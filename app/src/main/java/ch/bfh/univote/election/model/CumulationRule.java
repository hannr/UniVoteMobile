package ch.bfh.univote.election.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.HashSet;

import ch.bfh.univote.election.controllers.ElectionDataController;
import ch.bfh.univote.election.interfaces.Rule;

public class CumulationRule implements Rule {

    private Context context;
    private int id;
    private String type;
    private HashSet<Integer> optionIds;
    private int lowerBound;
    private int upperBound;

    public CumulationRule(Context context, int id, String type, JSONArray optionIds, int lowerBound, int upperBound) throws Exception {
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
        int optionCumulation = ElectionDataController.getInstance(context).getCumulationByOptionId(optionId);

        if (this.optionIds.contains(optionId) && optionCumulation >= this.lowerBound && optionCumulation < this.upperBound) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean optionIdsContainsId(int id) {
        return this.optionIds.contains(id);
    }
}
