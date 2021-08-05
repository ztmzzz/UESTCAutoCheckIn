package indi.dakaRobot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.IOException;
import java.util.*;
import java.util.Timer;

class TimerTask extends java.util.TimerTask {
  private Friend admin;

  public TimerTask(Friend admin) {
    this.admin = admin;
  }

  @Override
  public void run() {
    try {
      daka.checkLastDay();
    } catch (IOException e) {
      admin.sendMessage(e.getMessage());
    }
    try {
      daka.daka();
      admin.sendMessage("打卡成功");
    } catch (IOException e) {
      if (e.getMessage().equals("登录失败")) {
        admin.sendMessage("登录微信修改jsessionid");
      } else {
        admin.sendMessage(e.getMessage());
      }
    }
  }
}

public class Robot {
  // 输入管理员和机器人信息
  public static Long botQQ = 12345L;
  public static Long adminQQ = 12345L;
  public static String botPassword = "机器人QQ密码";

  public static void main(String[] args) {
    Bot bot =
        BotFactory.INSTANCE.newBot(
            botQQ,
            botPassword,
            new BotConfiguration() {
              {
                fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
              }
            });
    bot.login();
    admin(bot);
  }

  public static void admin(Bot bot) {
    Friend admin = bot.getFriend(adminQQ);
    Timer timer = new Timer();
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 8);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    Date time = calendar.getTime(); // 每天8点执行任务
    Date now = new Date();
    long diff = time.getTime() - now.getTime();
    if (diff < 0) {
      timer.schedule(new TimerTask(admin), 0);
      calendar.add(Calendar.DATE, 1);
      time = calendar.getTime();
    }
    timer.scheduleAtFixedRate(new TimerTask(admin), time, 1000 * 60 * 60 * 24);
    bot.getEventChannel()
        .subscribeAlways(
            FriendMessageEvent.class,
            (event) -> {
              Friend sender = event.getSender();
              if (sender.getId() == adminQQ) {
                String text = event.getMessage().contentToString();
                if (text.equals("打卡")) {
                  try {
                    daka.daka();
                    sender.sendMessage("打卡成功");
                  } catch (IOException e) {
                    sender.sendMessage(e.getMessage());
                  }
                }
              }
            });
  }
}
