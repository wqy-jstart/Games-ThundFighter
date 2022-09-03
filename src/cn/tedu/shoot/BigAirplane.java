package cn.tedu.shoot;
import java.awt.image.BufferedImage;

public class BigAirplane extends  FlyingObject implements EnemyScore {//大敌机
    int speed;  //移动速度
    public BigAirplane(){//--------构造方法
       super(66,89);
        speed=2;
    }
    /*重写step()移动 */
    public void step(){
        y+=speed;//向下
    }
    int index = 1; //下标
    /** 重写getImage()获取图片 */
    public BufferedImage getImage(){ //每10毫秒走一次
        if(isLive()){
            return Images.bairs[0];
        }else if(isDead()){
            BufferedImage img = Images.bairs[index++];
            if(index==Images.bairs.length){
                state = REMOVE;
            }
            return img;
        }
        return null;
    }

    /** 重写getScore()得分 */
    public int getScore(){
        return 3; //打掉大敌机，玩家得3分
    }
}
