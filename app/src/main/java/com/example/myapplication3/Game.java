package com.example.myapplication3;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
public class Game extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    enum States {
        Start,
        Playing,
        Pause,
        Finish
    }
    private Bitmap background_game, playerbitmap0, playerbitmap1, playerbitmap2, playerbitmap3, playerbitmap4, playerbitmap5, weaponbitmap,bulletBitmap;
    private Bitmap enemybitmap0,enemybitmap1,enemybitmap2,enemybitmap3,enemybitmap4;
    private Canvas mCanvas;
    private int ViewWith, ViewHeight;
    private SurfaceHolder surfaceHolder;
    private boolean isDraw = true;
    private Figure player;
    private Entity background;
    private Entity weapon;
    private GameController gameController;
    private float cameraOffsetX = 0;
    private float cameraOffsetY = 0;
    private List<Figure> enemies = new ArrayList<>();  // 怪物集合
    private Random random = new Random();
    private List<Bullet> bullets = new ArrayList<>();
    private int killCount = 0;
    MediaPlayer backgroundMusic;
    private States GameState = States.Start;
    private Thread gameThread;
    private boolean isRunning = true;

    public Game(Context context) {
        super(context);
        initView(context);
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private Paint textPaint;
    private void initView(Context context) {
        background_game = BitmapFactory.decodeResource(context.getResources(),R.drawable.background);
        playerbitmap0 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player0);
        playerbitmap1 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player1);
        playerbitmap2 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player2);
        playerbitmap3 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player3);
        playerbitmap4 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player4);
        playerbitmap5 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player5);
        weaponbitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.weapon);
        bulletBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.bullet3);
        enemybitmap0 = BitmapFactory.decodeResource(context.getResources(),R.drawable.e0);
        enemybitmap1 = BitmapFactory.decodeResource(context.getResources(),R.drawable.e1);
        enemybitmap2 = BitmapFactory.decodeResource(context.getResources(),R.drawable.e2);
        enemybitmap3 = BitmapFactory.decodeResource(context.getResources(),R.drawable.e3);
        enemybitmap4 = BitmapFactory.decodeResource(context.getResources(),R.drawable.e4);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        initData();
    }
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
    private void initData() {
        ViewWith = getMeasuredWidth();
        ViewHeight = getMeasuredHeight();
        playerbitmap0 = getRatioBitmap(playerbitmap0,2.5f,2.5f);
        playerbitmap1 = getRatioBitmap(playerbitmap1,2.5f,2.5f);
        playerbitmap2 = getRatioBitmap(playerbitmap2,2.5f,2.5f);
        playerbitmap3 = getRatioBitmap(playerbitmap3,2.5f,2.5f);
        playerbitmap4 = getRatioBitmap(playerbitmap4,2.5f,2.5f);
        playerbitmap5 = getRatioBitmap(playerbitmap5,2.5f,2.5f);
        weaponbitmap = getRatioBitmap(weaponbitmap,2.0f,2.0f);
        bulletBitmap = getRatioBitmap(bulletBitmap,1.0f,1.0f);
        enemybitmap0 = getRatioBitmap(enemybitmap0,2.5f,2.5f);
        enemybitmap1 = getRatioBitmap(enemybitmap1,2.5f,2.5f);
        enemybitmap2 = getRatioBitmap(enemybitmap2,2.5f,2.5f);
        enemybitmap3 = getRatioBitmap(enemybitmap3,2.5f,2.5f);
        enemybitmap4 = getRatioBitmap(enemybitmap4,2.5f,2.5f);
        initGame();
    }
    private void initGame(){
        background = new Entity(background_game,0,0,background_game.getWidth(),background_game.getHeight());
        player = new Figure(playerbitmap0, (float) ViewWith /2, (float) ViewHeight /2, playerbitmap0.getWidth(), playerbitmap0.getHeight(), 10, true);
        weapon = new Entity(weaponbitmap, (float) ViewWith /2, (float) ViewHeight /2,weaponbitmap.getWidth(),weaponbitmap.getHeight());
        int numberOfEnemies = 5;
        enemies.clear();
        cameraOffsetX = 0;
        cameraOffsetY = 0;
        killCount = 0;
        for (int i = 0; i < numberOfEnemies; i++) {

            int randomX = random.nextInt(ViewWith - enemybitmap0.getWidth());
            int randomY = random.nextInt(ViewHeight - enemybitmap0.getHeight());
            Figure enemy = new Figure(enemybitmap0, randomX, randomY, enemybitmap0.getWidth(), enemybitmap0.getHeight(), 10, true);
            enemies.add(enemy);
        }

        gameThread = new Thread(this);
        gameThread.start();
    }

    public Bitmap getRatioBitmap(Bitmap bitmap, float dx, float dy){
        return Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*dx), (int)(bitmap.getHeight()*dy), true);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        isDraw = false;

        // release Thread
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gameThread = null;
        }

        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
            backgroundMusic = null;
        }

        if (background_game != null) {
            background_game.recycle();
            background_game = null;
        }
        if (playerbitmap0 != null) {
            playerbitmap0.recycle();
            playerbitmap0 = null;
        }
        if (playerbitmap1 != null) {
            playerbitmap1.recycle();
            playerbitmap1 = null;
        }
        if (playerbitmap2 != null) {
            playerbitmap2.recycle();
            playerbitmap2 = null;
        }
        if (playerbitmap3 != null) {
            playerbitmap3.recycle();
            playerbitmap3 = null;
        }
        if (playerbitmap4 != null) {
            playerbitmap4.recycle();
            playerbitmap4 = null;
        }
        if (playerbitmap5 != null) {
            playerbitmap5.recycle();
            playerbitmap5 = null;
        }
        if (weaponbitmap != null) {
            weaponbitmap.recycle();
            weaponbitmap = null;
        }
        if (bulletBitmap != null) {
            bulletBitmap.recycle();
            bulletBitmap = null;
        }
        if (enemybitmap0 != null) {
            enemybitmap0.recycle();
            enemybitmap0 = null;
        }
        if (enemybitmap1 != null) {
            enemybitmap1.recycle();
            enemybitmap1 = null;
        }
        if (enemybitmap2 != null) {
            enemybitmap2.recycle();
            enemybitmap2 = null;
        }
        if (enemybitmap3 != null) {
            enemybitmap3.recycle();
            enemybitmap3 = null;
        }
        if (enemybitmap4 != null) {
            enemybitmap4.recycle();
            enemybitmap4 = null;
        }
    }

    private final int TARGET_FPS = 60;
    private final int FRAME_PERIOD = 1000 / TARGET_FPS;
    @Override
    public void run() {
        long startTime;
        long timeDiff;
        long sleepTime;
        while (isDraw) {
            startTime = System.currentTimeMillis();

            // draw
            drawMain();

            //update
            if (GameState.equals(States.Playing)) {
                update();
            }

            timeDiff = System.currentTimeMillis() - startTime;
            sleepTime = FRAME_PERIOD - timeDiff;

            // Frame rate control
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private float bulletAngle;
    private void update(){
        playerMove();
        enemyMove();
        weaponFire();
        updateBullets();
        updateEnemy();
        updateDamage();
    }
    int count = 0;
    private void updateEnemy() {
        boolean allEnemiesDead = true;

        for (Figure enemy : enemies) {
            if (enemy.getHealth() > 0) {
                allEnemiesDead = false;
                break;
            }
        }

        if (allEnemiesDead) {
            enemies.clear();
            count++;
            respawnEnemies();
        }
    }
    private void respawnEnemies() {
        int numberOfEnemies = 5 + count;
        for (int i = 0; i < numberOfEnemies; i++) {
            //random generate
            int randomX = random.nextInt(ViewWith - enemybitmap0.getWidth());
            int randomY = random.nextInt(ViewHeight - enemybitmap0.getHeight());
            Figure enemy = new Figure(enemybitmap0, randomX, randomY, enemybitmap0.getWidth(), enemybitmap0.getHeight(), 10, true);
            enemies.add(enemy);
        }
    }


    private HashMap<Figure, Integer> EnemyDamageInterval = new HashMap<>();
    private void updateDamage(){
        for (Figure enemy : enemies){
            if (enemy.getHealth() <= 0){
                continue;
            }
            if (player.getHealth() <= 0){
                continue;
            }
            if (!Collision(player,enemy)){
                continue;
            }
            int Interval = EnemyDamageInterval.getOrDefault(enemy,0);
            if (Interval > 0){
                Interval -= 1;
                EnemyDamageInterval.put(enemy,Interval);
                continue;
            }
            player.setHealth(player.getHealth() - 1);
            EnemyDamageInterval.put(enemy,60);
            if (player.getHealth() <= 0 ){
                GameState = States.Finish;
            }
        }
    }
    private int bulletFireInterval = 15;
    private int bulletFireCounter = 0;

    private void updateBullets() {
        float bulletSpeed = 10.0f;
        List<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            float dx = (float) Math.cos(bullet.Angle) * bulletSpeed;
            float dy = (float) Math.sin(bullet.Angle) * bulletSpeed;

            bullet.setX((int) (bullet.getX() + dx));
            bullet.setY((int) (bullet.getY() + dy));

            // out of view
            if (bullet.getX() < 0 || bullet.getX() > ViewWith || bullet.getY() < 0 || bullet.getY() > ViewHeight) {
                bulletsToRemove.add(bullet);  // add to remove list
            }

            for (Figure enemy : enemies) {
                if (enemy.getHealth() > 0 && isBulletCollidingWithEnemy(bullet, enemy)) {

                    enemy.setHealth(enemy.getHealth() - bullet.getDamage());
                    if (enemy.getHealth() <= 0) {
                        killCount++;
                    }
                    // add to remove list
                    bulletsToRemove.add(bullet);
                    break;
                }
            }
        }

        //destroy bullet
        bullets.removeAll(bulletsToRemove);
    }

    private boolean isBulletCollidingWithEnemy(Bullet bullet, Figure enemy) {
        return bullet.getX() < enemy.getX() + enemy.getWidth() &&
                bullet.getX() + bullet.getWidth() > enemy.getX() &&
                bullet.getY() < enemy.getY() + enemy.getHeight() &&
                bullet.getY() + bullet.getHeight() > enemy.getY();
    }
    private boolean Collision(Figure A, Figure B){
        double Distance = Math.sqrt(Math.pow(B.getX() - A.getX(), 2) + Math.pow(B.getY() - A.getY(), 2));
        double AverageD = (A.getWidth() + A.getHeight()) / 4f + (B.getWidth() + B.getHeight()) / 4f;
        return Distance <= AverageD;
    }


    private void weaponFire() {
        Figure nearestEnemy = findNearestEnemy();
        if (nearestEnemy != null) {
            //calculate Angle
            float dx = nearestEnemy.getX() - player.getX();
            float dy = nearestEnemy.getY() - player.getY();
            bulletAngle = (float) Math.atan2(dy, dx);

            fireBullet(bulletAngle);
        }
    }

    private Figure findNearestEnemy() {
        Figure nearestEnemy = null;
        float nearestDistance = Float.MAX_VALUE;

        for (Figure enemy : enemies) {
            if (enemy.getHealth() > 0) {
                float distance = calculateDistance(player.getX(), player.getY(), enemy.getX(), enemy.getY());
                if (distance < nearestDistance) {
                    nearestEnemy = enemy;
                    nearestDistance = distance;
                }
            }
        }
        return nearestEnemy;
    }
    private float calculateDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    int enemyChangeBitmapTime = 0;
    int enemyBitmapData = 0;
    private HashMap<Figure, float[]> enemyDirections = new HashMap<>();



    private void enemyMove() {
        float minSpeed = -5.0f;
        float maxSpeed = 5.0f;
        int directionChangeInterval = 60;

        for (Figure enemy : enemies) {
            if (enemy.getHealth() > 0) {

                if (enemyChangeBitmapTime % directionChangeInterval == 0 || !enemyDirections.containsKey(enemy)) {

                    float dx = minSpeed + (random.nextFloat() * (maxSpeed - minSpeed));
                    float dy = minSpeed + (random.nextFloat() * (maxSpeed - minSpeed));

                    enemyDirections.put(enemy, new float[]{dx, dy});
                }

                float[] direction = enemyDirections.get(enemy);
                float dx = 0;
                float dy = 0;
                if (direction != null) {
                    dx = direction[0];
                    dy = direction[1];
                }

                //update position
                enemy.setX(enemy.getX() + (int) dx);
                enemy.setY(enemy.getY() + (int) dy);

                if (dx > 0) {
                    enemy.setFacingRight(true);
                } else if (dx < 0) {
                    enemy.setFacingRight(false);
                }

                if (enemy.getX() <= 0) {
                    enemy.setX(0);
                    dx = Math.abs(dx);
                }
                if (enemy.getX() + enemy.getWidth() >= ViewWith) {
                    enemy.setX(ViewWith - enemy.getWidth());
                    dx = -Math.abs(dx);
                }
                if (enemy.getY() <= 0) {
                    enemy.setY(0);
                    dy = Math.abs(dy);
                }
                if (enemy.getY() + enemy.getHeight() >= ViewHeight) {
                    enemy.setY(ViewHeight - enemy.getHeight());
                    dy = -Math.abs(dy);
                }

                // update direction
                enemyDirections.put(enemy, new float[]{dx, dy});

                // enemy animation
                enemyChangeBitmapTime++;
                if (enemyChangeBitmapTime % 2 == 0) {
                    enemyBitmapData = (enemyBitmapData % 4) + 1;
                    switch (enemyBitmapData) {
                        case 1:
                            enemy.setBitmap(enemybitmap1);
                            break;
                        case 2:
                            enemy.setBitmap(enemybitmap2);
                            break;
                        case 3:
                            enemy.setBitmap(enemybitmap3);
                            break;
                        default:
                            enemy.setBitmap(enemybitmap0);
                            break;
                    }
                }
            } else if (enemy.getHealth() <= 0) {
                //enemy down
                enemy.setBitmap(enemybitmap4);
            }
        }
    }


    private void drawMain() {
        mCanvas = surfaceHolder.lockCanvas();
        if (GameState==States.Playing || GameState==States.Pause){
            drawBackground();
            drawPlayer();
            drawWeapon();
            drawBullet();
            drawEnemy();
            drawKillCount();
            drawPauseButton();
        }
        if (GameState == States.Pause){
            drawPause();
        }
        if (GameState == States.Start){
            drawStart();
        }
        if (GameState == States.Finish){
            drawFinish();
        }
        surfaceHolder.unlockCanvasAndPost(mCanvas);
    }

    private void drawFinish() {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        mCanvas.drawColor(Color.LTGRAY);

        int buttonWidth = 300;
        int buttonHeight = 100;

        int restartButtonX = (getWidth() - buttonWidth) / 2;
        int restartButtonY = getHeight() / 2 - buttonHeight - 20;

        int backButtonX = (getWidth() - buttonWidth) / 2;
        int backButtonY = getHeight() / 2 + 20;

        paint.setColor(Color.GREEN);
        mCanvas.drawRect(restartButtonX, restartButtonY, restartButtonX + buttonWidth, restartButtonY + buttonHeight, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        mCanvas.drawText("Restart", restartButtonX + (buttonWidth / 4), restartButtonY + (buttonHeight / 2) + 10, paint);

        paint.setColor(Color.BLUE);
        mCanvas.drawRect(backButtonX, backButtonY, backButtonX + buttonWidth, backButtonY + buttonHeight, paint);
        paint.setColor(Color.WHITE);
        mCanvas.drawText("Back to Start", backButtonX + (buttonWidth / 12), backButtonY + (buttonHeight / 2) + 10, paint);
    }


    private void drawStart() {
        mCanvas.drawColor(Color.BLACK);

        Paint startPaint = new Paint();
        startPaint.setColor(Color.WHITE);
        startPaint.setTextSize(100);
        startPaint.setTextAlign(Paint.Align.CENTER);

        mCanvas.drawText("Welcome to My Game!", mCanvas.getWidth() / 2, mCanvas.getHeight() / 2 - 100, startPaint);

        startPaint.setTextSize(50);
        mCanvas.drawText("Tap to Start", mCanvas.getWidth() / 2, mCanvas.getHeight() / 2 + 50, startPaint);

        int buttonWidth = 400;
        int buttonHeight = 150;
        int buttonX = (mCanvas.getWidth() - buttonWidth) / 2;
        int buttonY = mCanvas.getHeight() / 2 + 150;

        startPaint.setColor(Color.BLUE);
        mCanvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, startPaint);

        startPaint.setColor(Color.WHITE);
        startPaint.setTextSize(80);
        mCanvas.drawText("Start", mCanvas.getWidth() / 2, buttonY + buttonHeight / 2 + 30, startPaint);
    }
    private void drawPauseButton() {
        Paint paint = new Paint();
        paint.setColor(Color.RED);

        int buttonWidth = 150;
        int buttonHeight = 150;

        int buttonX = getWidth() - buttonWidth - 50;
        int buttonY = 50;

        mCanvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, paint);

        paint.setColor(Color.WHITE);
        int barWidth = 20;
        int barHeight = 80;

        mCanvas.drawRect(buttonX + (buttonWidth / 4) - (barWidth / 2),
                buttonY + (buttonHeight / 2) - (barHeight / 2),
                buttonX + (buttonWidth / 4) + (barWidth / 2),
                buttonY + (buttonHeight / 2) + (barHeight / 2), paint);

        mCanvas.drawRect(buttonX + (3 * buttonWidth / 4) - (barWidth / 2),
                buttonY + (buttonHeight / 2) - (barHeight / 2),
                buttonX + (3 * buttonWidth / 4) + (barWidth / 2),
                buttonY + (buttonHeight / 2) + (barHeight / 2), paint);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Finish Button
        if (GameState == States.Finish && event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int buttonWidth = 300;
            int buttonHeight = 100;
            int restartButtonX = (getWidth() - buttonWidth) / 2;
            int restartButtonY = getHeight() / 2 - buttonHeight - 20;

            if (x >= restartButtonX && x <= restartButtonX + buttonWidth &&
                    y >= restartButtonY && y <= restartButtonY + buttonHeight) {
                initGame();
                GameState = States.Playing;
            }

            int backButtonX = (getWidth() - buttonWidth) / 2;
            int backButtonY = getHeight() / 2 + 20;

            if (x >= backButtonX && x <= backButtonX + buttonWidth &&
                    y >= backButtonY && y <= backButtonY + buttonHeight) {
                initGame();
                GameState = States.Start;
            }
        }

        //Playing Button
        if (GameState == States.Playing && event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int buttonWidth = 150;
            int buttonHeight = 150;
            int buttonX = getWidth() - buttonWidth - 50;
            int buttonY = 50;

            if (x >= buttonX && x <= buttonX + buttonWidth && y >= buttonY && y <= buttonY + buttonHeight) {

                if (GameState == States.Playing) {
                    GameState = States.Pause;
                } else if (GameState == States.Pause) {
                    GameState = States.Playing;
                }
            }
        }
        //Start Button
        if (GameState == States.Start && event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int buttonWidth = 400;
            int buttonHeight = 150;
            int buttonX = (getWidth() - buttonWidth) / 2;
            int buttonY = getHeight() / 2 + 150;

            if (x >= buttonX && x <= buttonX + buttonWidth && y >= buttonY && y <= buttonY + buttonHeight) {
                // GameState change
                initGame();
                GameState = States.Playing;
            }
        }

        //Pause Button
        if (GameState == States.Pause && event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int buttonWidth = 400;
            int buttonHeight = 150;
            int buttonX = (getWidth() - buttonWidth) / 2;
            int buttonY = getHeight() / 2 + 50;

            if (x >= buttonX && x <= buttonX + buttonWidth && y >= buttonY && y <= buttonY + buttonHeight) {
                GameState = States.Playing;
            }
        }

        return true;
    }
    private void drawPause() {
        Paint pausePaint = new Paint();

        pausePaint.setColor(Color.BLACK);
        pausePaint.setAlpha(128);
        mCanvas.drawRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight(), pausePaint);

        pausePaint.setColor(Color.WHITE);
        pausePaint.setAlpha(255);
        pausePaint.setTextSize(100);
        pausePaint.setTextAlign(Paint.Align.CENTER);
        mCanvas.drawText("Game Paused", mCanvas.getWidth() / 2, mCanvas.getHeight() / 2 - 100, pausePaint);

        int buttonWidth = 400;
        int buttonHeight = 150;
        int buttonX = (mCanvas.getWidth() - buttonWidth) / 2;
        int buttonY = mCanvas.getHeight() / 2 + 50;

        pausePaint.setColor(Color.BLUE);
        mCanvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, pausePaint);

        pausePaint.setColor(Color.WHITE);
        pausePaint.setTextSize(80);
        mCanvas.drawText("Resume", mCanvas.getWidth() / 2, buttonY + buttonHeight / 2 + 30, pausePaint);
    }



    private void drawKillCount() {

        String HealthText = "Health: " + player.getHealth();
        String killText = "KILLS: " + killCount;
        mCanvas.drawText(HealthText, 200, 100, textPaint);
        mCanvas.drawText(killText, (float) ViewWith / 2, 100, textPaint);
    }

    private void drawEnemy() {
        for (Figure enemy : enemies) {
            Matrix matrix = new Matrix();

            if (!enemy.isFacingRight()) {
                matrix.preScale(-1.0f, 1.0f, (float) enemy.getBitmap().getWidth() / 2, (float) enemy.getBitmap().getHeight() / 2);  // 基于中心点翻转
            }

            matrix.setTranslate(enemy.getX() - cameraOffsetX - enemy.getWidth() / 2, enemy.getY() - cameraOffsetY - enemy.getHeight() / 2);


            mCanvas.drawBitmap(enemy.getBitmap(), matrix, null);
        }
    }

    int playerBitmapData = 0;
    int changeBitmapTime = 0;
    private void playerMove() {
        float angle = gameController.getAngle();  // Angle
        float strength = gameController.getStrength();  // Strength

        if (strength > 0) {
            float speed = 5.0f * strength;
            float dx = (float) Math.cos(angle) * speed;
            float dy = (float) Math.sin(angle) * speed;

            // change position
            player.setX(player.getX() + dx);
            player.setY(player.getY() + dy);
            cameraOffsetX += dx;
            cameraOffsetY += dy;

            // direction
            if (dx > 0) {
                player.setFacingRight(true);
            } else if (dx < 0) {
                player.setFacingRight(false);
            }

            // Animation
            changeBitmapTime++;
            if (changeBitmapTime % 2 == 0) {
                playerBitmapData = (playerBitmapData % 4) + 1;
            }
        } else {
            playerBitmapData = 0;
        }

        switch (playerBitmapData) {
            case 0:
                player.setBitmap(playerbitmap0);
                break;
            case 1:
                player.setBitmap(playerbitmap1);
                break;
            case 2:
                player.setBitmap(playerbitmap2);
                break;
            case 3:
                player.setBitmap(playerbitmap3);
                break;
            case 4:
                player.setBitmap(playerbitmap4);
                break;
        }

        if (player.getHealth() <= 0) {
            player.setBitmap(playerbitmap5);
        }
    }

    private void drawPlayer() {
        Matrix matrix = new Matrix();

        matrix.setTranslate(player.getX() - cameraOffsetX - player.getWidth() / 2, player.getY() - cameraOffsetY - player.getHeight() / 2);

        if (!player.isFacingRight()) {
            matrix.preScale(-1.0f, 1.0f, (float) player.getBitmap().getWidth() / 2, (float) player.getBitmap().getHeight() / 2);
        }

        mCanvas.drawBitmap(player.getBitmap(), matrix, null);
    }


    private void drawBackground() {
        mCanvas.drawBitmap(background.getBitmap(), background.getX() - cameraOffsetX, background.getY() - cameraOffsetY, null);
    }

    private void drawWeapon() {
        Figure nearestEnemy = findNearestEnemy();
        if (nearestEnemy != null) {

            float dx = nearestEnemy.getX() - player.getX();
            float dy = nearestEnemy.getY() - player.getY();
            float weaponAngle = (float) Math.atan2(dy, dx);

            float weaponAngleDegrees = (float) Math.toDegrees(weaponAngle);

            Matrix matrix = new Matrix();

            float playerX = player.getX();
            float playerY = player.getY();
            float weaponX = playerX + player.getWidth()/2;
            float weaponY = playerY;
            weapon.setX(weaponX);
            weapon.setY(weaponY);

            matrix.setTranslate(weaponX - cameraOffsetX , weaponY - cameraOffsetY  );  // 将武器放在角色头上 + weapon.getHeight() * 2

            weaponAngleDegrees = normalizeAngle(weaponAngleDegrees);
            if (weaponAngleDegrees > 90 && weaponAngleDegrees < 270) {

                matrix.preScale(1.0f, -1.0f, weapon.getWidth() / 2, weapon.getHeight() / 2);
            }

            matrix.postRotate(weaponAngleDegrees, playerX - cameraOffsetX , playerY - cameraOffsetY);

            mCanvas.drawBitmap(weapon.getBitmap(), matrix, null);
        }
    }

    private float normalizeAngle(float angle) {
        return (angle % 360 + 360) % 360;
    }



    private void fireBullet(float initBulletAngle) {
        bulletFireCounter++;
        if (bulletFireCounter >= bulletFireInterval) {

            float bulletStartX = weapon.getX() + (float) Math.cos(initBulletAngle) * weapon.getWidth();
            float bulletStartY = weapon.getY() + (float) Math.sin(initBulletAngle) * weapon.getHeight();

            Bullet newBullet = new Bullet(bulletBitmap,bulletStartX,bulletStartY, bulletBitmap.getWidth(), bulletBitmap.getHeight(), 1,initBulletAngle);


            bullets.add(newBullet);


            bulletFireCounter = 0;
        }
    }


    private void drawBullet() {
        for (Bullet bullet : bullets) {
            mCanvas.drawBitmap(bullet.getBitmap(), bullet.getX() - cameraOffsetX - bullet.getWidth()/2 , bullet.getY() - cameraOffsetY - bullet.getHeight()/2, null);
        }
    }
}
