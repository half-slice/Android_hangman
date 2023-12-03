package com.example.teamproject_hangman;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class HighLevelActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    EditText inputText;
    TextView hiddenText, lifeText, failText;
    ImageView hangmanImg;
    Button backBtn;
    Chronometer timer;
    Random random;
    int randomNumber;
    int life = 4;
    int success = 0;
    String chooseWord, hiddenWord;
    String[] words;
    HashSet<String> failWords = new HashSet<String>();
    Integer[] imageId = {R.drawable.hangman7,R.drawable.hangman5, R.drawable.hangman3, R.drawable.hangman2, R.drawable.hangman1};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_level);
        setTitle("high level test");

        inputText = findViewById(R.id.inputText);
        hiddenText = findViewById(R.id.hiddenText);
        lifeText = findViewById(R.id.lifeText);
        failText = findViewById(R.id.failText);
        hangmanImg = findViewById(R.id.hangmanImg);
        backBtn = findViewById(R.id.backBtn);
        timer = findViewById(R.id.timer);

        words = new String[]{"achieve", "benefit", "careful", "decide", "eager", "famous", "generous", "honest", "improve", "join",
                "kind", "learn", "mistake", "natural", "observe", "patient", "question", "recognize", "support", "unique",
                "valuable", "worried", "active", "brave", "curious", "determined", "efficient", "flexible", "grateful", "hopeful",
                "intelligent", "joyful", "knowledgeable", "loyal", "motivated", "neat", "optimistic", "passionate", "quick", "responsible",
                "sincere", "talented", "understanding", "versatile", "wise", "apologize", "balance", "communicate", "dream", "encourage",
                "focus", "grow", "help", "inspire", "journey", "keep", "listen", "mentor", "navigate", "overcome",
                "persevere", "question", "resolve", "strive", "teach", "understand", "visualize", "work", "analyze", "budget",
                "calculate", "develop", "evaluate", "forecast", "guide", "identify", "justify", "manage", "negotiate", "organize",
                "plan", "qualify", "research", "strategy", "target", "upgrade", "verify", "adapt", "blend", "collaborate",
                "demonstrate", "embrace", "facilitate", "generate", "implement", "justify", "navigate", "optimize", "promote", "quantify"};

        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        helper = new DBHelper(this);
        try{
            db = helper.getWritableDatabase();
        }
        catch (SQLException ex){
            //DB읽기, DB가 없다면 onCreate가 호출되고, version이 바뀌었다면 onUpgrade호출
            db = helper.getReadableDatabase();
        }


        chooseWord = randomWord(words);
        hiddenWord = setHiddenWord(chooseWord);

        hiddenText.setText(hiddenWord);
        lifeText.setText("life = " + life);
        //failText.setText(chooseWord);
        failText.setText("");
        hangmanImg.setImageResource(imageId[life]);

        //글자를 입력
        inputText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //엔터키 누를시 반응
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String getText = inputText.getText().toString();
                    inputText.setText("");
                    inputText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);


                    //알파벳과 단어에 맞는 글자를 입력시
                    if(chooseWord.contains(getText) && getText.matches("[a-zA-Z]")){
                        //단어에 맞는 글자를 찾는다
                        searchRightWord(getText);

                        //만약 hidden텍스트를 다 맞췄을 경우,
                        if(!hiddenWord.contains("_")){
                            makeRightWords();
                            return true;
                        }

                        Toast.makeText(HighLevelActivity.this, "알맞은 글자입니다", Toast.LENGTH_LONG).show();
                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.start();
                        return true;
                    }
                    //알파벳을 입력하지 않았을시
                    else if(!getText.matches("[a-zA-Z]")){
                        Toast.makeText(HighLevelActivity.this, "알파벳만 입력해 주세요", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    //글자가 틀렸을시
                    else if(!chooseWord.contains(getText)){
                        Toast.makeText(HighLevelActivity.this, "알맞은 글자가 아니였습니다", Toast.LENGTH_SHORT).show();
                        hangmanHurt(getText);
                        return true;
                    }
                }

                return false;
            }
        });
        
        //타이머가 30초 지났을시 -> life -1
        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedTime = SystemClock.elapsedRealtime() - timer.getBase();
                //30sec == 30000
                if(elapsedTime >= 30000){
                    Toast.makeText(HighLevelActivity.this, "시간 초과", Toast.LENGTH_SHORT).show();
                    hangmanHurt();
                }
            }
        });

        //돌아가기 버튼 클릭시
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGame();
            }
        });
    }

    //-----------------------------------------------------------------------------------------------
    public String randomWord(String[] words) {
        random = new Random();
        randomNumber = random.nextInt(words.length)+1;

        return words[randomNumber];
    }

    public String setHiddenWord(String chooseWord){
        int n = chooseWord.length();
        StringBuilder hiddenBuilder = new StringBuilder();
        for(int i=0; i<n; i++){
            hiddenBuilder.append("_ ");
        }
        return hiddenBuilder.toString();
    }

    //단어에 맞는 글자를 찾음
    public void searchRightWord(String getText){
        StringBuilder hidden = new StringBuilder(hiddenWord);
        for(int i=0; i<chooseWord.length(); i++){
            if(String.valueOf(chooseWord.charAt(i)).equals(getText)){
                hidden.setCharAt(i*2, getText.charAt(0));
            }
        }
        hiddenWord = hidden.toString();
        hiddenText.setText(hiddenWord);
        inputText.setText("");
    }
    
    //단어를 완성시킬시
    public void makeRightWords(){
        chooseWord = randomWord(words);
        hiddenWord = setHiddenWord(chooseWord);
        hiddenText.setText(hiddenWord);
        failWords.clear();
        failText.setText("");
        //failText.setText(chooseWord);
        inputText.setText("");
        success += 1;
        Toast.makeText(HighLevelActivity.this, "단어를 완성했습니다", Toast.LENGTH_SHORT).show();

        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
    }

    //행맨 다침
    public void hangmanHurt(){
        life -= 1;
        lifeText.setText("life = " + life);

        //목숨이 다 날아감
        if(life<0){
            hangmanDie();
        }
        else{
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
            hangmanImg.setImageResource(imageId[life]);
        }
    }

    public void hangmanHurt(String getText){
        life -= 1;
        lifeText.setText("life = " + life);
        failWords.add(getText);

        StringBuilder  viewTextBuilder = new StringBuilder();
        ArrayList<String> array = new ArrayList<String>(failWords);

        for(int i=0; i<failWords.size(); i++){
            if(i == 0){
                viewTextBuilder.append(array.get(i));
            }
            else{
                viewTextBuilder.append("," + array.get(i));
            }
        }
        String viewText = viewTextBuilder.toString();
        failText.setText(viewText);

        //목숨이 다 날아감
        if(life<0){
            hangmanDie();
        }
        else{
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
            hangmanImg.setImageResource(imageId[life]);
        }
    }
    //행맨 죽음
    public void hangmanDie(){
        inputText.setVisibility(View.INVISIBLE);
        hiddenText.setText(chooseWord);
        lifeText.setVisibility(View.INVISIBLE);
        failText.setText("Game Over");
        timer.stop();
        showDialog();
    }

    //대화상자 생성
    public void showDialog(){
        timer.stop();

        AlertDialog.Builder dlg = new AlertDialog.Builder(HighLevelActivity.this);
        dlg.setTitle("게임 끝");
        dlg.setMessage("정답은 " + chooseWord + "입니다.\r\n" + "성공한 단어는 " + success + "개 입니다.");
        dlg.setPositiveButton("다시하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //다시하기
                returnGame();
            }
        });
        dlg.setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //스코어 저장 대화상자로 이동
                ScoreDialog();
            }
        });
        dlg.show();
    }

    public void ScoreDialog(){
        AlertDialog.Builder dlg = new AlertDialog.Builder(HighLevelActivity.this);
        dlg.setTitle("기록하시겠습니까?");
        dlg.setMessage("점수 기록할 이름 입력");
        
        final EditText editName = new EditText(this);
        editName.setMaxEms(5);
        dlg.setView(editName);

        dlg.setPositiveButton("점수 저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(editName.getText().equals("")){
                    Toast.makeText(HighLevelActivity.this, "이름을 제대로 기입해주십시오", Toast.LENGTH_SHORT).show();
                }
                else{
                    db.execSQL("INSERT INTO userScore VALUES(null, '" + editName.getText().toString() +"', '" + success + "');");
                    Toast.makeText(HighLevelActivity.this, "정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    endGame();
                }
            }
        });

        dlg.setNegativeButton("메인으로", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endGame();
            }
        });
        dlg.show();
    }



    //
    public void returnGame(){
        chooseWord = randomWord(words);
        hiddenWord = setHiddenWord(chooseWord);
        failWords.clear();
        failText.setText("");
        //failText.setText(chooseWord);
        hiddenText.setText(hiddenWord);
        inputText.setVisibility(View.VISIBLE);
        life = 4;
        lifeText.setText("life = " + life);
        hangmanImg.setImageResource(imageId[life]);
        success = 0;
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

    }

    public void endGame(){
        finish();
    }
}


