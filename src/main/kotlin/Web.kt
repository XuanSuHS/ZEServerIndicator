package top.xuansu.mirai.zeServerIndicator

import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request

fun web(): String {
    val token = "fec9d45f7416256443b5e051c200fc1d"
    val baseurl = "https://api-clan.rushbgogogo.com/api/v1/systemApp/gameServerRoomsList?mode=ze"
    val okHttpclient = OkHttpClient.Builder().build()
    val request = Request.Builder()
        .url(baseurl)
        .header("clan_auth_token", token)
        .get()
        .build()
    val response = okHttpclient.newCall(request).execute()
    val responseData = response.body!!.string()
    val responseDataJSON = JsonParser.parseString(responseData).asJsonObject.getAsJsonArray("message")
    var serverString = ""

    for (i in 0 until responseDataJSON.size()){
        val server = responseDataJSON.get(i)
        val name = server.asJsonObject.get("Name").toString()
        val playerCount = server.asJsonObject.get("PlayerCount").toString()
        val gameMap = server.asJsonObject.get("GameMap").toString()
        serverString = serverString.plus("$name  ").plus(playerCount+"人\n").plus(gameMap).plus("\n\n")
    }
    return serverString.replace("\"", "")
}