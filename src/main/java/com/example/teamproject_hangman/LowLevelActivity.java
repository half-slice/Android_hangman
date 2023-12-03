package com.example.teamproject_hangman;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class LowLevelActivity extends AppCompatActivity {

    TextView hiddenText, failText, lifeText;
    EditText setText;
    Button backBtn;
    Random random;
    int randomNumber;
    int success;
    String chooseWord;
    String[] targetWord;
    StringBuilder revealedWord;
    ImageView hangmanImg;
    Integer[] imageId = {R.drawable.hangman2, R.drawable.hangman3, R.drawable.hangman5, R.drawable.hangman7, R.drawable.hangman1};
    int life = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_level);

        hiddenText = findViewById(R.id.hiddenText);
        failText = findViewById(R.id.failText);
        lifeText = findViewById(R.id.lifeText);
        setText = findViewById(R.id.setText);
        backBtn = findViewById(R.id.backBtn);
        hangmanImg = findViewById(R.id.imageView2);

        targetWord = new String[]{"achieve", "benefit", "careful", "decide", "eager", "famous", "generous", "honest", "improve", "join",
                "kind", "learn", "mistake", "natural", "observe", "patient", "question", "recognize", "support", "unique",
                "valuable", "worried", "active", "brave", "curious", "determined", "efficient", "flexible", "grateful", "hopeful",
                "intelligent", "joyful", "knowledgeable", "loyal", "motivated", "neat", "optimistic", "passionate", "quick", "responsible",
                "sincere", "talented", "understanding", "versatile", "wise", "apologize", "balance", "communicate", "dream", "encourage",
                "focus", "grow", "help", "inspire", "journey", "keep", "listen", "mentor", "navigate", "overcome",
                "persevere", "question", "resolve", "strive", "teach", "understand", "visualize", "work", "analyze", "budget",
                "calculate", "develop", "evaluate", "forecast", "guide", "identify", "justify", "manage", "negotiate", "organize",
                "plan", "qualify", "research", "strategy", "target", "upgrade", "verify", "adapt", "blend", "collaborate",
                "demonstrate", "embrace", "facilitate", "generate", "implement", "justify", "navigate", "optimize", "promote", "quantify"};

        // 랜덤하게 단어 추출
        chooseWord = randomWord(targetWord);

        hangmanImg.setImageResource(imageId[life]);

        // 단어의 길이만큼 "_" 생성
        revealedWord = new StringBuilder(chooseWord.length());
        for (int i = 0; i < chooseWord.length(); i++) {
            revealedWord.append("_ ");
        }
        hiddenText.setText(revealedWord.toString());
        lifeText.setText("life: " + life);

        // 엔터
        setText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                checkGuess(setText.getText().toString());
                setText.setText("");
                // setText.clearFocus(); // 포커스
                return true;
            }
            return false;
        });

        backBtn.setOnClickListener(v -> finish());
    }
//__________________________________________________________________________________________________

    // 렌덤하게 단어 뽑기
    public String randomWord(String[] targetWord) {
        random = new Random();
        randomNumber = random.nextInt(targetWord.length);
        return targetWord[randomNumber];
    }
    //
    public void checkGuess(String guess) {
        // 비어있거나 알파벳이 아닌 경우
        if (guess.isEmpty() || !Character.isLetter(guess.charAt(0))) {
            Toast.makeText(LowLevelActivity.this, "알파벳만 입력해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 알파벳 확인
        boolean isCorrectGuess = false;
        for (int i = 0; i < chooseWord.length(); i++) {
            if (chooseWord.charAt(i) == guess.charAt(0)) {
                revealedWord.setCharAt(i * 2, guess.charAt(0));
                isCorrectGuess = true;
            }
        }

        // 만약 hidden텍스트를 다 맞췄을 경우,
        if (isCorrectGuess) {
            hiddenText.setText(revealedWord.toString());
            if (!revealedWord.toString().contains("_")) {
                Toast.makeText(LowLevelActivity.this, "알맞은 글자입니다", Toast.LENGTH_LONG).show();
                success += 1;
            }
        }
        // 글자가 틀렸을 시
        else {
            life--;
            lifeText.setText("life: " + life);
            failText.append(guess + " ");
            if (life < 0) {
                hiddenText.setText(chooseWord);
                Toast.makeText(LowLevelActivity.this, "알맞은 글자가 아니였습니다", Toast.LENGTH_SHORT).show();
                lifeText.setText("Game Over");
                showDialog();
            }

            switch (life) {
                case 3:
                    hangmanImg.setImageResource(imageId[3 - life]);
                    break;
                case 2:
                    hangmanImg.setImageResource(imageId[3 - life]);
                    break;
                case 1:
                    hangmanImg.setImageResource(imageId[3 - life]);
                    break;
                case 0:
                    hangmanImg.setImageResource(imageId[3 - life]);
                    break;
            }
        }
        setText.setText("");
    }

    public void showDialog(){
        AlertDialog.Builder dlg = new AlertDialog.Builder(LowLevelActivity.this);
        dlg.setTitle("게임 끝");
        dlg.setMessage("성공한 단어는 " + success + "개 입니다.");
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
                //메인화면으로 이동
                endGame();
            }
        });
        dlg.show();
    }

    public void returnGame(){
        chooseWord = randomWord(targetWord);
        //revealedWord = revealedWord(chooseWord);
        failText.setText("정답이 아닌 글자 : ");
        hiddenText.setText(revealedWord);
        life = 4;
        lifeText.setText("life = " + life);
        hangmanImg.setImageResource(imageId[life]);
        success = 0;

    }

    public void endGame(){
        finish();
    }
}
