package com.example.teamproject_hangman;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    Button turnBackBtn;
    int[] nameArray = {R.id.firstNameText, R.id.secondNameText, R.id.thirdNameText, R.id.fourthNameText,
            R.id.fifthNameText, R.id.sixthNameText, R.id.seventhNameText};
    int[] scoreArray = {R.id.firstScoreText, R.id.secondScoreText, R.id.thirdScoreText, R.id.fourthScoreText,
                R.id.fifthScoreText, R.id.sixthScoreText, R.id.seventhScoreText};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        setTitle("Score menu");

        turnBackBtn = findViewById(R.id.turnBackBtn);

        helper = new DBHelper(this);
        try{
            db = helper.getWritableDatabase();
        }
        catch (SQLException ex){
            //DB읽기, DB가 없다면 onCreate가 호출되고, version이 바뀌었다면 onUpgrade호출
            db = helper.getReadableDatabase();
        }

        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM userScore ORDER BY score", null);

        int i = 0;
        while(cursor.moveToNext() && i<7){
            TextView nametv = findViewById(nameArray[i]);
            TextView scoretv = findViewById(scoreArray[i]);

            nametv.setText(cursor.getString(1));
            scoretv.setText(cursor.getString(2));
            i++;
        }

        turnBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
