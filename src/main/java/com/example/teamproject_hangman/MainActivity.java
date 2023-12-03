package com.example.teamproject_hangman;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button startBtn, endBtn, scoreBtn;
    final String[] dlgtext = {"쉬움", "어려움"};
    DBHelper helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startBtn);
        endBtn = findViewById(R.id.endBtn);
        scoreBtn = findViewById(R.id.scoreBtn);

        helper = new DBHelper(this);
        try{
            db = helper.getWritableDatabase();
        }
        catch (SQLException ex){
            //DB읽기, DB가 없다면 onCreate가 호출되고, version이 바뀌었다면 onUpgrade호출
            db = helper.getReadableDatabase();
        }


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("난이도를 선택하세요");
                dlg.setIcon(R.drawable.ic_launcher_foreground);
                dlg.setItems(dlgtext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Intent intent = new Intent(getApplicationContext(), LowLevelActivity.class);
                            startActivity(intent);
                        }
                        else if(which == 1){
                            Intent intent = new Intent(getApplicationContext(), HighLevelActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                dlg.setNegativeButton("닫기",null);
                dlg.show();
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        scoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
                startActivity(intent);
            }
        });
    }
}

class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mycontacts.db";
    private static final int DATABASE_VERSION = 2;

    //세번째 인수 factory 표준 cursor를 이용할 경우 null로 지정
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //db에 저장하는 값은 일련번호, 이름, 점수
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE userScore(_id INTEGER PRIMARY KEY AUTOINCREMENT, name text, score INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }
}