package ch.bfh.univote.election.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.bfh.univote.election.R;
import ch.bfh.univote.election.activities.OptionItem;

public class BallotViewAdapter extends ArrayAdapter<OptionItem> {
    private List<OptionItem> items;
    private Context context;
    private static int layoutResourceId = R.layout.ballot_list_view_item;

    public BallotViewAdapter(Context context, List<OptionItem> items) {
        super(context, layoutResourceId, items);
        this.context = context;
        this.items = items;
    }

    public List<OptionItem> getItems() {
        return this.items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        OptionItemHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new OptionItemHolder();
        holder.optionItem = items.get(position);


        holder.commonName = (TextView)row.findViewById(R.id.candidateTextView);
        holder.count = (TextView)row.findViewById(R.id.countTextView);


        // different style for list item
        if (items.get(position).getType().equals("list")) {
            holder.commonName.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
            holder.commonName.setTypeface(holder.commonName.getTypeface(), Typeface.BOLD);
        }

        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(OptionItemHolder holder) {
        holder.commonName.setText(holder.optionItem.getName());
        holder.count.setText(String.valueOf(holder.optionItem.getCount()));
    }

    public static class OptionItemHolder {
        OptionItem optionItem;
        TextView commonName;
        TextView count;
    }
}
