package org.best.taskboard.models;

import java.util.ArrayList;

//Board model
public class Board {

    private String name;
    private String description;
    private ArrayList<String> cards = new ArrayList<>();
    private ArrayList<String> owners = new ArrayList<>();
    private String _id;

    public Board(String name, String description) {
        this.name = name;
        this.description = description;
//        this.owners = owners;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getCards() {
        return cards;
    }

    public void setCards(ArrayList<String> cards) {
        this.cards = cards;
    }

    public ArrayList<String> getOwners() {
        return owners;
    }

    public void setOwners(ArrayList<String> owners) {
        this.owners = owners;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
