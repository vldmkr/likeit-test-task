package org.best.taskboard.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.best.taskboard.R;
import org.best.taskboard.models.Card;

import java.util.List;

//Cards adapter to show every card in the corresponding list
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private List<Card> mCards;

    public CardsAdapter(List<Card> cards) {
        mCards = cards;
    }

    //Define the fields of every card item in the list
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView description;
        LinearLayout leftColor;

        ViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.card_description);
            leftColor = (LinearLayout) itemView.findViewById(R.id.card_left_color);
        }
    }

    @Override
    public CardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_card, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(CardsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Card card = mCards.get(position);

        TextView description = viewHolder.description;
        description.setText(card.getContent());
        LinearLayout leftColor = viewHolder.leftColor;

        if (card.getCategory().equals("ready")) {
            leftColor.setBackgroundColor(ContextCompat.getColor(
                    viewHolder.itemView.getContext(),
                    R.color.readyColor));
        } else if (card.getCategory().equals("inprogress")) {
            leftColor.setBackgroundColor(ContextCompat.getColor(
                    viewHolder.itemView.getContext(),
                    R.color.inprogressColor));
        } else if (card.getCategory().equals("paused")) {
            leftColor.setBackgroundColor(ContextCompat.getColor(
                    viewHolder.itemView.getContext(),
                    R.color.pausedColor));
        } else if (card.getCategory().equals("testing")) {
            leftColor.setBackgroundColor(ContextCompat.getColor(
                    viewHolder.itemView.getContext(),
                    R.color.testingColor));
        } else if (card.getCategory().equals("done")) {
            leftColor.setBackgroundColor(ContextCompat.getColor(
                    viewHolder.itemView.getContext(),
                    R.color.doneColor));
        }

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogLightTheme);
//                builder.setTitle(v.getResources().getString(R.string.choose_action))
//                        .setItems(R.array.actions_array, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (which == 0) {
//                                    //Move card to another category (column)
//                                    ((KanbanActivity) view.getContext()).moveCardDialog(card);
//                                } else if (which == 1) {
//                                    //Edit card
//                                    ((KanbanActivity) view.getContext()).editCardDialog(card);
//                                } else if (which == 2) {
//                                    //Delete card
//                                    ((KanbanActivity) view.getContext()).deleteCardDialog(card);
//                                }
//                            }
//                        })
//                        .show();
                return true;
            }
        });
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mCards.size();
    }

}