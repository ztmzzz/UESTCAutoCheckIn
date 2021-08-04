import json
from mitmproxy.options import Options
from mitmproxy.tools.dump import DumpMaster


class GetSession:
    def __init__(self):
        pass

    def response(self, flow):
        if "wxvacation/api/epidemic/login/checkBind" in flow.request.path:  # 检测是否为获取session的api
            dataStr = flow.response.get_text()
            # print(flow.response.get_text())
            dataJson = json.loads(dataStr)
            status = dataJson["status"]
            if status:
                data = dataJson["data"]
                sessionId = data["sessionId"]
                # print(sessionId)
                with open("daka.txt", "r+") as f:
                    text = f.readlines()
                    text[0] = sessionId + "\n"
                    f.seek(0)
                    f.writelines(text)


addons = [
    GetSession()
]

# opts = Options(listen_host='0.0.0.0', listen_port=8080, http2=True)
opts = Options(listen_host='0.0.0.0', listen_port=8080)

m = DumpMaster(opts)

m.addons.add(GetSession())

try:
    m.run()
except KeyboardInterrupt:
    m.shutdown()
