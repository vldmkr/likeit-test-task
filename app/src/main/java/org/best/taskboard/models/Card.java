package org.best.taskboard.models;

public class Card {

    private String mContent = null;
    private String mCategory = "asd";
    private String board = null;
    private boolean mIsMine = true;

    public Card(String content, boolean isMine) {
        this(content);
        mIsMine = isMine;
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

    public boolean isMine() {
        return mIsMine;
    }

    public void setContent(String content) {
        mContent = content;
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
