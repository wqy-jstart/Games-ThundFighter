package cn.tedu.shoot;

import java.awt.image.BufferedImage;

public class Bullet extends FlyingObject {//子弹
    int speed; //移动速度

    public Bullet(int x, int y) { //子弹可以有多个，子弹的初始坐标要依赖于当前英雄机的坐标位置
        super(8, 20, x, y);
        speed = 10;
    }

    /*重写step()移动 */
    public void step() {
        y -= speed;
    }
    public BufferedImage getImage(){
        return Images.bullet;
    }
}


