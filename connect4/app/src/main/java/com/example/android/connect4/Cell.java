package com.example.android.connect4;

public class Cell {
    public boolean empty;
    public gameBoard.Turn player;

    public Cell() {
        empty = true;
    }

    public void setPlayer(gameBoard.Turn player) {
        this.player = player;
        empty = false;
    }
}
