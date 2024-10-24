package com.example.myapplication3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game extends SurfaceView implements SurfaceHolder.Callback, Runnable {

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
        background = new Entity(background_game,0,0,background_game.getWidth(),background_game.getHeight());
        player = new Figure(playerbitmap0, ViewWith/2 - playerbitmap0.getWidth()/2,ViewHeight/2-playerbitmap0.getHeight()/2, playerbitmap0.getWidth(), playerbitmap0.getHeight(), 10, true);
        weapon = new Entity(weaponbitmap,ViewWith/2-weaponbitmap.getWidth()/2,ViewHeight/2-weaponbitmap.getWidth()/2,weaponbitmap.getWidth(),weaponbitmap.getHeight());
        //pBullet = new Bullet(bulletBitmap,ViewWith/2-weaponbitmap.getWidth()/2,ViewHeight/2-weaponbitmap.getWidth()/2, bulletBitmap.getWidth(),bulletBitmap.getHeight(), 1);
        int numberOfEnemies = 5;
        for (int i = 0; i < numberOfEnemies; i++) {
            // 在随机位置生成怪物
            int randomX = random.nextInt(ViewWith - enemybitmap0.getWidth());
            int randomY = random.nextInt(ViewHeight - enemybitmap0.getHeight());
            Figure enemy = new Figure(enemybitmap0, randomX, randomY, enemybitmap0.getWidth(), enemybitmap0.getHeight(), 10, true);
            enemies.add(enemy);  // 添加怪物到集合中
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
        //updateDamage();
    }
    private HashMap<Figure, Integer> EnemyDamageInterval = new HashMap<>();
    private void updateDamage(){
        for (Figure enemy : enemies){
            if (!Collision(player,enemy)){
                continue;
            }
            int Interval = EnemyDamageInterval.getOrDefault(enemy,0);
            if (Interval > 0){
                Interval -= 1;
                EnemyDamageInterval.put(enemy,Interval);
                continue;
            }
            player.health -= 1;
            EnemyDamageInterval.put(enemy,6);
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
        double AverageD = (A.getWidth() + A.getHeight()) / 2f + (B.getWidth() + B.getHeight()) / 2f;
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
            if (enemy.getHealth() > 0) {  // 只考虑存活的敌人
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
        drawBackground();
        drawPlayer();
        drawWeapon();
        drawBullet();
        drawEnemy();
        surfaceHolder.unlockCanvasAndPost(mCanvas);
    }
    private void drawEnemy() {
        for (Figure enemy : enemies) {
            Matrix matrix = new Matrix();

            // 先进行翻转操作，再设置平移
            if (!enemy.isFacingRight()) {
                matrix.preScale(-1.0f, 1.0f, enemy.getBitmap().getWidth() / 2, enemy.getBitmap().getHeight() / 2);  // 基于中心点翻转
            }

            // 设置平移，将敌人绘制在正确的位置
            matrix.setTranslate(enemy.getX() - cameraOffsetX, enemy.getY() - cameraOffsetY);

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
        matrix.setTranslate(ViewWith / 2 - player.getBitmap().getWidth() / 2, ViewHeight / 2 - player.getBitmap().getHeight() / 2);

        // 根据角色朝向进行翻转
        if (!player.isFacingRight()) {
            matrix.preScale(-1.0f, 1.0f, player.getBitmap().getWidth() / 2, player.getBitmap().getHeight() / 2);
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

            // 将武器设置在角色上方
            matrix.setTranslate(playerX, playerY + weapon.getHeight() * 2);  // 将武器放在角色头上

            // 如果武器角度在 90° 到 270° 之间（指向左边）
            if (weaponAngleDegrees > 90 && weaponAngleDegrees < 270) {
                // 水平翻转武器图像
                matrix.preScale(-1.0f, 1.0f, weapon.getWidth() / 2, weapon.getHeight() / 2);
            }

            // 旋转武器，使其指向最近敌人
            matrix.postRotate(weaponAngleDegrees, playerX + weapon.getWidth() / 2, playerY + weapon.getHeight() * 2 + weapon.getHeight() / 2);

            // 绘制旋转后的武器
            mCanvas.drawBitmap(weapon.getBitmap(), matrix, null);
        }
    }





    private void fireBullet(float initBulletAngle) {
        bulletFireCounter++;
        if (bulletFireCounter >= bulletFireInterval) {
            // 获取武器的 X 和 Y 坐标
            float weaponX = player.getX() + weapon.getWidth() / 2;
            float weaponY = player.getY() + weapon.getHeight() * 2;

            // 根据武器角度计算子弹的起始位置，让子弹从武器的前端发射
            float bulletStartX = weaponX + (float) Math.cos(initBulletAngle) * weapon.getWidth() + cameraOffsetX;
            float bulletStartY = weaponY + (float) Math.sin(initBulletAngle) * weapon.getHeight() + cameraOffsetY;

            // 创建新的子弹并将其添加到子弹列表
            Bullet newBullet = new Bullet(bulletBitmap, (int) bulletStartX, (int) bulletStartY, bulletBitmap.getWidth(), bulletBitmap.getHeight(), 1,initBulletAngle);

            // 将新子弹添加到子弹列表
            bullets.add(newBullet);

            // 重置子弹发射计数器
            bulletFireCounter = 0;
        }
    }


    private void drawBullet() {
        for (Bullet bullet : bullets) {
            mCanvas.drawBitmap(bullet.getBitmap(), bullet.getX() - cameraOffsetX, bullet.getY() - cameraOffsetY, null);
        }
    }

}
