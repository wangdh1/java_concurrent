package com.wangdh.survey.thread.base;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 比赛规则如下:
 *
 *   1.有四个选手, A1和A2为一个队, B1和B2为另一个队. A1首先发球(启动球), 然后B1, A2, B2将最后发球. 每一轮每个选手发6个球.
 *
 *   2.选手不改变他们的位置.
 *
 *   3.比赛期间, 双方选手必须轮流发球,并且在同一个队伍的两个选手可以竞争发球.
 *
 *   4.当轮到某个选手时, 他/她可以调用一个叫做shot(rate) 的随机函数来模拟比赛,在给定概率rate以内,该函数返回 “in”, 否则返回”out”. 例如 rate=85%, 则球在界内的概率为85%, 出界的概率为15%.
 *
 *   5.如果shot函数返回”in”, 对方选手必须调用shot函数把球打回.
 *
 *   6.如果shot函数返回”out”, 对方选手赢得1分,随后重新发球.
 *
 *   7.当每个选手发完6个球后比赛终止.分数多的一方赢得比赛.分数一样多,比赛为平局.
 *
 *   8.每个选手作为一个线程实现.
 */
public class TableTennisThreadDemo {


    public static void main(String args[]) {

        CountDownLatch countDownLatch = new CountDownLatch(4);
        double rate = 0.75; //设置选手赢的概率
        int sendNum = 6; //每个选手发球次数


//两个队的名字
        String team1_Name = TeamController.getTeamName()[0];
        String team2_Name = TeamController.getTeamName()[1];


//四个选手名字
        String team1_memName1 = TeamController.getTeamMems(team1_Name)[0];
        String team1_memName2 = TeamController.getTeamMems(team1_Name)[1];
        String team2_memName1 = TeamController.getTeamMems(team2_Name)[0];
        String team2_memName2 = TeamController.getTeamMems(team2_Name)[1];


//创建两个队
        Team team1 = new Team(team1_Name);
        Team team2 = new Team(team2_Name);


//设置对手
        TeamController.setTeamRival(team2, team1);
//设置比赛场数
        TeamController.setMatchTimes(sendNum * 4); //参数为选手数*每个选手的发球数


//创建乒乓球对象
        TableTennis tableTennis = new TableTennis(rate);


//创建四个线程类
        Thread thread1 = new Thread(new TeamMember(team1_memName1, sendNum, team1, tableTennis, countDownLatch));
        Thread thread2 = new Thread(new TeamMember(team1_memName2, sendNum, team1, tableTennis, countDownLatch));
        Thread thread3 = new Thread(new TeamMember(team2_memName1, sendNum, team2, tableTennis, countDownLatch));
        Thread thread4 = new Thread(new TeamMember(team2_memName2, sendNum, team2, tableTennis, countDownLatch));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TableTennisThreadDemo.class.getName()).log(Level.SEVERE, null, ex);
        }


        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();


//等待四个选手运行完
        try {
            countDownLatch.await();
            System.out.println("team1得分：" + team1.getScore());
            System.out.println("team2得分：" + team2.getScore());
            if (team1.getScore() > team2.getScore()) {
                System.out.println("恭喜team1队赢!");
            } else if (team1.getScore() < team2.getScore()) {
                System.out.println("恭喜team2队赢！");
            } else {
                System.out.println("平局了！");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(TableTennisThreadDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


class TableTennis {


    int rate; //赢的频率
    volatile int state = 0; //球的状态,0代表发球,1代表传球,默认是发球状态
    String trail = "team1"; //传球的轨迹
    String teamTrail = "team1"; //发球的轨迹
    volatile boolean isEnd = false; //判断比赛有没有结束
    volatile int totalSendNum = 0; //总共发球次数


    public int getTotalSendNum() {
        return totalSendNum;
    }


    public void addTotalSendNum() {
        this.totalSendNum = ++totalSendNum;
    }


    public boolean isIsEnd() {
        return isEnd;
    }


    public void setIsEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public String getTeamTrail() {
        return teamTrail;
    }


    public void setTeamTrail(String teamTrail) {
        this.teamTrail = teamTrail;
    }


    public TableTennis(double rate) {
        this.rate = (int) (rate * 100);
    }


    /**
     * 判断输赢
     *
     * @return
     */
    public final String shot() {
        int randRate = (int) (Math.random() * 100);
        if (((100 - randRate) > (this.rate))) {
            return "out";
        } else {
            return "in";
        }
    }


    public int getRate() {
        return rate;
    }


    public void setRate(int rate) {
        this.rate = rate;
    }


    public int getState() {
        return state;
    }


    public void setState(int state) {
        this.state = state;
    }


    public String getTrail() {
        return trail;
    }


    public void setTrail(String trail) {
        this.trail = trail;
    }


}


class Team {


    String teamName; //球队的名称
    int score = 0; //球队的得分


    public Team(String teamName) {
        this.teamName = teamName;
    }


    public String getTeamName() {
        return teamName;
    }


    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    public int getScore() {
        return score;
    }


    public void addScore() {
        this.score = ++score;
    }


}


class TeamController {


    private static final String[] team = {"team1", "team2"}; //比赛队的名称
    private static final HashMap<String, String[]> map = new HashMap(); //用于存放各队的队员信息
    private static final HashMap<String, Team> rivalTeam = new HashMap(); //用于存放各队的对手
    private static int totalMatchTime; //默认0场比赛


    static {
        map.put(team[0], new String[]{"A1", "A2"}); //初始化team1队球员
        map.put(team[1], new String[]{"B1", "B2"}); //初始化team2队球员
    }


    //设置各队的对方队伍
    public static void setTeamRival(Team rivalTeam1, Team rivalTeam2) {
        rivalTeam.put(team[0], rivalTeam1);
        rivalTeam.put(team[1], rivalTeam2);
    }

    public static void setMatchTimes(int time) {
        totalMatchTime = time;
    }

    public static int getMatchTimes() {
        return totalMatchTime;
    }


    public static Team getRivalTeam(String teamName) {
        return rivalTeam.get(teamName);
    }


    public static String[] getTeamName() {
        return team;
    }


    public static String[] getTeamMems(String teamName) {
        return map.get(teamName);
    }
}
//队员


class TeamMember implements Runnable {


    Team team; //球员所在的球队
    TableTennis tableTennis; //每个队员所传的球
    String memName; //球员名称
    int sendNum; //每个队员传球的次数


    CountDownLatch countDownLatch; //保证每个球员发球次数


    public TeamMember(String memName, int sendNum, Team team, TableTennis tableTennis, CountDownLatch countDownLatch) {
        this.team = team;
        this.tableTennis = tableTennis;
        this.memName = memName;
        this.sendNum = sendNum;
        this.countDownLatch = countDownLatch;
    }


    @Override
    public void run() {
        while (!this.tableTennis.isIsEnd()) { //当在发球此数内
            sendBall();
        }
        countDownLatch.countDown();
    }


    /**
     * 发球的逻辑注意这里的synchronized,并发控制
     */
    private void sendBall() {
        synchronized (tableTennis) {

// System.out.println("当前发球线程:" + Thread.currentThread().getName());
            while (!(this.tableTennis.getTrail().equals(this.team.getTeamName()))) { //判断是否是该队传球
// System.out.println("发球睡眠线程:" + Thread.currentThread().getName());
                if (this.tableTennis.isIsEnd()) {
                    return;
                }

                try {
                    tableTennis.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(TableTennis.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (this.tableTennis.isIsEnd()) { //判断比赛有没有结束，结束直接返回
                return;
            }
            if (this.tableTennis.getState() == 0) { //判断是不是发球状态
                this.sendNum--; //将发球次数减一
            }

//发球的时候判断在界内还是界外
            if (this.tableTennis.shot().equals("in")) { //发球表示球在界内
                this.tableTennis.setTrail(TeamController.getRivalTeam(this.team.getTeamName()).getTeamName()); //对手接球
                this.tableTennis.setState(1); //如果发球在界内，将发球状态改为传球状态
                System.out.print(this.memName + "-in-");
            } else { //如果在界外，此时还是发球状态
                TeamController.getRivalTeam(this.team.getTeamName()).addScore(); //对方队加上一分
                System.out.print(this.memName + "-out\n");
                this.tableTennis.setTeamTrail(TeamController.getRivalTeam(this.tableTennis.getTeamTrail()).getTeamName());
                this.tableTennis.setTrail(this.tableTennis.getTeamTrail());
                this.tableTennis.setState(0);
                this.tableTennis.addTotalSendNum(); //将发球次数加一
                if (this.tableTennis.getTotalSendNum() == TeamController.getMatchTimes()) {
                    this.tableTennis.setIsEnd(true);
                }
            }


//执行完唤醒对手线程
            tableTennis.notifyAll();
        }
    }

}