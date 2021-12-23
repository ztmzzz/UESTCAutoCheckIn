import json
import os
import urllib3

sessionId = os.environ["session"]

data = {
    "healthCondition": "正常",
    "todayMorningTemperature": "36°C~36.5°C",
    "yesterdayEveningTemperature": "36°C~36.5°C",
    "yesterdayMiddayTemperature": "36°C~36.5°C",
    "healthColor": "绿色",
    "location": "四川省成都市郫都区丹桂路",
}
encoded_data = json.dumps(data).encode("utf-8")
http = urllib3.PoolManager()
r = http.request(
    "POST",
    "https://jzsz.uestc.edu.cn/wxvacation/monitorRegisterForReturned",
    body=encoded_data,
    headers={
        'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) '
                      'Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI '
                      'MiniProgramEnv/Windows WindowsWechat',
        'Accept-Encoding': 'identity',
        'Connection': 'close',
        'Content-Type': 'application/json',
        'Cookie': 'SESSION=' + sessionId,

    }
)
if r.status == 200:
    response = r.data
    if len(response) > 0:
        j = json.loads(response)
        print(j)
else:
    print("连接失败")
