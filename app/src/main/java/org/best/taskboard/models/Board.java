package org.best.taskboard.models;

import java.net.InetAddress;
import java.util.ArrayList;

//Board model
public class Board {

    private String name;
    private InetAddress address;
    private ArrayList<String> cards = new ArrayList<>();
    private ArrayList<String> owners = new ArrayList<>();
    private String _id;

    public Board(String name, InetAddress address) {
        this.name = name;
        this.address = address;
//        this.owners = owners;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getAddress() {
        return address;
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
