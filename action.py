import os
import time
import requests

data = {
    "healthCondition": "正常",
    "todayMorningTemperature": "36°C~36.5°C",
    "yesterdayEveningTemperature": "36°C~36.5°C",
    "yesterdayMiddayTemperature": "36°C~36.5°C",
    "healthColor": "绿色",
    "location": "四川省成都市郫都区丹桂路",
}
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) '
                  'Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI '
                  'MiniProgramEnv/Windows WindowsWechat',
    'Content-Type': 'application/json',
}
sessionId = os.environ["session"]

res = requests.get(url='https://jzsz.uestc.edu.cn/wxvacation/api/user/getLoginUser?sessionId=' + sessionId,
                   headers=headers)

cookie = res.cookies
cookie.set('Hm_lvt_fe3b7a223fc08c795f0f4b6350703e6f', str(int(time.time())))
cookie.set('Hm_lpvt_fe3b7a223fc08c795f0f4b6350703e6f', str(int(time.time())))

r = requests.post(url="https://jzsz.uestc.edu.cn/wxvacation/monitorRegisterForReturned",
                  json=data,
                  cookies=cookie,
                  headers=headers,
                  )

if r.status_code == 200:
    response = r.text
    if len(response) > 0:
        print(response)
else:
    print("连接失败")
