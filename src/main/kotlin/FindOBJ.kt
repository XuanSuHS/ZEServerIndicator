package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact

object FindOBJ {
    lateinit var group: Contact
    var FindON = false
    var serverNextMaptoMessage = ""
    var serverNominateMaptoMessage = ""

    @OptIn(DelicateCoroutinesApi::class)
    fun sendOBJtoGroup(serverName:String,serverMap:String,serverPlayer:Int) {
        val message = "有OBJ!\n$serverName\n$serverMap\n人数：$serverPlayer"
        GlobalScope.launch { group.sendMessage(message) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendZEDOBJtoGroup(serverName:String, serverMap:String, serverNextMap:String, serverNominateMap:String, serverPlayer:Int) {
        serverNextMaptoMessage = if (serverNextMap.length >= 10) {
            serverNextMap
        } else {
            ""
        }
        serverNominateMaptoMessage = if (serverNominateMap.length >= 10) {
            serverNominateMap
        } else {
            ""
        }
        val message = "有OBJ!\n$serverName\n$serverMap$serverNextMaptoMessage$serverNominateMaptoMessage\n人数：$serverPlayer"
        GlobalScope.launch { group.sendMessage(message) }
    }
}