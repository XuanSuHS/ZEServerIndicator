package top.xuansu.mirai.zeServerIndicator

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

fun webfortopze(): String {
    //TODO:token从文件传入
    val token = "fec9d45f7416256443b5e051c200fc1d"
    val baseurl = "https://api-clan.rushbgogogo.com/api/v1/systemApp/gameServerRoomsList?mode=ze"
    //构建http请求
    val okHttpclient = OkHttpClient.Builder().build()
    val request = Request.Builder()
        .url(baseurl)
        .header("clan_auth_token", token)
        .get()
        .build()
    val response = okHttpclient.newCall(request).execute()
    val responseData = response.body!!.string()
    response.close()
    //将请求转换为JsonObject并提取其中message数组部分
    val responseDataJSON = JsonParser.parseString(responseData).asJsonObject.getAsJsonArray("message")
    var serverString = "   [5e ZE 服务器数据]"
    //遍历数组中每一项中所需的数据
    for (i in 0 until responseDataJSON.size()){
        val server = responseDataJSON.get(i)
        val name = server.asJsonObject.get("Name").toString()
        val playerCount = server.asJsonObject.get("PlayerCount").toString()
        val gameMap = server.asJsonObject.get("GameMap").toString()
        serverString = serverString.plus("\n\n$name  ").plus(playerCount+"人\n").plus(gameMap)
    }
    return serverString.replace("\"", "")
}

object UB {
    //因为数组从0开始，偷个懒不用转换了
    private var serverNameArr = Array(12) { "" }


    private var tscoreArr = Array(12) { 0 }
    private var ctscoreArr = Array(12) { 0 }
    private var serverMapArr = Array(12) { "" }
    private var serverNextMapArr = Array(12) { "" }
    private var serverPlayerArr = Array(12) { 0 }

    var wsfail = false

    //Event "server/init"
    //即客户端初始化
    fun serverInit(serverJSON:JsonObject) {

        //提取JSON中服务器数据
        val serverData = serverJSON.get("data").asJsonObject

        //剔除非 CSGO 服务器信息
        val serverappid = serverData.get("appid").toString().toInt()
        if (serverappid != 730) {return}

        // 剔除非 ZE 模式信息
        val serverMode = serverData.get("mode").toString().toInt()
        if (serverMode != 1) {return}

        //查询是哪个服务器
        val serverNumber = serverData.get("id").toString().toInt()

        //将服务器名写入数组
        serverNameArr[serverNumber] = serverData.get("name").toString().replace("\"", "").replace(" Q群 749180050", "").replace("UB社区 ","")

        //将比分写入数组
        tscoreArr[serverNumber] = serverData.get("t_score").toString().toInt()
        ctscoreArr[serverNumber] = serverData.get("ct_score").toString().toInt()

        //将地图写入数组
        serverMapArr[serverNumber] = serverData.get("map").asJsonObject.get("name").toString().replace("\"", "")
        serverNextMapArr[serverNumber] = serverData.get("nextmap").asJsonObject.get("name").toString().replace("\"", "")

        //根据player数组里的个数获取人数,将人数写入数组
        serverPlayerArr[serverNumber] = serverData.get("clients").asJsonArray.size()
    }

    //Event "server/client/connected"
    //即玩家连接到服务器
    fun clientconnect(serverJSON: JsonObject) {
        serverPlayerArr[serverJSON.get("server").toString().toInt()] += 1
    }

    //Event "server/client/disconnect"
    //即玩家断开连接
    fun clientdisconnect(serverJSON: JsonObject) {
        serverPlayerArr[serverJSON.get("server").toString().toInt()] -= 1
    }

    //Event "server/nextmap/changed"
    //即 RTV 选定下张图
    fun nextMapChange(serverJSON: JsonObject) {
        val serverNumber = serverJSON.get("server").toString().toInt()
        serverNextMapArr[serverNumber] = serverJSON.get("data").asJsonObject.get("name").toString().replace("\"", "")

        //寻找OBJ!
        if(!FindOBJ.FindON) {return}
        if(FindOBJ.FindON && serverNextMapArr[serverNumber] != serverMapArr[serverNumber] && serverNextMapArr[serverNumber].contains("ze_obj")) {
            FindOBJ.sendOBJtoGroup("UB社区" + serverNameArr[serverNumber],
                "下张地图：" + serverNextMapArr[serverNumber],
                serverPlayerArr[serverNumber])
        }
    }

    //Event "server/map/start"
    //下张图开始
    fun mapStart(serverJSON: JsonObject) {
        serverMapArr[serverJSON.get("server").toString().toInt()] = serverJSON.get("data").asJsonObject.get("name").toString().replace("\"", "")
    }

    fun webforub() {
        val okHttpClient = OkHttpClient.Builder()
            .build()
        val baseurl = "ws://app.moeub.com/ws?files=3"
        val request = Request.Builder()
            .get()
            .url(baseurl)
            .header("User-Agent","Moeub Client")
            .build()
        //构建websocket客户端
        val websocket = okHttpClient.newWebSocket(request, object : WebSocketListener(){

            //在websocket连接收到返回信息时执行
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)

                //转数据为json
                val serverJSON = JsonParser.parseString(text).asJsonObject
                val event = serverJSON.get("event").toString().replace("\"", "")
                //根据返回Event确定动作
                if (event == "server/init") {
                    serverInit(serverJSON)}

                if (event == "server/client/connected") {
                    clientconnect(serverJSON)}

                if (event == "server/client/disconnect") {
                    clientdisconnect(serverJSON)}

                if (event == "server/nextmap/changed") {
                    nextMapChange(serverJSON)}

                if (event == "server/map/start") {
                    mapStart(serverJSON)
                }
                
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                wsfail = true
            }
        })
        websocket.send("6")
    }

    fun dataOutput():String {
        var response = "   [UB 社区 ZE 服务器数据]"
        for (i in 1 until 12) {
            //判定有没有定下张地图
            if (serverNameArr[i] == "") {continue}
            val serverNextMap = if (serverNextMapArr[i] == serverMapArr[i] || serverNextMapArr[i].length <= 3) {
                ""
            } else {
                "\n下张地图：" + serverNextMapArr[i]
            }

            response += "\n------------------------------\n".plus("【" + serverNameArr[i] + "】").plus(" ").plus(serverPlayerArr[i]).plus("/64\n")
                .plus("地图："+ serverMapArr[i] + "\n")
                .plus("比分：" + ctscoreArr[i] + "/" + tscoreArr[i])
                .plus(serverNextMap)
        }
        return response
    }

    //寻找OBJ
    fun firstTimeFindOBJ() {
        if (!FindOBJ.FindON) {return}
        for (i in 1 until 12) {
            val serverNumber = i
            if (serverMapArr[serverNumber].contains("ze_obj")) {
                FindOBJ.sendOBJtoGroup("UB社区 " + serverNameArr[serverNumber],
                    "地图：" + serverMapArr[serverNumber],
                    serverPlayerArr[serverNumber])
            } else if (serverNextMapArr[serverNumber].contains("ze_obj")) {
                FindOBJ.sendOBJtoGroup("UB社区 " + serverNameArr[serverNumber],
                    "下张地图：" + serverNextMapArr[serverNumber],
                    serverPlayerArr[serverNumber])
            }
        }
    }
}


object Zed {

    private var serverNameArr = Array(7) {""}
    private var serverAddressArr = Array(7) {""}
    private var serverMapArr = Array(7) {""}
    private var serverNextMapArr = Array(7) {""}
    private var serverNominateMapArr = Array(7) {""}
    private var serverPlayerArr = Array(7) {0}
    fun webforZED() {
        //构建ServerList 请求
        val okHttpclient = OkHttpClient.Builder().build()
        val serverListBaseURL = "http://zedbox.cn:5002/api/version/GetServerList"
        val mediaType = "text/json;charset=utf-8".toMediaType()
        val body = "".toRequestBody(mediaType)
        val serverListRequest = Request.Builder()
            .url(serverListBaseURL)
            .post(body)
            .build()
        val serverListResponse = okHttpclient.newCall(serverListRequest).execute()
        //变换ServerList 返回数据
        val serverListResponseData = serverListResponse.body!!.string()

        val serverListResponseDataJSON = JsonParser.parseString(serverListResponseData).asJsonArray
        //
        for (i in 0 until serverListResponseDataJSON.size()) {
            val server = serverListResponseDataJSON.get(i).asJsonObject
            //跳过ServerList中非 ZE/ZM 服务器
            if (server.get("serverGroupSortNumber").toString() != "1") {continue}
            //确定并跳过 ZM 服务器

            if (server.get("serverName").toString().contains("ZM")) {continue}
            //寻找ServerList中需要的JSON项
            val serverNumber = server.get("serverID").toString().replace("\"", "").toInt().minus(100)
            serverNameArr[serverNumber] = server.get("serverName").toString().replace("\"","").replace(" 僵尸逃跑","")
            serverAddressArr[serverNumber] = server.get("ip").toString().plus(":").plus(server.get("port").toString()).replace("\"", "")

            //单独处理地图
            //如果没有则不显示这两个字段
            val nextMap = server.get("nextMap").toString().replace("\"", "")
            serverMapArr[serverNumber] = if (nextMap.contains("暂无")) {
                ""
            } else {
                "下张地图：$nextMap\n"
            }
            val nominateMap = server.get("nominateMap").toString().replace("\"", "")
            serverNominateMapArr[serverNumber] = if (nominateMap.contains("暂无")) {
                ""
            } else {
                "预定地图：$nominateMap\n"
            }

            //构建 每个服务器的具体数据 请求
            val serverInfoBaseURL = "http://zombieden.cn/getserverinfo.php?address="
            val serverURL = serverInfoBaseURL.plus(serverAddressArr[serverNumber])
            val serverInfoRequest = Request.Builder()
                .url(serverURL)
                .get()
                .build()
            val serverinforesponse = okHttpclient.newCall(serverInfoRequest).execute()
            val serverData = serverinforesponse.body!!.string()
            if (!serverData.contains("HostName")) {return}
            val serverDataJSON = JsonParser.parseString(serverData).asJsonObject
            //寻找服务器详细数据中需要的项
            serverPlayerArr[serverNumber] = serverDataJSON.get("Players").toString().toInt()
            serverMapArr[serverNumber] = serverDataJSON.get("Map").toString().replace("\"", "")
        }
    }

    fun dataOutput():String {
        var response = "   [僵尸乐园 ZE 服务器数据]\n"
        for ( i in 1 until 7) {
            response += "\n".plus(serverNameArr[i]).plus("  ").plus(serverPlayerArr[i].toString() + "/64\n")
                .plus("地图："+ serverMapArr[i] + "\n")
                .plus("地址：" + serverAddressArr[i] + "\n")
                .plus(serverNextMapArr[i])
        }
        return response
    }

    var hasOBJServerArr = Array(7) {false}
    fun findOBJ() {
        if (!FindOBJ.FindON) {return}
        for (i in 1 until 7) {
            if(!hasOBJServerArr[i] && (
                        serverMapArr[i].contains("ze_Obj")
                        || serverNextMapArr[i].contains("ze_Obj")
                        || serverNominateMapArr[i].contains("ze_Obj")
                        || serverMapArr[i].contains("ze_obj")
                        || serverNextMapArr[i].contains("ze_obj")
                        || serverNominateMapArr[i].contains("ze_obj")
                    )
                ) {
                hasOBJServerArr[i] = true
                FindOBJ.sendZEDOBJtoGroup(serverNameArr[i],
                    "地图：" + serverMapArr[i]+ "\n",
                    "下张地图" + serverNextMapArr[i]+ "\n",
                    "预定地图" + serverNominateMapArr[i] + "\n",
                    serverPlayerArr[i])
            } else if (!(
                        serverMapArr[i].contains("ze_Obj")
                        || serverNextMapArr[i].contains("ze_Obj")
                        || serverNominateMapArr[i].contains("ze_Obj")
                        || serverMapArr[i].contains("ze_obj")
                        || serverNextMapArr[i].contains("ze_obj")
                        || serverNominateMapArr[i].contains("ze_obj")
                    )
                ){
                hasOBJServerArr[i] = false
            }
        }
    }
}

fun webforfys():String {
    val token = "swallowtail"
    val baseurl = "https://fyscs.com/silverwing/system/dashboard"
    //构建http请求
    val okHttpclient = OkHttpClient.Builder().build()
    val request = Request.Builder()
        .url(baseurl)
        .header("X-Client", token)
        .get()
        .build()
    val response = okHttpclient.newCall(request).execute()
    val responseData = response.body!!.string()
    var serverString = "   [风云社 ZE 服务器数据]"
    response.close()
    val serverDataArray = JsonParser.parseString(responseData).asJsonObject.get("servers").asJsonArray
    for (i in 0 until  serverDataArray.size()) {
        val server = serverDataArray.get(i).asJsonObject
        //排除非ZE服务器
        if(server.get("modeId").toString() != "1001") {continue}
        val serverName = server.get("name").toString().replace("\"", "")
        val serverMap = server.get("map").toString().replace("\"", "")
        val currentPlayers = server.get("currentPlayers").toString()
        val maxPlayers = server.get("maxPlayers").toString()
        val host = server.get("host").toString().replace("\"", "")
        val port = server.get("port").toString()
        serverString = serverString.plus("\n\n$serverName").plus(" 人数：$currentPlayers/$maxPlayers\n").plus("地图：$serverMap\n").plus("地址：$host:$port")
    }
    return serverString
}