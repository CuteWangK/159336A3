package com.example.myapplication3;

import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    private Game gameView;
    private GameController gameController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gameView = findViewById(R.id.GameView);
        gameController = findViewById(R.id.ControllerView);

        // 将 GameController 的输入传递给 Game
        gameView.setGameController(gameController);

    }
}