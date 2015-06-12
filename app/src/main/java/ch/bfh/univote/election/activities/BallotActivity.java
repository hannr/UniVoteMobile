package ch.bfh.univote.election.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.TreeMap;
import ch.bfh.univote.election.R;
import ch.bfh.univote.election.adapters.BallotViewAdapter;
import ch.bfh.univote.election.controllers.ElectionDataController;


/**
 * The Ballot where the chosen options are shown with the corresponding cumulation count.
 */
public class BallotActivity extends Activity {

    private ListView ballotListView;
    private BallotViewAdapter bva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ballot);

        ballotListView = (ListView) findViewById(R.id.ballotListView);

        TreeMap<Integer, OptionItem> sortedMap = (TreeMap<Integer, OptionItem>) ElectionDataController.getInstance(getApplicationContext()).getOptionItemsFromSharedPrefsWhereOptionCountBiggerZero();
        ArrayList<OptionItem> optionItems = new ArrayList<>();
        for (Integer key : sortedMap.keySet()) {
            optionItems.add(sortedMap.get(key));
        }

        bva = new BallotViewAdapter(this, optionItems);
        ballotListView.setAdapter(bva);

        Button deleteAll = (Button) findViewById(R.id.deleteBallotContentButton);
        Button sendBallot = (Button) findViewById(R.id.sendBallotButton);

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ElectionDataController.getInstance(getApplicationContext()).removeAllOptionsFromBallot();
                bva.notifyDataSetChanged();
                bva.clear();
            }
        });

        sendBallot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ElectionDataController.getInstance(getApplicationContext()).setLoginRequiredFlag(false);
                Intent intent = new Intent(getApplicationContext(), VerificationActivity.class);
                // later a JSON Object with Votes would be sent via ElectionDataController to Server Interface Class
                // and Verification Activity would show some real Verification Data. But at this point of time, the author
                // does not know how the json Object has to look like and therefore shows some mock data at verification activity...
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ElectionDataController.getInstance(getApplicationContext()).setLoginRequiredFlag(true);

        TreeMap<Integer, OptionItem> sortedMap = (TreeMap<Integer, OptionItem>) ElectionDataController.getInstance(getApplicationContext()).getOptionItemsFromSharedPrefsWhereOptionCountBiggerZero();
        ArrayList<OptionItem> optionItems = new ArrayList<>();
        for (Integer key : sortedMap.keySet()) {
            optionItems.add(sortedMap.get(key));
        }
        
        bva.clear();
        bva.addAll(optionItems);
    }

    public void onClick(View v) {
        ElectionDataController.getInstance(getApplicationContext()).setLoginRequiredFlag(false);
        Intent intent = new Intent(this, OptionSelectionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (ElectionDataController.getInstance(getApplicationContext()).getLoginRequiredFlag()) {
            Intent intent = new Intent(this, PasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ElectionDataController.getInstance(getApplicationContext()).setLoginRequiredFlag(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ballot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) { return true; }
        return super.onOptionsItemSelected(item);
    }
}