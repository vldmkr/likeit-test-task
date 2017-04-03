package org.best.taskboard.models;

public class Card {

    private String mContent = null;
    private String mCategory = "asd";
    private String board = null;

    public Card() {
    }

    public Card(String content) {
        this.mContent = content;
    }

    public boolean isEmpty() {
        return mContent == null;
    }

    public String getContent() {
        return mContent;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

}
