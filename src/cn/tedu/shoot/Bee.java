package cn.tedu.shoot;

import java.util.Random;
import java.awt.image.BufferedImage;

public class Bee extends FlyingObject implements EnemyAward {//小蜜蜂
    int xSpeed; //x坐标移动速度
    int ySpeed; //y坐标移动速度
    int awardType; //奖励类型
    public Bee(){//-----------构造方法
        super(60,51);
        xSpeed=1;
        ySpeed=2;
        Random rand = new Random();
        awardType=rand.nextInt(2);//0到1之间随机生成
    }
    /*重写step()移动 */
    public void step(){
        x+=xSpeed;//向左或向右
        y+=ySpeed;//向下
        if(x<=0 || x>=400-width){//若x<=0或x>=(窗口宽-蜜蜂宽)，表示到两头了
            xSpeed*=-1;//则切换方法(正变负，负变正)
        }
    }
    int index = 1; //下标
    /** 重写getImage()获取图片 */
    public BufferedImage getImage(){ //每10毫秒走一次
        if(isLive()){
            return Images.bees[0];
        }else if(isDead()){
            BufferedImage img = Images.bees[index++];
            if(index==Images.bees.length){
                state = REMOVE;
            }
            return img;
        }
        return null;
    }

    /** 重写getAwardType()获取奖励类型 */
    public int getAwardType(){
        return awardType; //返回奖励类型
    }
}
