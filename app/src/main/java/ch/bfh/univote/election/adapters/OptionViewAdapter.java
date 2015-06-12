package ch.bfh.univote.election.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import ch.bfh.univote.election.R;
import ch.bfh.univote.election.activities.OptionItem;
import ch.bfh.univote.election.activities.OptionListFragment;

public class OptionViewAdapter extends ArrayAdapter<OptionItem> {
    private List<OptionItem> items;
    private Context context;
    private OptionListFragment parentFragment;
    private static int layoutResourceId = R.layout.candidate_list_view_item;

    public OptionViewAdapter(OptionListFragment parentFragment, Context context, List<OptionItem> items) {
        super(context, layoutResourceId, items);
        this.parentFragment = parentFragment;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        OptionItemHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new OptionItemHolder();
        holder.optionItem = items.get(position);

        holder.addButton = (ImageButton)row.findViewById(R.id.addButton);
        holder.addButton.setTag(holder.optionItem);
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentFragment.addOption(v);
            }
        });

        holder.removeButton = (ImageButton)row.findViewById(R.id.deleteButton);
        holder.removeButton.setTag(holder.optionItem);
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentFragment.removeOption(v);
            }
        });

        holder.commonName = (TextView)row.findViewById(R.id.candidateNameTextView);
        holder.count = (TextView)row.findViewById(R.id.candidateCountTextView);


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
        ImageButton addButton;
        ImageButton removeButton;
    }
}
