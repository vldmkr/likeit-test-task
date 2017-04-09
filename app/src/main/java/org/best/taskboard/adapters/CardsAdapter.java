package org.best.taskboard.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.best.taskboard.BoardActivity;
import org.best.taskboard.R;
import org.best.taskboard.models.Card;

import java.util.List;

//Cards adapter to show every card in the corresponding list
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private List<Card> mCards;
    private int mColor = 0;

    public CardsAdapter(List<Card> cards) {
        mCards = cards;
    }

    public void setColor(int color) {
        mColor = color;
    }

    //Define the fields of every card item in the list
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView description;
        LinearLayout leftColor;
        LinearLayout rightColor;

        ViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.card_description);
            leftColor = (LinearLayout) itemView.findViewById(R.id.card_left_color);
            rightColor = (LinearLayout) itemView.findViewById(R.id.card_right_color);
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
        final Card card = mCards.get(viewHolder.getAdapterPosition());

        TextView description = viewHolder.description;
        description.setText(card.getContent());
        LinearLayout hintColor = card.isMine() ? viewHolder.rightColor : viewHolder.leftColor;
        viewHolder.rightColor.setVisibility(card.isMine() ? View.VISIBLE : View.INVISIBLE);
        viewHolder.leftColor.setVisibility(card.isMine() ? View.INVISIBLE : View.VISIBLE);

        if (mColor != 0) {
            hintColor.setBackgroundColor(mColor);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogLightTheme);
                builder.setTitle(v.getResources().getString(R.string.choose_action))
                        .setItems(R.array.actions_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    //Move card to another category (column)
                                    ((BoardActivity) view.getContext()).dialogResendMsg(card);
                                } else if (which == 1) {
                                    //Edit card
                                    ((BoardActivity) view.getContext()).dialogEditMsg(card);
                                } else if (which == 2) {
                                    //Delete card
                                    ((BoardActivity) view.getContext()).dialogDeleteMsg(card);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mCards.size();
    }

}
