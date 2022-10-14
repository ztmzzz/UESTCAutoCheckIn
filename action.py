import time
import requests
import argparse

data_normal = {
    "healthCondition": "正常",
    "todayMorningTemperature": "36°C~36.5°C",
    "yesterdayEveningTemperature": "36°C~36.5°C",
    "yesterdayMiddayTemperature": "36°C~36.5°C",
    "healthColor": "绿色",
    "location": "四川省成都市郫都区丹桂路",
}
url_normal = r'https://jzsz.uestc.edu.cn/wxvacation/monitorRegisterForReturned'
data_vacation = {
    "isLeaveChengdu": 1,
    "currentAddress": "浙江省宁波市北仑区abc路",
    "isContactWuhan": 0,
    "isSymptom": 0,
    "temperature": "36°C~36.5°C",
    "province": "浙江省",
    "healthInfo": "正常",
    "isFever": 0,
    "remark": "",
    "healthColor": "绿色",
    "city": "宁波市",
    "county": "北仑区",
    "isInSchool": 0
}
url_vacation = r'https://jzsz.uestc.edu.cn/wxvacation/api/epidemic/monitorRegister'
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) '
                  'Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI '
                  'MiniProgramEnv/Windows WindowsWechat',
    'Content-Type': 'application/json',
}
parser = argparse.ArgumentParser()
parser.add_argument('--session', type=str, default=None)
parser.add_argument('--type', type=str, default=None)
args = parser.parse_args()
sessionId = args.session
type = args.type

res = requests.get(url='https://jzsz.uestc.edu.cn/wxvacation/api/user/getLoginUser?sessionId=' + sessionId,
                   headers=headers)

cookie = res.cookies
cookie.set('Hm_lvt_fe3b7a223fc08c795f0f4b6350703e6f', str(int(time.time())))
cookie.set('Hm_lpvt_fe3b7a223fc08c795f0f4b6350703e6f', str(int(time.time())))
if type == 'normal':
    r = requests.post(url=url_normal,
                      json=data_normal,
                      cookies=cookie,
                      headers=headers,
                      )
elif type == 'vacation':
    r = requests.post(url=url_vacation,
                      json=data_vacation,
                      cookies=cookie,
                      headers=headers,
                      )
else:
    r = requests.post(url=url_normal,
                      json=data_normal,
                      cookies=cookie,
                      headers=headers,
                      )
if r.status_code == 200:
    response = r.text
    if len(response) > 0:
        print(response)
else:
    print("连接失败")
