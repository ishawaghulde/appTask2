package com.example.android.connect4;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ImageView[][] cells;
    private View boardView;
    private gameBoard board;
    private ViewHolder viewHolder;
    private static int NUM_ROWS = 6;
    private static int NUM_COLS = 7;
    public int prevCol = 0;
    public int prevRow = 0;


    private class ViewHolder {
        public TextView winnerText;
        public ImageView turnIndicatorImageView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        board = new gameBoard(NUM_COLS, NUM_ROWS);
        boardView = findViewById(R.id.game_board);
        buildCells();
        boardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_UP: {
                        int col = colAtX(motionEvent.getX());
                        prevCol = col;
                        if (col != -1)
                            drop(col);
                    }
                }
                return true;
            }
        });
        ImageView resetButton = (ImageView) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
        ImageView undoButton = (ImageView) findViewById(R.id.undo);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reverse();
            }
        });



        viewHolder = new ViewHolder();
        viewHolder.turnIndicatorImageView = (ImageView) findViewById(R.id.turn_indicator_image_view);
        viewHolder.turnIndicatorImageView.setImageResource(resourceForTurn());
        viewHolder.winnerText = (TextView) findViewById(R.id.winner_text);
        viewHolder.winnerText.setVisibility(View.GONE);
    }

    public void reverse(){
        prevRow = board.lastAvailableRow(prevCol) + 1;
        board.undo(prevRow, prevCol);
        ViewGroup row = (ViewGroup) ((ViewGroup) boardView).getChildAt(prevRow);
        row.setClipChildren(false);
        ImageView imageView = (ImageView) row.getChildAt(prevCol);
        imageView.setImageResource(android.R.color.transparent);
//        cells[prevRow][prevCol].setImageResource(android.R.color.transparent);
        cells[prevRow][prevCol] = imageView;
        changeTurn();

    }

    private void buildCells() {
        cells = new ImageView[NUM_ROWS][NUM_COLS];
        for (int r=0; r<NUM_ROWS; r++) {
            ViewGroup row = (ViewGroup) ((ViewGroup) boardView).getChildAt(r);
            row.setClipChildren(false);
            for (int c=0; c<NUM_COLS; c++) {
                ImageView imageView = (ImageView) row.getChildAt(c);
                imageView.setImageResource(android.R.color.transparent);
                cells[r][c] = imageView;
            }
        }
    }

    private void drop(int col) {
        if (board.hasWinner)
            return;
        int row = board.lastAvailableRow(col);
        if (row == -1)
            return;
        final ImageView cell = cells[row][col];
        float move = -(cell.getHeight() * row + cell.getHeight() + 15);
        cell.setY(move);
        cell.setImageResource(resourceForTurn());
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, Math.abs(move));
        anim.setDuration(850);
        anim.setFillAfter(true);
        cell.startAnimation(anim);
        board.occupyCell(col, row);
        try{
            MediaPlayer mplayer = MediaPlayer.create(MainActivity.this, R.raw.coin_fall);
            mplayer.start();
        }catch(Exception e){
            Log.d("coin_fall" , "error: " + e);
        }
        if (board.checkForWin(col, row)) {
            win();
        } else {
            changeTurn();
        }
    }

    private void win() {
        int color = board.turn == gameBoard.Turn.FIRST ? getResources().getColor(R.color.red) : getResources().getColor(R.color.yellow);
        viewHolder.winnerText.setTextColor(color);
        viewHolder.winnerText.setVisibility(View.VISIBLE);
    }

    private void changeTurn() {
        board.toggleTurn();
        viewHolder.turnIndicatorImageView.setImageResource(resourceForTurn());
    }

    private int colAtX(float x) {
        float colWidth = cells[0][0].getWidth();
        int col = (int) x / (int) colWidth;
        if (col < 0 || col > 6)
            return -1;
        return col;
    }

    private int resourceForTurn() {
        switch (board.turn) {
            case FIRST:
                return R.drawable.red_coin;
            case SECOND:
                return R.drawable.yellow_disc;
        }
        return R.drawable.red_coin;
    }

    private void reset() {
        board.reset();
        viewHolder.winnerText.setVisibility(View.GONE);
        viewHolder.turnIndicatorImageView.setImageResource(resourceForTurn());
        for (int r=0; r<NUM_ROWS; r++) {
            for (int c=0; c<NUM_COLS; c++) {
                cells[r][c].setImageResource(android.R.color.transparent);
            }
        }
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
}
