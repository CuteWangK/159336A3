package com.example.myapplication3;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
    private Bitmap enemybitmap0,enemybitmap1,enemybitmap2,enemybitmap3,enemybitmap4, enemybullet;
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
    private States GameState = States.Start;

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
        textPaint.setColor(Color.WHITE); // 设置文本颜色
        textPaint.setTextSize(60);  // 设置文本大小
        textPaint.setTextAlign(Paint.Align.CENTER);  // 文本对齐方式设置为居中
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
        if (enemies.isEmpty()) {
            for (int i = 0; i < numberOfEnemies; i++) {

                int randomX = random.nextInt(ViewWith - enemybitmap0.getWidth());
                int randomY = random.nextInt(ViewHeight - enemybitmap0.getHeight());
                Figure enemy = new Figure(enemybitmap0, randomX, randomY, enemybitmap0.getWidth(), enemybitmap0.getHeight(), 10, true);
                enemies.add(enemy);
            }
        }
        new Thread(this).start();
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

            // 绘制和更新游戏
            drawMain();
            if (GameState.equals(States.Playing))
                update();

            // 计算帧时间和休眠时间
            timeDiff = System.currentTimeMillis() - startTime;
            sleepTime = FRAME_PERIOD - timeDiff;

            // 如果时间不足，等待一段时间
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
            respawnEnemies();  // 重新生成敌人
        }
    }
    private void respawnEnemies() {
        int numberOfEnemies = 5 + count;  // 可以根据需要调整生成敌人的数量
        for (int i = 0; i < numberOfEnemies; i++) {
            // 在随机位置生成敌人
            int randomX = random.nextInt(ViewWith - enemybitmap0.getWidth());
            int randomY = random.nextInt(ViewHeight - enemybitmap0.getHeight());
            Figure enemy = new Figure(enemybitmap0, randomX, randomY, enemybitmap0.getWidth(), enemybitmap0.getHeight(), 10, true);
            enemies.add(enemy);  // 添加敌人到集合中
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
    private int bulletFireInterval = 15;  // 每30帧发射一次子弹
    private int bulletFireCounter = 0;    // 子弹发射计数器

    private void updateBullets() {
        float bulletSpeed = 10.0f;  // 子弹的速度
        List<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            // 子弹方向通过Game类管理，而不是在Bullet类中
            float dx = (float) Math.cos(bullet.Angle) * bulletSpeed;
            float dy = (float) Math.sin(bullet.Angle) * bulletSpeed;

            // 更新子弹的位置
            bullet.setX((int) (bullet.getX() + dx));
            bullet.setY((int) (bullet.getY() + dy));

            // 检查子弹是否超出屏幕范围
            if (bullet.getX() < 0 || bullet.getX() > ViewWith || bullet.getY() < 0 || bullet.getY() > ViewHeight) {
                bulletsToRemove.add(bullet);  // 标记待移除的子弹
            }

            // 检查子弹与敌人的碰撞
            for (Figure enemy : enemies) {
                if (enemy.getHealth() > 0 && isBulletCollidingWithEnemy(bullet, enemy)) {
                    // 减少敌人的健康值
                    enemy.setHealth(enemy.getHealth() - bullet.getDamage());
                    if (enemy.getHealth() <= 0) {
                        killCount++;  // 增加击杀数量
                    }
                    // 子弹击中敌人后消失
                    bulletsToRemove.add(bullet);
                    break;
                }
            }
        }

        // 移除被标记的子弹
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
            // 计算武器指向最近敌人的角度
            float dx = nearestEnemy.getX() - player.getX();
            float dy = nearestEnemy.getY() - player.getY();
            bulletAngle = (float) Math.atan2(dy, dx);  // 子弹的发射角度是根据最近敌人计算的

            // 发射子弹
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
        float minSpeed = -5.0f;  // 最小速度
        float maxSpeed = 5.0f;   // 最大速度
        int directionChangeInterval = 60;  // 每60帧改变一次方向

        for (Figure enemy : enemies) {
            if (enemy.getHealth() > 0) {
                // 如果当前帧数达到方向变化的间隔，重新生成方向
                if (enemyChangeBitmapTime % directionChangeInterval == 0 || !enemyDirections.containsKey(enemy)) {
                    // 为每个敌人生成随机的 X 和 Y 移动速度（范围从 -5 到 5）
                    float dx = minSpeed + (random.nextFloat() * (maxSpeed - minSpeed));
                    float dy = minSpeed + (random.nextFloat() * (maxSpeed - minSpeed));

                    // 将敌人的移动方向存入 HashMap 中
                    enemyDirections.put(enemy, new float[]{dx, dy});
                }

                // 获取敌人的当前移动方向
                float[] direction = enemyDirections.get(enemy);
                float dx = 0;
                float dy = 0;
                if (direction != null) {
                    dx = direction[0];
                    dy = direction[1];
                }


                // 更新敌人位置
                enemy.setX(enemy.getX() + (int) dx);
                enemy.setY(enemy.getY() + (int) dy);

                // **根据当前dx的值，判断敌人朝向**
                if (dx > 0) {
                    enemy.setFacingRight(true);  // 向右
                } else if (dx < 0) {
                    enemy.setFacingRight(false); // 向左
                }

                // 边界检查，防止敌人移出屏幕并改变方向
                if (enemy.getX() <= 0) {
                    enemy.setX(0);
                    dx = Math.abs(dx);  // 反转 X 方向，向右移动
                }
                if (enemy.getX() + enemy.getWidth() >= ViewWith) {
                    enemy.setX(ViewWith - enemy.getWidth());
                    dx = -Math.abs(dx);  // 反转 X 方向，向左移动
                }
                if (enemy.getY() <= 0) {
                    enemy.setY(0);
                    dy = Math.abs(dy);  // 反转 Y 方向，向下移动
                }
                if (enemy.getY() + enemy.getHeight() >= ViewHeight) {
                    enemy.setY(ViewHeight - enemy.getHeight());
                    dy = -Math.abs(dy);  // 反转 Y 方向，向上移动
                }

                // 更新方向存储
                enemyDirections.put(enemy, new float[]{dx, dy});

                // 敌人动画逻辑
                enemyChangeBitmapTime++;
                if (enemyChangeBitmapTime % 2 == 0) {  // 每6帧切换一次图片
                    enemyBitmapData = (enemyBitmapData % 4) + 1;  // 动画帧的切换（1到4）
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
                enemy.setBitmap(enemybitmap4);  // 死亡图片
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
        paint.setColor(Color.BLACK); // 背景颜色
        mCanvas.drawColor(Color.LTGRAY); // 设置完成页面的背景颜色

        // 设置按钮的宽度和高度
        int buttonWidth = 300;
        int buttonHeight = 100;

        // 计算按钮的 X 和 Y 坐标
        int restartButtonX = (getWidth() - buttonWidth) / 2; // 居中
        int restartButtonY = getHeight() / 2 - buttonHeight - 20; // 上面的按钮

        int backButtonX = (getWidth() - buttonWidth) / 2; // 居中
        int backButtonY = getHeight() / 2 + 20; // 下面的按钮

        // 绘制重启按钮
        paint.setColor(Color.GREEN); // 设置重启按钮颜色
        mCanvas.drawRect(restartButtonX, restartButtonY, restartButtonX + buttonWidth, restartButtonY + buttonHeight, paint);
        paint.setColor(Color.WHITE); // 设置字体颜色为白色
        paint.setTextSize(40);
        mCanvas.drawText("Restart", restartButtonX + (buttonWidth / 4), restartButtonY + (buttonHeight / 2) + 10, paint);

        // 绘制返回开始按钮
        paint.setColor(Color.BLUE); // 设置返回开始按钮颜色
        mCanvas.drawRect(backButtonX, backButtonY, backButtonX + buttonWidth, backButtonY + buttonHeight, paint);
        paint.setColor(Color.WHITE); // 设置字体颜色为白色
        mCanvas.drawText("Back to Start", backButtonX + (buttonWidth / 12), backButtonY + (buttonHeight / 2) + 10, paint);
    }


    private void drawStart() {
        // 绘制背景颜色
        mCanvas.drawColor(Color.BLACK); // 设置背景为黑色

        // 创建画笔
        Paint startPaint = new Paint();
        startPaint.setColor(Color.WHITE); // 设置文本颜色为白色
        startPaint.setTextSize(100);  // 设置标题文本大小
        startPaint.setTextAlign(Paint.Align.CENTER);  // 文本对齐方式设置为居中

        // 绘制游戏标题
        mCanvas.drawText("Welcome to My Game!", mCanvas.getWidth() / 2, mCanvas.getHeight() / 2 - 100, startPaint);

        // 绘制说明文本
        startPaint.setTextSize(50); // 设置说明文本大小
        mCanvas.drawText("Tap to Start", mCanvas.getWidth() / 2, mCanvas.getHeight() / 2 + 50, startPaint);

        // 绘制开始按钮
        int buttonWidth = 400;
        int buttonHeight = 150;
        int buttonX = (mCanvas.getWidth() - buttonWidth) / 2;
        int buttonY = mCanvas.getHeight() / 2 + 150;

        // 绘制按钮矩形
        startPaint.setColor(Color.BLUE);
        mCanvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, startPaint);

        // 绘制按钮上的文本
        startPaint.setColor(Color.WHITE);
        startPaint.setTextSize(80);
        mCanvas.drawText("Start", mCanvas.getWidth() / 2, buttonY + buttonHeight / 2 + 30, startPaint);
    }
    private void drawPauseButton() {
        Paint paint = new Paint();
        paint.setColor(Color.RED); // 设置按钮颜色为红色

        // 定义按钮的宽度和高度
        int buttonWidth = 150;
        int buttonHeight = 150;

        // 定义按钮的位置（右上角）
        int buttonX = getWidth() - buttonWidth - 50; // 距离右边的距离
        int buttonY = 50; // 距离顶部的距离

        // 绘制按钮的矩形
        mCanvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, paint);

        // 绘制暂停符号（两个竖条）
        paint.setColor(Color.WHITE); // 设置符号颜色为白色
        int barWidth = 20;
        int barHeight = 80;

        // 绘制左边的竖条
        mCanvas.drawRect(buttonX + (buttonWidth / 4) - (barWidth / 2),
                buttonY + (buttonHeight / 2) - (barHeight / 2),
                buttonX + (buttonWidth / 4) + (barWidth / 2),
                buttonY + (buttonHeight / 2) + (barHeight / 2), paint);

        // 绘制右边的竖条
        mCanvas.drawRect(buttonX + (3 * buttonWidth / 4) - (barWidth / 2),
                buttonY + (buttonHeight / 2) - (barHeight / 2),
                buttonX + (3 * buttonWidth / 4) + (barWidth / 2),
                buttonY + (buttonHeight / 2) + (barHeight / 2), paint);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (GameState == States.Finish && event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int buttonWidth = 300;
            int buttonHeight = 100;
            int restartButtonX = (getWidth() - buttonWidth) / 2;
            int restartButtonY = getHeight() / 2 - buttonHeight - 20;

            if (x >= restartButtonX && x <= restartButtonX + buttonWidth &&
                    y >= restartButtonY && y <= restartButtonY + buttonHeight) {
                GameState = States.Playing;
                initGame();
            }

            int backButtonX = (getWidth() - buttonWidth) / 2;
            int backButtonY = getHeight() / 2 + 20;

            if (x >= backButtonX && x <= backButtonX + buttonWidth &&
                    y >= backButtonY && y <= backButtonY + buttonHeight) {

                GameState = States.Start;
            }
        }
        if (GameState == States.Playing && event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            // 定义暂停按钮的位置
            int buttonWidth = 150;
            int buttonHeight = 150;
            int buttonX = getWidth() - buttonWidth - 50; // 距离右边的距离
            int buttonY = 50; // 距离顶部的距离

            // 检查是否触摸了暂停按钮的区域
            if (x >= buttonX && x <= buttonX + buttonWidth && y >= buttonY && y <= buttonY + buttonHeight) {
                // 用户点击了按钮，切换到暂停状态
                if (GameState == States.Playing) {
                    GameState = States.Pause; // 切换到暂停状态
                } else if (GameState == States.Pause) {
                    GameState = States.Playing; // 切换回游戏状态
                }
            }
        }
        if (GameState == States.Start && event.getAction() == MotionEvent.ACTION_DOWN) {
            // 获取触摸的 x 和 y 坐标
            float x = event.getX();
            float y = event.getY();

            // 检查是否触摸了开始按钮的区域
            int buttonWidth = 400;
            int buttonHeight = 150;
            int buttonX = (getWidth() - buttonWidth) / 2;
            int buttonY = getHeight() / 2 + 150; // 确保与 drawStart 中的 Y 坐标一致

            if (x >= buttonX && x <= buttonX + buttonWidth && y >= buttonY && y <= buttonY + buttonHeight) {
                // 用户点击了按钮，开始游戏
                GameState = States.Playing;
                initGame();
            }
        }
        if (GameState == States.Pause && event.getAction() == MotionEvent.ACTION_DOWN) {
            // 获取触摸的 x 和 y 坐标
            float x = event.getX();
            float y = event.getY();

            // 检查是否触摸了按钮的区域
            int buttonWidth = 400;
            int buttonHeight = 150;
            int buttonX = (getWidth() - buttonWidth) / 2;
            int buttonY = getHeight() / 2 + 50;

            if (x >= buttonX && x <= buttonX + buttonWidth && y >= buttonY && y <= buttonY + buttonHeight) {
                // 用户点击了按钮，恢复游戏
                GameState = States.Playing;
            }
        }

        return true;
    }
    private void drawPause() {
        Paint pausePaint = new Paint();

        pausePaint.setColor(Color.BLACK);
        pausePaint.setAlpha(128);  // 设置半透明
        mCanvas.drawRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight(), pausePaint);

        pausePaint.setColor(Color.WHITE);
        pausePaint.setAlpha(255);  // 设置为不透明
        pausePaint.setTextSize(100);
        pausePaint.setTextAlign(Paint.Align.CENTER);
        mCanvas.drawText("Game Paused", mCanvas.getWidth() / 2, mCanvas.getHeight() / 2 - 100, pausePaint);

        // 3. 绘制按钮
        int buttonWidth = 400;
        int buttonHeight = 150;
        int buttonX = (mCanvas.getWidth() - buttonWidth) / 2;
        int buttonY = mCanvas.getHeight() / 2 + 50;

        // 绘制按钮矩形
        pausePaint.setColor(Color.BLUE);
        mCanvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, pausePaint);

        // 绘制按钮上的文本
        pausePaint.setColor(Color.WHITE);
        pausePaint.setTextSize(80);
        mCanvas.drawText("Resume", mCanvas.getWidth() / 2, buttonY + buttonHeight / 2 + 30, pausePaint);
    }



    private void drawKillCount() {
        String HealthText = "Health: " + player.getHealth();
        String killText = "KILLS: " + killCount;
        // 在屏幕顶部中间绘制击杀数量
        mCanvas.drawText(HealthText, 200, 100, textPaint);
        mCanvas.drawText(killText, (float) ViewWith / 2, 100, textPaint);
    }

    private void drawEnemy() {
        for (Figure enemy : enemies) {
            Matrix matrix = new Matrix();

            // 先进行翻转操作，再设置平移
            if (!enemy.isFacingRight()) {
                matrix.preScale(-1.0f, 1.0f, (float) enemy.getBitmap().getWidth() / 2, (float) enemy.getBitmap().getHeight() / 2);  // 基于中心点翻转
            }

            // 设置平移，将敌人绘制在正确的位置
            matrix.setTranslate(enemy.getX() - cameraOffsetX - enemy.getWidth() / 2, enemy.getY() - cameraOffsetY - enemy.getHeight() / 2);

            // 绘制当前动画帧
            mCanvas.drawBitmap(enemy.getBitmap(), matrix, null);
        }
    }

    int playerBitmapData = 0;
    int changeBitmapTime = 0;
    private void playerMove() {
        float angle = gameController.getAngle();  // 获取摇杆的角度
        float strength = gameController.getStrength();  // 获取摇杆的力度

        if (strength > 0) {
            float speed = 5.0f * strength;  // 根据摇杆力度调整速度
            float dx = (float) Math.cos(angle) * speed;
            float dy = (float) Math.sin(angle) * speed;

            // 移动玩家
            player.setX(player.getX() + dx);
            player.setY(player.getY() + dy);
            cameraOffsetX += dx;
            cameraOffsetY += dy;

            // 判断水平移动方向，并调整玩家朝向
            if (dx > 0) {
                player.setFacingRight(true);  // 朝右
            } else if (dx < 0) {
                player.setFacingRight(false); // 朝左
            }

            // 播放跑步动画
            changeBitmapTime++;
            if (changeBitmapTime % 2 == 0) {  // 每6帧切换一次图片
                playerBitmapData = (playerBitmapData % 4) + 1;
            }
        } else {
            // 如果没有移动，角色保持站立不动
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

        // 始终将角色绘制在屏幕中央，而不是基于角色的X、Y坐标
        matrix.setTranslate(player.getX() - cameraOffsetX - player.getWidth() / 2, player.getY() - cameraOffsetY - player.getHeight() / 2);

        if (!player.isFacingRight()) {
            matrix.preScale(-1.0f, 1.0f, (float) player.getBitmap().getWidth() / 2, (float) player.getBitmap().getHeight() / 2);
        }

        // 绘制玩家
        mCanvas.drawBitmap(player.getBitmap(), matrix, null);
    }


    private void drawBackground() {
        mCanvas.drawBitmap(background.getBitmap(), background.getX() - cameraOffsetX, background.getY() - cameraOffsetY, null);
    }

    private void drawWeapon() {
        Figure nearestEnemy = findNearestEnemy();  // 找到最近的敌人
        if (nearestEnemy != null) {
            // 计算武器指向最近敌人的角度
            float dx = nearestEnemy.getX() - player.getX();
            float dy = nearestEnemy.getY() - player.getY();
            float weaponAngle = (float) Math.atan2(dy, dx);  // 计算武器与敌人的夹角

            // 将武器角度从弧度转换为度数
            float weaponAngleDegrees = (float) Math.toDegrees(weaponAngle);

            // 生成一个新的矩阵，用来旋转武器
            Matrix matrix = new Matrix();

            // 获取角色的 X 和 Y 坐标
            float playerX = player.getX();
            float playerY = player.getY();
            float weaponX = playerX + player.getWidth()/2;
            float weaponY = playerY;
            weapon.setX(weaponX);
            weapon.setY(weaponY);
            // 将武器设置在角色上方
            matrix.setTranslate(weaponX - cameraOffsetX , weaponY - cameraOffsetY  );  // 将武器放在角色头上 + weapon.getHeight() * 2

            // 如果武器角度在 90° 到 270° 之间（指向左边）
            weaponAngleDegrees = normalizeAngle(weaponAngleDegrees);
            if (weaponAngleDegrees > 90 && weaponAngleDegrees < 270) {
                // 水平翻转武器图像
                matrix.preScale(1.0f, -1.0f, weapon.getWidth() / 2, weapon.getHeight() / 2);
            }

            // 旋转武器，使其指向最近敌人
            matrix.postRotate(weaponAngleDegrees, playerX - cameraOffsetX , playerY - cameraOffsetY);

            // 绘制旋转后的武器
            mCanvas.drawBitmap(weapon.getBitmap(), matrix, null);
        }
    }

    private float normalizeAngle(float angle) {
        return (angle % 360 + 360) % 360;
    }



    private void fireBullet(float initBulletAngle) {
        bulletFireCounter++;
        if (bulletFireCounter >= bulletFireInterval) {
            // 获取武器的 X 和 Y 坐标

            // 根据武器角度计算子弹的起始位置，让子弹从武器的前端发射
            float bulletStartX = weapon.getX() + (float) Math.cos(initBulletAngle) * weapon.getWidth();
            float bulletStartY = weapon.getY() + (float) Math.sin(initBulletAngle) * weapon.getHeight();
            // 创建新的子弹并将其添加到子弹列表
            Bullet newBullet = new Bullet(bulletBitmap,bulletStartX,bulletStartY, bulletBitmap.getWidth(), bulletBitmap.getHeight(), 1,initBulletAngle);

            // 将新子弹添加到子弹列表
            bullets.add(newBullet);

            // 重置子弹发射计数器
            bulletFireCounter = 0;
        }
    }


    private void drawBullet() {
        for (Bullet bullet : bullets) {
            mCanvas.drawBitmap(bullet.getBitmap(), bullet.getX() - cameraOffsetX - bullet.getWidth()/2 , bullet.getY() - cameraOffsetY - bullet.getHeight()/2, null);
        }
    }



}
