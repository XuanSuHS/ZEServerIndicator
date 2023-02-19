package top.xuansu.mirai.zeServerIndicator

import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.WebSocket
import okhttp3.WebSocketListener

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

object ub {
    var serverNameArr = emptyArray<String>()
    fun webforub():String {
        val okHttpClient = OkHttpClient.Builder().build()
        val baseurl = "ws://app.moeub.com/ws?files=3"
        val request = Request.Builder()
            .get()
            .url(baseurl)
            .header("User-Agent","Moeub Client")
            .build()
        var response = "   [UB 社区 ZE 服务器数据]"
        //构建websocket客户端
        val websocket = okHttpClient.newWebSocket(request, object : WebSocketListener(){
            //在websocket连接收到返回信息时执行
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                //转数据为json
                val serverJSON = JsonParser.parseString(text).asJsonObject
                val serverInit = serverJSON.get("event").toString().replace("\"", "")
                //for的原因是不合条件时跳出，if太恶心了
                //只执行一次
                for (i in 1 until 2) {
                    //剔除非服务器信息
                    if (serverInit != "server/init") {continue}

                    //提取JSON中服务器数据
                    val serverData = serverJSON.get("data").asJsonObject

                    //剔除非 CSGO 服务器信息
                    val serverappid = serverData.get("appid").toString().toInt()
                    if (serverappid != 730) {continue}

                    // 剔除非 ZE 模式信息
                    val serverMode = serverData.get("mode").toString().toInt()
                    if (serverMode != 1) {continue}

                    val serverName = serverData.get("name").toString().replace("\"", "")
                    val tscore = serverData.get("t_score").toString().toInt()
                    val ctscore = serverData.get("ct_score").toString().toInt()

                    val map = serverData.get("map").asJsonObject.get("name").toString().replace("\"", "")
                    val serverNametoresp = serverName.replace(" Q群 749180050", "").replace("UB社区 ","")

                    val nextmap = serverData.get("nextmap").asJsonObject.get("name").toString().replace("\"", "")
                    var nextmaptoresp = "\n下张地图：$nextmap"
                    //如果当前地图名字等于下张地图名字（即没RTV）则不显示下张图
                    if(map == nextmap || nextmap.length <= 3) {nextmaptoresp = ""}

                    //根据player数组里的个数获取人数
                    val playerArray = serverData.get("clients").asJsonArray
                    val players = playerArray.size()

                    response = response.plus("\n------------------------------\n【$serverNametoresp】 $players/64\n地图：$map\n比分：$tscore/$ctscore$nextmaptoresp")
                }
            }
        })
        Thread.sleep(7000)
        websocket.cancel()
        //if (response.length <= 20) {return "UB爆炸辣"}
        return response
    }
}

fun webforzed():String {
    //构建ServerList 请求
    val okHttpclient = OkHttpClient.Builder().build()
    val serverlistbaseurl = "http://zedbox.cn:5002/api/version/GetServerList"
    val mediaType = "text/json;charset=utf-8".toMediaType()
    val body = "".toRequestBody(mediaType)
    val serverlistrequest = Request.Builder()
        .url(serverlistbaseurl)
        .post(body)
        .build()
    val serverlistresponse = okHttpclient.newCall(serverlistrequest).execute()
    //变换ServerList 返回数据
    val serverlistresponseData = serverlistresponse.body!!.string()
    val serverlistresponseDataJSON = JsonParser.parseString(serverlistresponseData).asJsonArray
    var serverString = "   [僵尸乐园 ZE 服务器数据]\n"

    //构建 每个服务器的具体数据 请求
    val serverinfobaseurl = "http://zombieden.cn/getserverinfo.php?address="


    for (i in 0 until serverlistresponseDataJSON.size()) {
        val server = serverlistresponseDataJSON.get(i).asJsonObject
        //跳过ServerList中非 ZE/ZM 服务器
        if (server.get("serverGroupSortNumber").toString() != "1") {continue}
        //确定并跳过 ZM 服务器
        val serverName = server.get("serverName").toString().replace("僵尸逃跑 ","")
        if (serverName.contains("ZM")) {continue}
        //寻找ServerList中需要的JSON项
        val ip = server.get("ip").toString()
        val port = server.get("port").toString()
        //单独处理地图相关字段
        var nextMap = server.get("nextMap").toString().replace("\"", "")
        var nominateMap = server.get("nominateMap").toString().replace("\"", "")
        //如果没有则不显示这两个字段
        nextMap = if (nextMap.contains("暂无")) {
            ""
        } else {
            "下张地图：$nextMap\n"
        }
        nominateMap = if (nominateMap.contains("暂无")) {
            ""
        } else {
            "预定地图：$nominateMap\n"
        }


        //根据服务器单独请求详细数据
        val serveraddress = ip.replace("\"", "").plus(":$port")
        val serverurl = serverinfobaseurl.plus(serveraddress)
        val serverinforequest = Request.Builder()
            .url(serverurl)
            .get()
            .build()
        //sleep() //防止服务器端出BUG
        val serverinforesponse = okHttpclient.newCall(serverinforequest).execute()
        val serverData = serverinforesponse.body!!.string()
        val serverDataJSON = JsonParser.parseString(serverData).asJsonObject
        //寻找服务器详细数据中需要的项
        val players = serverDataJSON.get("Players").toString()
        val map = serverDataJSON.get("Map").toString()

        serverString = serverString.plus("\n$serverName  $players/64\n").plus("地图：$map\n").plus("地址：$serveraddress\n").plus(nextMap).plus(nominateMap)
    }
    return serverString.replace("\"", "")
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