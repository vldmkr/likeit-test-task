package org.best.taskboard.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
        description.setText(board.getDescription());

        //On click listener
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BoardActivity) v.getContext()).selectBoard(viewHolder.name.getText().toString());
            }
        });

        //On long click listener
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //long click
                final View view = v;
                CharSequence[] items = {v.getResources().getString(R.string.edit_board_dialog_title),
                v.getResources().getString(R.string.delete_board_dialog_title)};
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogLightTheme);
                builder.setTitle(v.getResources().getString(R.string.choose_action))
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                switch (which) {
//                                    case 0:
//                                        //Edit board name and description
//                                        ((KanbanActivity) view.getContext()).editBoardDialog(board);
//                                        break;
//                                    case 1:
//                                        //Delete board
//                                        ((KanbanActivity) view.getContext()).deleteBoardDialog(board);
//                                        break;
//                                }
                            }
                        })
                        .show();
                return false;
            }
        });
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mBoards.size();
    }

}
