package top.xuansu.mirai.zeServerIndicator

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import top.xuansu.mirai.zeServerIndicator.Indicator.dataFolder
import top.xuansu.mirai.zeServerIndicator.Indicator.save
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object TopZE {

    var mapData = JsonObject()
    private var serverName = Array(7) { "" }
    private var playerCount = Array(7) { 0 }
    private var maxPlayer = Array(7) { 0 }
    private var map = Array(7) { "" }
    private var mapChinese = Array(7) { "" }
    private var mapDifficulty = Array(7) { "" }
    private var mapTag = Array(7) { "" }
    private var mapInMapData = Array(7) { false }

    fun webForTopZE() {
        val token = Config.topZEToken
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
        //遍历数组中每一项中所需的数据
        for (i in 0 until responseDataJSON.size()) {
            val server = responseDataJSON.get(i).asJsonObject
            val id = when (server.get("RoomId").toString().replace("\"", "")) {
                "60079" -> {
                    1
                }

                "60109" -> {
                    2
                }

                "60112" -> {
                    3
                }

                "60053" -> {
                    4
                }

                "60847" -> {
                    5
                }

                "60848" -> {
                    6
                }

                else -> {
                    0
                }
            }

            serverName[id] = server.get("Name").toString().replace("\"", "")
            playerCount[id] = server.get("PlayerCount").toString().replace("\"", "").toInt()
            maxPlayer[id] = server.get("MaxPlayer").toString().replace("\"", "").toInt()
            map[id] = server.get("GameMap").toString().replace("\"", "")

            if (mapData.has(map[id])) {
                mapInMapData[id] = true
                val mapInfo = mapData.get(map[id]).asJsonObject
                mapChinese[id] = mapInfo.get("chinese").toString().replace("\"", "")
                mapDifficulty[id] = mapInfo.get("level").toString().replace("\"", "")
                mapTag[id] = mapInfo.get("tag").toString().replace("\"", "")
            } else {
                mapInMapData[id] = false
            }
        }
    }

    fun getData(id: Int = 0): String {
        when (id) {
            0 -> {
                var response = "   [5e ZE 服务器数据]\n"
                for (i in 1..6) {
                    response = response.plus("\n" + serverName[i])
                        .plus(" " + playerCount[i].toString() + "/" + maxPlayer[i] + "\n")
                        .plus("地图：" + map[i] + "\n")

                    if (mapInMapData[i]) {
                        response = response
                            .plus("译名：" + mapChinese[i] + "\n")
                            .plus("信息：" + mapDifficulty[i] + " " + mapTag[i] + "\n")
                    }
                }
                return response
            }

            in 1..6 -> {
                var response = (serverName[id])
                    .plus(" " + playerCount[id].toString() + "/" + maxPlayer[id] + "\n")
                    .plus("地图：" + map[id] + "\n")

                if (mapInMapData[id]) {
                    response = response
                        .plus("译名：" + mapChinese[id] + "\n")
                        .plus("信息：" + mapDifficulty[id] + " " + mapTag[id] + "\n")
                }
                return response
            }

            else -> {
                return "无此服务器"
            }
        }
    }

    fun initializeMapData() {
        if (dataFolder.resolve("mapData.json").exists()) {
            mapData = JsonParser.parseString(dataFolder.resolve("mapData.json").readText()).asJsonObject
        }
        else {
            updateMapData()
        }
    }

    fun updateMapData() {
        val serverURL = "https://raw.fastgit.org/mr2b-wmk/GOCommunity-ZEConfigs/master/mapchinese.cfg"
        val okHttpClient = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(serverURL)
            .get()
            .build()
        val response = okHttpClient.newCall(request).execute()
        val mapDataString = response.body!!.string()
        val mapDataJsonOut = mapDataString
            .replace("\"\t\t\"", "\": \"")
            .replace("\"\n\t\t\"", "\",\n\t\t\"")
            .replace("}\n\t\"", "},\n\t\"")
            .replace("\"\n\t{", "\":\n\t{")
            .replace("\"MapInfo\"", "")
        mapData = JsonParser.parseString(mapDataJsonOut).asJsonObject
        val path = Paths.get(File(dataFolder.path, "mapData.json").path)
        val inputStream = mapDataJsonOut.byteInputStream()
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
    }
}

object UB {
    //因为数组从0开始，偷个懒不用转换了
    private var serverName = Array(12) { "" }


    private var tScore = Array(12) { 0 }
    private var ctScore = Array(12) { 0 }
    private var map = Array(12) { "" }
    private var nextMap = Array(12) { "" }
    private var playerCount = Array(12) { 0 }
    private var serverAddress = Array(12) { "" }

    private val objRegex = "(?i)^(ze_obj_)".toRegex()
    private var announcedOBJMap = Array(12) { "" }
    private var announcedNextOBJMap = Array(12) { "" }

    var isWebsocketFailed = false
    private val okHttpClient = OkHttpClient.Builder()
        .build()

    //Event "server/init"
    //即客户端初始化
    fun serverInit(serverJSON: JsonObject) {

        //提取JSON中服务器数据
        val serverData = serverJSON.get("data").asJsonObject

        //剔除非 CSGO 服务器信息
        val serverappid = serverData.get("appid").toString().toInt()
        if (serverappid != 730) {
            return
        }

        // 剔除非 ZE 模式信息
        val serverMode = serverData.get("mode").toString().toInt()
        if (serverMode != 1) {
            return
        }

        //查询是哪个服务器
        val serverID = serverData.get("id").toString().toInt()

        //将服务器名写入数组
        serverName[serverID] =
            serverData.get("name").toString().replace("\"", "").replace(" Q群 749180050", "").replace("UB社区 ", "")

        //将服务器地址写入数组
        val serverHost = serverData.get("host").toString().replace("\"", "")
        val serverPort = serverData.get("port").toString()
        serverAddress[serverID] = serverHost.plus(":").plus(serverPort)

        //将比分写入数组
        tScore[serverID] = serverData.get("t_score").toString().toInt()
        ctScore[serverID] = serverData.get("ct_score").toString().toInt()

        //将地图写入数组
        map[serverID] = serverData.get("map").asJsonObject.get("name").toString().replace("\"", "")
        nextMap[serverID] = serverData.get("nextmap").asJsonObject.get("name").toString().replace("\"", "")

        //寻找OBJ!
        if (objRegex.containsMatchIn(map[serverID])) {
            sendOBJtoGroup(serverID, "Map")
        }
        if (objRegex.containsMatchIn(nextMap[serverID]) && (nextMap[serverID] != map[serverID])) {
            sendOBJtoGroup(serverID, "NextMap")
        }

        //根据player数组里的个数获取人数,将人数写入数组
        playerCount[serverID] = serverData.get("clients").asJsonArray.size()
    }

    //Event "server/client/connected"
    //即玩家连接到服务器
    //更新人数
    fun clientConnect(serverJSON: JsonObject) {
        playerCount[serverJSON.get("server").toString().toInt()] += 1
    }

    //Event "server/client/disconnect"
    //即玩家断开连接
    //更新人数
    fun clientDisconnect(serverJSON: JsonObject) {
        playerCount[serverJSON.get("server").toString().toInt()] -= 1
    }

    //Event "server/nextmap/changed"
    //即 RTV 选定下张图
    //更新下张图
    fun nextMapChange(serverJSON: JsonObject) {
        val serverID = serverJSON.get("server").toString().toInt()
        nextMap[serverID] = serverJSON.get("data").asJsonObject.get("name").toString().replace("\"", "")

        //寻找OBJ!
        if (objRegex.containsMatchIn(nextMap[serverID])) {
            sendOBJtoGroup(serverID, "NextMap")
        }
    }

    //Event "server/map/start"
    //下张图开始
    //更新地图
    fun mapStart(serverJSON: JsonObject) {
        val serverID = serverJSON.get("server").toString().toInt()
        map[serverID] = serverJSON.get("data").asJsonObject.get("name").toString().replace("\"", "")

        if (objRegex.containsMatchIn(map[serverID])) {
            sendOBJtoGroup(serverID, "Map")
        }
    }

    //Event "server/round_end"
    //回合结束
    //更新比分和人数
    fun roundEnd(serverJSON: JsonObject) {
        ctScore[serverJSON.get("server").toString().toInt()] =
            serverJSON.get("data").asJsonObject.get("ct_score").toString().toInt()
        tScore[serverJSON.get("server").toString().toInt()] =
            serverJSON.get("data").asJsonObject.get("t_score").toString().toInt()
        playerCount[serverJSON.get("server").toString().toInt()] =
            serverJSON.get("data").asJsonObject.get("player_count").toString().toInt()
    }

    fun webForUB() {

        val baseurl = "ws://app.moeub.com/ws?files=3"
        val mainRequest = Request.Builder()
            .get()
            .url(baseurl)
            .header("User-Agent", "Moeub Client")
            .build()
        //构建websocket客户端
        val websocket = okHttpClient.newWebSocket(mainRequest, object : WebSocketListener() {

            //在websocket连接收到返回信息时执行
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)

                //转数据为json
                val serverJSON = JsonParser.parseString(text).asJsonObject
                val event = serverJSON.get("event").toString().replace("\"", "")

                //根据返回Event确定动作
                if (event == "server/init") {
                    serverInit(serverJSON)
                }

                if (event == "server/client/connected") {
                    clientConnect(serverJSON)
                }

                if (event == "server/client/disconnect") {
                    clientDisconnect(serverJSON)
                }

                if (event == "server/nextmap/changed") {
                    nextMapChange(serverJSON)
                }

                if (event == "server/map/start") {
                    mapStart(serverJSON)
                }

                if (event == "server/round_end") {
                    roundEnd(serverJSON)
                }

            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                isWebsocketFailed = true
            }
        })
        for (i in 0 until 30) {
            if (isWebsocketFailed) {
                websocket.cancel()
                return
            }
            Thread.sleep(1000)
        }
        websocket.cancel()
    }

    private fun getMapChi(map: String): String? {
        return if (Data.UBMapChi[map] != null) {
            Data.UBMapChi[map]
        } else {
            val mapRequest = Request.Builder()
                .url("https://game.moeub.cn/api/maps?pageSize=50&name=$map")
                .get()
                .build()
            val mapResponse = okHttpClient.newCall(mapRequest).execute()
            val mapResponseBody = JsonParser.parseString(mapResponse.body!!.string()).asJsonObject
            val mapResponseData = mapResponseBody.get("data").asJsonObject.get("data").asJsonArray
            val mapChi = mapResponseData.get(0).asJsonObject.get("label").toString().replace("\"", "")
            Data.UBMapChi[map] = mapChi
            Data.save()
            mapChi
        }
    }

    fun getData(id: Int = 0): String {
        when (id) {
            0 -> {
                var response = "   [UB 社区 ZE 服务器数据]"
                for (i in 1 until 12) {
                    //判定有没有定下张地图
                    if (serverName[i] == "") {
                        continue
                    }
                    var serverNextMap = ""
                    if (!(nextMap[i] == map[i] || nextMap[i].length <= 3)) {
                        val serverNextMapChi = getMapChi(nextMap[i])
                        serverNextMap = "\n下张地图：" + nextMap[i] + "\n地图译名：$serverNextMapChi"
                    }

                    val serverMapChi = getMapChi(map[i])

                    response += "\n------------------------------\n".plus("【" + serverName[i] + "】").plus(" ")
                        .plus(playerCount[i]).plus("/64\n")
                        .plus("地图：" + map[i] + "\n译名：$serverMapChi")
                        .plus(serverNextMap)
                }
                return response
            }

            in 1..11 -> {
                //判定有没有定下张地图
                if (serverName[id] == "") {
                    return "无法获取此服务器信息"
                }

                val serverNextMap = if (!(nextMap[id] == map[id] || nextMap[id].length <= 3)) {
                    "下张地图：" + nextMap[id] + "\n地图译名：" + getMapChi(nextMap[id]) + "\n"
                } else {
                    ""
                }

                return "【" + serverName[id] + "】".plus(" ")
                    .plus(playerCount[id]).plus("/64\n")
                    .plus("地图：" + map[id] + "\n")
                    .plus("译名：" + getMapChi(map[id]) + "\n")
                    .plus("比分：" + ctScore[id] + "/" + tScore[id] + "\n")
                    .plus(serverNextMap)
                    .plus("地址：" + serverAddress[id])
            }

            else -> {
                return "无此服务器"
            }
        }
    }

    //寻找OBJ
    private fun sendOBJtoGroup(
        id: Int,
        announceReason: String
    ) {
        if (!FindOBJ.FindON) {
            return
        }

        var message = "发现OBJ!\n"
            .plus("UB社区 " + serverName[id] + "\n")

        message += when (announceReason) {
            "Map" -> {
                if (announcedOBJMap[id] == map[id]) {
                    return
                } else {
                    announcedOBJMap[id] = map[id]
                }
                ("地图：" + map[id] + "\n")
            }

            "NextMap" -> {
                if (announcedNextOBJMap[id] == nextMap[id]) {
                    return
                } else {
                    announcedNextOBJMap[id] = nextMap[id]
                }
                ("下张地图：" + nextMap[id] + "\n")
            }

            else -> {
                return
            }
        }
        message = message.plus("人数：" + playerCount[id] + "\n")
            .plus("地址：" + serverAddress[id])
        CoroutineScope(Dispatchers.IO).launch { FindOBJ.group.sendMessage(message) }
    }
}


object Zed {

    //初始化服务器信息参数
    private var serverName = Array(10) { "" }
    private var serverAddress = Array(10) { "" }
    private var map = Array(10) { "" }
    private var mapChi = Array(10) { "" }
    private var nextMap = Array(10) { "" }
    private var nominateMap = Array(10) { "" }
    private var playerCount = Array(10) { 0 }
    private var maxPlayer = Array(10) { 0 }
    private val objRegex = "(?i)^(ze_obj_)".toRegex()

    private val OBJAnnouncedFor = Array(10) { "" }

    fun webForZED() {
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

        //获取变换后JSON内信息
        for (i in 0 until serverListResponseDataJSON.size()) {

            val serverOBJStatus: MutableSet<String> = mutableSetOf()

            val server = serverListResponseDataJSON.get(i).asJsonObject

            //跳过ServerList中非 ZE/ZM 服务器
            if (server.get("serverGroupSortNumber").toString() != "1") {
                continue
            }

            //跳过 ZM 服务器
            if (server.get("serverName").toString().contains("ZM")) {
                continue
            }

            //确定服务器ID
            val serverNumber = server.get("serverID").toString().replace("\"", "").toInt().minus(100)

            //寻找服务器名与地址
            serverName[serverNumber] = server.get("serverName").toString().replace("\"", "").replace(" 僵尸逃跑", "")
            serverAddress[serverNumber] =
                server.get("ip").toString().plus(":").plus(server.get("port").toString()).replace("\"", "")

            //寻找服务器下张地图
            //如果没有则不显示
            val nextMapInJSON = server.get("nextMap").toString().replace("\"", "")
            if (nextMapInJSON.contains("暂无")) {
                nextMap[serverNumber] = ""
            } else {
                nextMap[serverNumber] = "下张地图：$nextMapInJSON\n"
                //确认下张地图是不是OBJ
                if (objRegex.containsMatchIn(nextMap[serverNumber])) {
                    serverOBJStatus.add("NextMap")
                }
            }

            //寻找服务器预定地图
            //如果没有则不显示
            val nominateMapInJSON = server.get("nominateMap").toString().replace("\"", "")
            if (nominateMapInJSON.contains("暂无")) {
                nominateMap[serverNumber] = ""
            } else {
                nominateMap[serverNumber] = "预定地图：$nominateMapInJSON\n"
                //确认预定地图是不是OBJ
                if (objRegex.containsMatchIn(nominateMap[serverNumber])) {
                    serverOBJStatus.add("NominateMap")
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                //构建 每个服务器的具体数据 请求
                val serverInfoBaseURL = "http://zombieden.cn/getserverinfo.php?address="
                val serverURL = serverInfoBaseURL.plus(serverAddress[serverNumber])
                val serverInfoRequest = Request.Builder()
                    .url(serverURL)
                    .get()
                    .build()
                val serverInfoResponse = okHttpclient.newCall(serverInfoRequest).execute()
                val serverData = serverInfoResponse.body!!.string()
                if (!serverData.contains("HostName")) {
                    return@launch
                }
                val serverDataJSON = JsonParser.parseString(serverData).asJsonObject
                //寻找服务器详细数据中需要的项
                playerCount[serverNumber] = serverDataJSON.get("Players").toString().toInt()
                maxPlayer[serverNumber] = serverDataJSON.get("MaxPlayers").toString().toInt()
                map[serverNumber] = serverDataJSON.get("Map").toString().replace("\"", "")
                mapChi[serverNumber] = if (serverDataJSON.has("MapChi")) {
                    serverDataJSON.get("MapChi").toString().replace("\"", "")
                } else {
                    ""
                }

                //确认地图是不是OBJ
                if (objRegex.containsMatchIn(map[serverNumber])) {
                    serverOBJStatus.add("Map")
                }

                //如果找到OBJ且FindOBJ开启则发送消息
                if (serverOBJStatus.isNotEmpty() && FindOBJ.FindON) {
                    sendOBJtoGroup(serverNumber, serverOBJStatus)
                } else {
                    OBJAnnouncedFor[serverNumber] = ""
                }
            }
        }
    }

    fun getData(id: Int = 0): String {
        when (id) {
            0 -> {
                var response = "   [僵尸乐园 ZE 服务器数据]\n"
                for (i in 1..7) {
                    response += "\n".plus(serverName[i]).plus("  ")
                        .plus(playerCount[i].toString() + "/" + maxPlayer[i] + "\n")
                        .plus("地图：" + map[i] + "\n")

                    //检查地图有无翻译
                    if (mapChi[i] != "") {
                        response = response.plus("译名：" + mapChi[i] + "\n")
                    }

                    response = response.plus("地址：" + serverAddress[i] + "\n")
                        .plus(nextMap[i])
                        .plus(nominateMap[i])
                }
                return response
            }

            in 1..7 -> {
                var response = serverName[id].plus("  ")
                    .plus(playerCount[id].toString() + "/" + maxPlayer[id] + "\n")
                    .plus("地图：" + map[id] + "\n")

                //检查地图有无翻译
                if (mapChi[id] != "") {
                    response = response.plus("译名：" + mapChi[id] + "\n")
                }

                response = response.plus("地址：" + serverAddress[id] + "\n")
                    .plus(nextMap[id])
                    .plus(nominateMap[id])
                return response
            }

            else -> {
                return "无此服务器"
            }
        }
    }

    private fun sendOBJtoGroup(
        id: Int,
        announcingReason: MutableSet<String>
    ) {

        var announceReason = "Map"
        var message = "有OBJ!\n"
            .plus(serverName[id] + "\n")
            .plus("地图：" + map[id] + "\n")
            .plus("人数：" + playerCount[id] + "/" + maxPlayer[id] + "\n")
            .plus("地址：" + serverAddress[id])


        if (announcingReason.contains("NextMap")) {
            message = message.plus("\n下张地图：" + nextMap[id])
            announceReason += ",NextMap"
        }

        if (announcingReason.contains("NominateMap")) {
            message = message.plus("\n预定地图：" + nominateMap[id])
            announceReason += ",NominateMap"
        }

        //如果本次发送的信息与之前一样则不再发送
        if (OBJAnnouncedFor[id] == announceReason) {
            return
        }

        CoroutineScope(Dispatchers.Default).launch { FindOBJ.group.sendMessage(message) }
        OBJAnnouncedFor[id] = announceReason
    }
}

fun webForFys(): String {
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
    for (i in 0 until serverDataArray.size()) {
        val server = serverDataArray.get(i).asJsonObject
        //排除非ZE服务器
        if (server.get("modeId").toString() != "1001") {
            continue
        }
        val serverName = server.get("name").toString().replace("\"", "")
        val serverMap = server.get("map").toString().replace("\"", "")
        val currentPlayers = server.get("currentPlayers").toString()
        val maxPlayers = server.get("maxPlayers").toString()
        val host = server.get("host").toString().replace("\"", "")
        val port = server.get("port").toString()
        serverString =
            serverString.plus("\n\n$serverName").plus(" 人数：$currentPlayers/$maxPlayers\n").plus("地图：$serverMap\n")
                .plus("地址：$host:$port")
    }
    return serverString
}