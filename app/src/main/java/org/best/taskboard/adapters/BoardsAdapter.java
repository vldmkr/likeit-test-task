package org.best.taskboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.best.taskboard.BoardActivity;
import org.best.taskboard.R;
import org.best.taskboard.models.Board;

import java.util.List;

//Boards adapter to show every board in the list
public class BoardsAdapter extends RecyclerView.Adapter<BoardsAdapter.ViewHolder> {

    private List<Board> mBoards;

    public BoardsAdapter(List<Board> boards) {
        mBoards = boards;
    }

    //Define the fields for every board item in the list
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_item_board_name);
            description = (TextView) itemView.findViewById(R.id.list_item_board_description);
        }
    }

    @Override
    public BoardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_board, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BoardsAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Board board = mBoards.get(position);

        TextView name = viewHolder.name;
        TextView description = viewHolder.description;
        name.setText(board.getName());
        description.setText(board.getAddress().toString());

        //On click listener
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BoardActivity) v.getContext()).selectBoard(board.getName(), board.getAddress());
            }
        });
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mBoards.size();
    }

}
