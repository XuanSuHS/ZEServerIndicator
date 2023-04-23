package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact

object FindOBJ {
    lateinit var group: Contact
    var FindON = false
    private var serverNextMaptoMessage = ""
    private var serverNominateMaptoMessage = ""

    fun sendOBJtoGroup(serverName:String,serverMap:String,serverPlayer:Int) {
        val message = "有OBJ!\n$serverName\n$serverMap\n人数：$serverPlayer"
        CoroutineScope(Dispatchers.Default).launch { group.sendMessage(message) }
    }

    fun sendZEDOBJtoGroup(serverName:String, serverMap:String, serverNextMap:String, serverNominateMap:String, serverPlayer:Int) {
        serverNextMaptoMessage = if (serverNextMap.length >= 10) {
            serverNextMap + "\n"
        } else {
            ""
        }
        serverNominateMaptoMessage = if (serverNominateMap.length >= 10) {
            serverNominateMap + "\n"
        } else {
            ""
        }
        val message = "有OBJ!\n".plus(serverName + "\n").plus(serverMap + "\n").plus(serverNextMaptoMessage).plus(serverNominateMaptoMessage).plus("人数：$serverPlayer")
        CoroutineScope(Dispatchers.Default).launch { group.sendMessage(message) }
    }
}