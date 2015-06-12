package ch.bfh.univote.election.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

import ch.bfh.univote.election.activities.PasswordActivity;
import ch.bfh.univote.election.R;
import ch.bfh.univote.election.model.ElectionContainer;
import ch.bfh.univote.election.controllers.ElectionDataController;

public class ElectionViewAdapter extends ArrayAdapter<ElectionContainer> {
    private List<ElectionContainer> items;
    private Context context;
    private static int layoutResourceId = R.layout.election_item;

    public ElectionViewAdapter(Context context, List<ElectionContainer> items) {
        super(context, layoutResourceId, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ElectionItemHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new ElectionItemHolder();
        holder.electionContainer = items.get(position);

        holder.button = (Button)row.findViewById(R.id.electionButton);



        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(final ElectionItemHolder holder) {
        holder.button.setText(holder.electionContainer.getElectionTitleDe());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(ElectionDataController.ELECTION_SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt(ElectionDataController.CURRENT_ELECTION_ID_SHARED_PREFS_KEY, holder.electionContainer.getElectionId());
                editor.commit();

                Intent intent = new Intent(getContext(), PasswordActivity.class);
                getContext().startActivity(intent);
            }
        });
    }

    public static class ElectionItemHolder {
        ElectionContainer electionContainer;
        Button button;
    }
}
