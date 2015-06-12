package ch.bfh.univote.election.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.bfh.univote.election.activities.OptionItem;
import ch.bfh.univote.election.activities.OptionSelectionActivity;
import ch.bfh.univote.election.interfaces.Option;
import ch.bfh.univote.election.interfaces.Rule;
import ch.bfh.univote.election.interfaces.ServerInterface;
import ch.bfh.univote.election.model.CumulationRule;
import ch.bfh.univote.election.model.Candidate;
import ch.bfh.univote.election.model.ElectionList;
import ch.bfh.univote.election.model.SummationRule;

public class ElectionDataController {
    private Context context;
    private SortedMap<Integer, Candidate> candidates;
    private SortedMap<Integer, ElectionList> lists;
    private SortedMap<Integer, Rule> rules;
    public static final String ELECTION_SHARED_PREFS = "electionSharedPrefs";
    public static final String CURRENT_ELECTION_ID_SHARED_PREFS_KEY = "currentElectionId";
    public static final String LOGIN_REQUIRED_SHARED_PREFS_KEY = "loginRequired";
    public static final String DECRYPTED_VOTING_KEY_SHARED_PREFS_KEY = "votingKey";
    private static final String TAG_OPTIONS = "options";
    private static final String TAG_RULES = "rules";
    private static ElectionDataController instance;
    private ServerInterface serverController;
    private SharedPreferences sharedPreferences;

    private ElectionDataController(Context context) {
        this.context = context;
        this.serverController = new DummyServerController(context);
        this.sharedPreferences = context.getSharedPreferences(ELECTION_SHARED_PREFS, Context.MODE_PRIVATE);
        this.candidates = new TreeMap<>();
        this.lists = new TreeMap<>();
        this.rules = new TreeMap<>();

        try {
            parseJSONData();
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

    // Singleton
    public static ElectionDataController getInstance(Context context) {
        if (ElectionDataController.instance == null) {
            ElectionDataController.instance = new ElectionDataController(context);
        }
        return ElectionDataController.instance;
    }

    // to user there is no difference between summate and cumulate. let class decide what to do!
    // option gets added to sharedpreferences
    // returns the (new) count of the option
    public int addVote(int id) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        int count = this.sharedPreferences.getInt(String.valueOf(id), 0);

        // each option id corresponds to one summation rule and one cumulation rule (lists and candidates)
        // both rules have to return isAllowed = true in order to allow to add the option to the ballot.
        boolean myBool = true;
        for(Map.Entry<Integer,Rule> entry : this.rules.entrySet()){
            // check if option id corresponds to rule
            if (entry.getValue().optionIdsContainsId(id)) {
                // if so, check if it is allowed to add the option to the ballot
                myBool &= (entry.getValue().isAllowed(this.context, id));
            }
        }

        if (myBool) {
            // add option to ballot (shared prefs)
            count = count + 1;
            editor.putInt(String.valueOf(id), count);
            editor.commit();
        }
        return count;
    }

    public void addListWithAllCandidates(int listId) {
        this.sharedPreferences.edit().clear().commit();

        // add list item
        addVote(listId);

        // add candidate items
        for (Map.Entry<Integer,Candidate> entry : this.candidates.entrySet()){
            // if candidate belongs to list
            if (entry.getValue().getListId() == listId) {
                // add candidate as long as possible (as long as adding does make a change)
                int oldReturnValue = 0;
                int newReturnValue = 0;
                boolean value = true;
                while (value) {
                    newReturnValue = addVote(entry.getValue().getId());
                    if (newReturnValue != oldReturnValue) {
                        oldReturnValue = newReturnValue;
                    } else {
                        value = false;
                    }
                }
            }
        }
    }

    public boolean decryptedVotingKeyStoredInSharedPrefs() {
        if (this.sharedPreferences.getStringSet(DECRYPTED_VOTING_KEY_SHARED_PREFS_KEY, null) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void addDecryptedVotingKeyToSharedPrefs(String votingKey) {
        this.sharedPreferences.edit().putString(DECRYPTED_VOTING_KEY_SHARED_PREFS_KEY, votingKey).commit();
    }

    public ArrayList<OptionItem> getOptionItemsFromSharedPrefsByListNumber(int listNumber) {
        ArrayList<OptionItem> results = new ArrayList<>();

        TreeMap<Integer, Option> options = (TreeMap<Integer, Option>) getOptionsByListNumber(String.valueOf(listNumber));

        for(Map.Entry<Integer,Option> entry : options.entrySet()) {
            Option value = entry.getValue();
            results.add(new OptionItem(value, this.sharedPreferences.getInt(String.valueOf(value.getId()), 0)));
        }
        return results;
    }

    public void removeAllOptionsFromBallot() {
        String votingKey = this.sharedPreferences.getString(DECRYPTED_VOTING_KEY_SHARED_PREFS_KEY, null);
        this.sharedPreferences.edit().clear().commit();
        if (votingKey != null) {
            addDecryptedVotingKeyToSharedPrefs(votingKey);
        };
    }

    // option gets removed from sharedpreferences
    public int removeFromBallot(int id) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        int value = this.sharedPreferences.getInt(String.valueOf(id), 0);
        if (value != 0) {
            value -= 1;
            editor.putInt(String.valueOf(id), value);
            editor.commit();
        }
        return value;
    }

    public Option getOptionById(int id) {
        if (this.lists.containsKey(id)) {
            return this.lists.get(id);
        } else if(this.candidates.containsKey(id)) {
            return this.candidates.get(id);
        } else {
            return null;
        }
    }

    public Map<Integer, ElectionList> getLists() {
        return lists;
    }

    public Set<? extends Map.Entry<String, ?>> getAllEntriesFromSharedPrefs() {
        return this.sharedPreferences.getAll().entrySet();
    }

    public int getCumulationByOptionId(int optionId) {
        return sharedPreferences.getInt(String.valueOf(optionId), 0);
    }

    public SortedMap<Integer, Option> getOptionsByListNumber(String listNumber) {
        SortedMap<Integer, Option> map = getCandidatesByListNumber(listNumber);

        for (Integer key : lists.keySet()) {
            if (lists.get(key).getNumber().equals(listNumber)) {
                map.put(key, lists.get(key));
            }
        }
        return map;
    }

    private SortedMap<Integer, Option> getCandidatesByListId(int id) {
        SortedMap<Integer, Option> myTreeMap = new TreeMap<>();
        for (Integer key : candidates.keySet()) {
            if (candidates.get(key).getListId() == id) {
                myTreeMap.put(key, candidates.get(key));
            }
        }
        return myTreeMap;
    }

    private SortedMap<Integer, Option> getCandidatesByListNumber(String listNumber) {
        int correspondingIdToNumber = 0;
        for (Integer key : lists.keySet()) {
            if (lists.get(key).getNumber().equals(listNumber)) {
                correspondingIdToNumber = lists.get(key).getId();
            }
        }
        return getCandidatesByListId(correspondingIdToNumber);
    }

    private void parseJSONData() throws Exception {

        JSONObject jsonObj = new JSONObject(serverController.pullElectionDataByElectionId(this.sharedPreferences.getInt((ElectionDataController.CURRENT_ELECTION_ID_SHARED_PREFS_KEY),0)));

        // Getting JSON Array node
        JSONArray options = jsonObj.getJSONArray(TAG_OPTIONS);
        JSONArray rules = jsonObj.getJSONArray(TAG_RULES);

        // looping through all options
        int length = options.length();
        for (int i = 0; i < length; i++) {
            JSONObject o = options.getJSONObject(i);
            if (o.getString("type").equals("list")) {
                this.lists.put(o.getInt("id"), new ElectionList(
                        o.getInt("id"),
                        o.getString("type"),
                        o.getString("number"),
                        o.getString("title"),
                        o.getString("partyAcronym")
                ));
            } else if (o.getString("type").equals("candidate")) {
                this.candidates.put(o.getInt("id"), new Candidate(
                        o.getInt("id"),
                        o.getString("type"),
                        o.getString("number"),
                        o.getString("lastName"),
                        o.getString("firstName"),
                        o.getString("sex"),
                        o.getString("studyBranch"),
                        o.getString("studyDegree"),
                        o.getInt("studySemester"),
                        o.getInt("listId")
                ));
            } else {}
        }

        // looping throug all rules
        for (int i = 0; i < rules.length(); i++) {
            JSONObject o = rules.getJSONObject(i);
            if (o.getString("type").equals("summation")) {
                this.rules.put(o.getInt("id"), new SummationRule(
                        this.context,
                        o.getInt("id"),
                        o.getString("type"),
                        o.getJSONArray("optionIds"),
                        o.getInt("lowerBound"),
                        o.getInt("upperBound")
                ));
            } else if (o.getString("type").equals("cumulation")) {
                this.rules.put(o.getInt("id"), new CumulationRule(
                        this.context,
                        o.getInt("id"),
                        o.getString("type"),
                        o.getJSONArray("optionIds"),
                        o.getInt("lowerBound"),
                        o.getInt("upperBound")
                ));
            } else {}
        }

    }

    public SortedMap<Integer, OptionItem> getOptionItemsFromSharedPrefsWhereOptionCountBiggerZero() {
        SortedMap<Integer, OptionItem> sortedMap = new TreeMap<>();
        for(Map.Entry<String,?> entry : this.sharedPreferences.getAll().entrySet()){
            if (isNumeric(entry.getKey())) {
                if ((this.lists.containsKey(Integer.valueOf(entry.getKey())) || this.candidates.containsKey(Integer.valueOf(entry.getKey()))) && ((Integer) entry.getValue() > 0)) {
                    sortedMap.put(Integer.valueOf(entry.getKey()), new OptionItem(getOptionById(Integer.valueOf(entry.getKey())), (Integer) entry.getValue()));
                }
            }
        }
        return sortedMap;
    }

    public ServerInterface getServerController() {
        return this.serverController;
    }

    public void setLoginRequiredFlag(boolean value) {
        this.sharedPreferences.edit().putBoolean(LOGIN_REQUIRED_SHARED_PREFS_KEY, value).commit();
    }

    public boolean getLoginRequiredFlag() {
        return this.sharedPreferences.getBoolean(LOGIN_REQUIRED_SHARED_PREFS_KEY, true);
    }

    public void clearSharedPrefs() {
        this.sharedPreferences.edit().clear().commit();
    }

    private boolean isNumeric(String str) {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
