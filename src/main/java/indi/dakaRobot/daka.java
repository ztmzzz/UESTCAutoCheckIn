package indi.dakaRobot;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class daka {
  public static String sessionId;
  public static OkHttpClient client = new OkHttpClient();
  public static ObjectMapper mapper = new ObjectMapper();
  public static Date lastDay;
  public static Date date;
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  // 修改地址既可，默认填报体温为36°C~36.5°C，可在下面修改
  public static String location = "你的地址";
  public static final String file = "daka.txt";
  public static final String api = "monitorRegisterForReturned";//api为monitorRegister或者monitorRegisterForReturned，详细参数在下面修改

  static {
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
      sessionId = bufferedReader.readLine();
      SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
      lastDay = ft.parse(bufferedReader.readLine());
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
  }

  public static void updateInfo() {
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
      sessionId = bufferedReader.readLine();
      SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
      lastDay = ft.parse(bufferedReader.readLine());
      date = new Date();
      String today = ft.format(date);
      date = ft.parse(today);
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }
  }

  public static void checkLastDay() throws IOException {
    updateInfo();
    if (date.getTime() - lastDay.getTime() > 24 * 60 * 60 * 1000) {
      throw new IOException("昨天没有打卡");
    }
  }

  public static void daka() throws IOException {
    updateInfo();
    if (date.equals(lastDay)) {
      throw new IOException("重复打卡");
    }
    Request request=generateRequest(api);
    Response response = client.newCall(request).execute();
    String result;
    if (response.isSuccessful()) {
      result = response.body().string();
    } else {
      throw new IOException("打卡失败,网络错误");
    }
    try {
      JsonNode node = mapper.readTree(result);
      String code = node.get("code").asText();
      switch (code) {
        case "0": // 打卡成功
          try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
            bufferedWriter.write(sessionId + "\n" + ft.format(date));
            bufferedWriter.flush();
          } catch (IOException e) {
            e.printStackTrace();
          }
          break;
        case "50005": // 打卡信息错误
          throw new IOException("打卡信息错误");
        case "40001": // sessionid过期
          throw new IOException("需要更新sessionId");
        case "50000": // 重复打卡，垃圾学校数据库错误
          throw new IOException(node.get("detail").asText());
        default: // 未知错误
          throw new IOException("未知错误");
      }
    } catch (NullPointerException e) {
      throw new IOException("打卡失败,解析json错误");
    }
  }

  public static Request generateRequest(String choice) {
    ObjectNode nodeTemp = mapper.createObjectNode();
    if (choice.equals("monitorRegister")) {
      nodeTemp.put("currentAddress", location);
      nodeTemp.put("remark", "");
      nodeTemp.put("healthInfo", "正常");
      nodeTemp.put("isContactWuhan", 0);
      nodeTemp.put("isFever", 0);
      nodeTemp.put("isInSchool", 0);
      nodeTemp.put("isLeaveChengdu", 1);
      nodeTemp.put("isSymptom", 0);
      nodeTemp.put("temperature", "36°C~36.5°C");
    } else {
      nodeTemp.put("healthCondition", "正常");
      nodeTemp.put("todayMorningTemperature", "36°C~36.5°C");
      nodeTemp.put("yesterdayEveningTemperature", "36°C~36.5°C");
      nodeTemp.put("yesterdayMiddayTemperature", "36°C~36.5°C");
      nodeTemp.put("healthColor", "绿色");
      nodeTemp.put("location", location);
    }
    String temp = nodeTemp.toString();
    RequestBody body = RequestBody.create(temp, JSON);
    return new Request.Builder()
            .url("https://jzsz.uestc.edu.cn/wxvacation/"+api)
            .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat")
            .addHeader("Accept-Encoding", "identity")
            .addHeader("Connection", "close")
            .addHeader("Content-Type", "application/json")
            .addHeader("Cookie", "JSESSIONID=" + sessionId)
            .post(body)
            .build();
  }

  public static void main(String[] args) {
    updateInfo();
    try {
      System.out.println(sessionId);
      daka();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
