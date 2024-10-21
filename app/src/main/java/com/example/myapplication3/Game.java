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

public class Game extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Bitmap background_game, player0, player1, player2, player3, player4, player5, weapon_game,bulletBitmap;
    private Canvas mCanvas;
    private int ViewWith, ViewHeight;
    private SurfaceHolder surfaceHolder;
    private boolean isDraw = true;
    private Entity player;
    private Entity background;
    private final boolean dead = false;
    private Entity weapon;
    private boolean isFacingRight = true;
    private GameController gameController;
    private float cameraOffsetX = 0;
    private float cameraOffsetY = 0;
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
        background_game = BitmapFactory.decodeResource(context.getResources(),R.drawable.back);
        player0 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player0);
        player1 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player1);
        player2 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player2);
        player3 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player3);
        player4 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player4);
        player5 = BitmapFactory.decodeResource(context.getResources(),R.drawable.player5);
        weapon_game = BitmapFactory.decodeResource(context.getResources(),R.drawable.weapon);
        bulletBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.bullet1);
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
        //background_game= getRatioBitmap(background_game,(float) ViewWith/background_game.getWidth(),(float) ViewHeight/background_game.getHeight());
        player0 = getRatioBitmap(player0,2.5f,2.5f);
        player1 = getRatioBitmap(player1,2.5f,2.5f);
        player2 = getRatioBitmap(player2,2.5f,2.5f);
        player3 = getRatioBitmap(player3,2.5f,2.5f);
        player4 = getRatioBitmap(player4,2.5f,2.5f);
        player5 = getRatioBitmap(player5,2.5f,2.5f);
        weapon_game = getRatioBitmap(weapon_game,2.5f,2.5f);
        background = new Entity(background_game,0,0,background_game.getWidth(),background_game.getHeight());
        player = new Entity(player0, ViewWith/2 - player0.getWidth()/2,ViewHeight/2-player0.getHeight()/2, player0.getWidth(), player0.getHeight());
        weapon = new Entity(weapon_game,ViewWith/2-weapon_game.getWidth()/2,ViewHeight/2-weapon_game.getWidth()/2,weapon_game.getWidth(),weapon_game.getHeight());
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

    @Override
    public void run() {
        while (isDraw){
            drawMain();
            update();
        }
    }

    private void update(){
        playerMove();
    }

    private void updateCamera() {
        // 计算角色相对于屏幕中心的偏移量
        cameraOffsetX = player.getX() - (ViewWith / 2 - player.getWith() / 2);
        cameraOffsetY = player.getY() - (ViewHeight / 2 - player.getHeight() / 2);
    }

    private void drawMain() {
        mCanvas = surfaceHolder.lockCanvas();
        drawBackground();
        drawPlayer();
        drawWeapon();

        surfaceHolder.unlockCanvasAndPost(mCanvas);
    }
    int playerBitmapData = 0;
    int changeBitmapTime = 0;
    private void playerMove() {
        float angle = gameController.getAngle();
        float strength = gameController.getStrength();

        if (strength > 0) {
            float speed = 5.0f * strength;  // 根据摇杆力度调整速度
            float dx = (float) Math.cos(angle) * speed;
            float dy = (float) Math.sin(angle) * speed;

            updateCamera();
            // 更新角色的X和Y坐标
            player.setX((int) (player.getX() + dx));
            player.setY((int) (player.getY() + dy));
            // 根据摇杆的角度判断角色朝向
            if (angle < -Math.PI / 2 || angle > Math.PI / 2) {
                isFacingRight = false;  // 角色面朝左
            } else {
                isFacingRight = true;  // 角色面朝右
            }

            // 播放跑步动画
            changeBitmapTime++;
            if (changeBitmapTime % 6 == 0) {  // 每5帧切换一次图片
                playerBitmapData = (playerBitmapData % 4) + 1;
            }
        } else {
            // 如果没有移动，角色保持站立不动
            playerBitmapData = 0;
        }

        switch (playerBitmapData){
            case 0:
                player.setBitmap(player0);
                break;
            case 1:
                player.setBitmap(player1);
                break;
            case 2:
                player.setBitmap(player2);
                break;
            case 3:
                player.setBitmap(player3);
                break;
            case 4:
                player.setBitmap(player4);
                break;
        }
        if (dead){
            player.setBitmap(player5);
        }
    }


    int playerSpinData;
    private void drawPlayer() {
        Matrix matrix = new Matrix();
        matrix.setTranslate(player.getX() - cameraOffsetX, player.getY() - cameraOffsetY);
        if (!isFacingRight) {
            // 水平翻转位图
            matrix.preScale(-1.0f, 1.0f, player.getBitmap().getWidth() / 2, player.getBitmap().getHeight() / 2);
        }
        matrix.postRotate(playerSpinData);
        mCanvas.drawBitmap(player.getBitmap(),matrix,null);
    }

    private void drawBackground() {
        mCanvas.drawBitmap(background.getBitmap(), background.getX() - cameraOffsetX, background.getY() - cameraOffsetY, null);
    }

    private void drawWeapon(){
        mCanvas.drawBitmap(weapon.getBitmap(), player.getX(), player.getY(), null);
    }
}
