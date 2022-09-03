package cn.tedu.shoot;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/** 整个游戏窗口 */
public class World extends JPanel {
    public static final int WIDTH = 400;  //窗口的宽
    public static final int HEIGHT = 700; //窗口的高

    //如下对象为窗口中所显示的对象
    private Sky sky = new Sky();         //天空对象
    private Hero hero = new Hero();      //英雄机对象
    private FlyingObject[] enemies = {}; //敌人(小敌机、大敌机、小蜜蜂)数组
    private Bullet[] bullets = {};       //子弹数组

    /** 生成敌人(小敌机、大敌机、小蜜蜂)对象 */
    public FlyingObject nextOne(){
        Random rand = new Random(); //随机数对象
        int type = rand.nextInt(20); //0到19之间的随机数
        if(type<5){ //0到4时，返回小蜜蜂对象
            return new Bee() ;
        }else if(type<13){ //5到12时，返回小敌机对象
            return new Airplane();
        }else{ //13到19时，返回大敌机对象
            return new BigAirplane();
        }
    }

    private int enterIndex = 0; //敌人入场计数
    /** 敌人入场 */
    public void enterAction(){ //每10毫秒走一次
        enterIndex++; //每10毫秒增1
        if(enterIndex%40==0){ //每400(40*10)毫秒走一次
            FlyingObject obj = nextOne(); //获取敌人对象
            enemies = Arrays.copyOf(enemies,enemies.length+1); //扩容
            enemies[enemies.length-1] = obj; //将obj添加到enemies的最后一个元素上
        }
    }

    private int shootIndex = 0; //子弹入场计数
    /** 子弹入场 */
    public void shootAction(){ //每10毫秒走一次
        shootIndex++; //每10毫秒增1
        if(shootIndex%30==0){ //每300(30*10)毫秒走一次
            Bullet[] bs = hero.shoot(); //获取子弹数组对象
            bullets = Arrays.copyOf(bullets,bullets.length+bs.length); //扩容(bs有几个就扩大几个容量)
            System.arraycopy(bs,0,bullets,bullets.length-bs.length,bs.length); //数组的追加
        }
    }

    /** 飞行物移动 */
    public void stepAction(){ //每10毫秒走一次
        sky.step(); //天空动
        for(int i=0;i<enemies.length;i++){ //遍历所有敌人
            enemies[i].step(); //敌人动
        }
        for(int i=0;i<bullets.length;i++){ //遍历所有子弹
            bullets[i].step(); //子弹动
        }
    }

    /** 删除越界的敌人和子弹 */
    public void outOfBoundsAction(){ //每10毫秒走一次
        for(int i=0;i<enemies.length;i++){ //遍历所有敌人
            if(enemies[i].isOutOfBounds() || enemies[i].isRemove()){ //越界了
                enemies[i] = enemies[enemies.length-1]; //将越界敌人替换为enemies的最后一个元素
                enemies = Arrays.copyOf(enemies,enemies.length-1); //缩容
            }
        }

        for(int i=0;i<bullets.length;i++){ //遍历所有子弹
            if(bullets[i].isOutOfBounds()|| bullets[i].isRemove()){ //越界了
                bullets[i] = bullets[bullets.length-1]; //将越界子弹替换为bullets的最后一个元素
                bullets = Arrays.copyOf(bullets,bullets.length-1); //缩容
            }
        }
    }
    private int score = 0; //玩家的得分
    /** 子弹与敌人的碰撞 */
    public void bulletBangAction(){ //每10毫秒走一次
        for(int i=0;i<bullets.length;i++){ //遍历所有子弹
            Bullet b = bullets[i]; //获取每一个子弹
            for(int j=0;j<enemies.length;j++){ //遍历所有敌人
                FlyingObject f = enemies[j]; //获取每一个敌人
                if(b.isLive() && f.isLive() && f.isHit(b)){ //若都活着的，并且还撞上了
                    b.goDead(); //子弹去死
                    f.goDead(); //敌人去死
                    if(f instanceof EnemyScore){ //若被撞对象能得分
                        EnemyScore es = (EnemyScore)f; //将被撞对象强转为得分接口
                        score += es.getScore(); //玩家得分
                    }
                    if(f instanceof EnemyAward){ //若被撞对象为奖励
                        EnemyAward ea = (EnemyAward)f; //将被撞对象强转为奖励接口
                        int type = ea.getAwardType(); //获取奖励类型
                        switch (type){ //根据奖励类型来获取不同的奖励
                            case EnemyAward.FIRE: //若为火力值
                                hero.addFire();   //则英雄机增火力
                                break;
                            case EnemyAward.LIFE: //若为命
                                hero.addLife();   //则英雄机增命
                                break;
                        }
                    }
                }
            }
        }
    }

    /** 启动程序的执行 */
    public void action(){
        //鼠标侦听器
        MouseAdapter m = new MouseAdapter(){
            /** 重写mouseMoved()鼠标移动事件 */
            public void mouseMoved(MouseEvent e){
                int x = e.getX(); //获取鼠标的x坐标
                int y = e.getY(); //获取鼠标的y坐标
                hero.moveTo(x,y); //英雄机随着鼠标移动
            }
        };
        this.addMouseListener(m);
        this.addMouseMotionListener(m);

        Timer timer = new Timer(); //定时器对象
        int interval = 10; //定时间隔(以毫秒为单位)
        timer.schedule(new TimerTask(){
            public void run(){ //定时干的事(每10毫秒走一次)
                enterAction(); //敌人入场
                shootAction(); //子弹入场
                stepAction();  //飞行物移动
                outOfBoundsAction(); //删除越界的敌人和子弹
                outOfBoundsAction(); //删除越界的敌人和子弹
                bulletBangAction();  //子弹与敌人的碰撞
                repaint(); //重画(重新调用paint()方法)
            }
        },interval,interval); //定时计划表
    }

    /** 重写paint()画  g:画笔 */
    public void paint(Graphics g){ //每10毫秒走一次
        g.drawImage(sky.getImage(),sky.x,sky.y,null); //画天空
        g.drawImage(sky.getImage(),sky.x,sky.getY1(),null); //画天空2
        g.drawImage(hero.getImage(),hero.x,hero.y,null); //画英雄机
        for(int i=0;i<enemies.length;i++){ //遍历所有敌人
            FlyingObject f = enemies[i]; //获取每一个敌人
            g.drawImage(f.getImage(),f.x,f.y,null); //画敌人
        }
        for(int i=0;i<bullets.length;i++){ //遍历所有子弹
            Bullet b = bullets[i]; //获取每一个子弹
            g.drawImage(b.getImage(),b.x,b.y,null); //画子弹
        }
        g.drawString("SCORE: "+score,10,25); //画分
        g.drawString("LIFE: "+hero.getLife(),10,45); //画命
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        World world = new World();
        world.setFocusable(true);
        frame.add(world);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH,HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); //1)设置窗口可见  2)尽快调用paint()方法
        world.action(); //启动程序的执行
    }
}