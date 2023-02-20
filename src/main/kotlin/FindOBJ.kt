package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact

object FindOBJ {
    lateinit var group: Contact
    var FindON = false

    @OptIn(DelicateCoroutinesApi::class)
    fun sendOBJtoGroup(serverName:String,serverMap:String,serverPlayer:Int) {
        val message = "有OBJ!\n$serverName\n$serverMap\n人数：$serverPlayer"
        GlobalScope.launch { group.sendMessage(message) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendZEDOBJtoGroup(serverName:String, serverMap:String, serverNextMap:String, serverNominateMap:String, serverPlayer:Int) {
        val message = "有OBJ!\n$serverName\n$serverMap$serverNextMap$serverNominateMap\n人数：$serverPlayer"
        GlobalScope.launch { group.sendMessage(message) }
    }
}