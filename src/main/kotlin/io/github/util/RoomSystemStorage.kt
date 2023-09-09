package io.github.util

import io.github.PATH
import io.github.checkDirectory
import io.github.util.RoomSystemResult.FAIL
import io.github.util.RoomSystemResult.SUCCESS
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.util.Scanner

class RoomSystemStorage {
    private var insideRoomSystem = RoomSystem()

    val roomSystem
        get() = insideRoomSystem

    fun save(name: String) {
        val path = "$PATH/$name"
        checkDirectory(path)
        val file = FileWriter("$path/rs.json")
        val string = Json.encodeToString(withoutPoints(insideRoomSystem))
        file.write(string)
        file.close()
    }

    fun read(name: String): RoomSystemResult {
        val file = File("$PATH/$name/rs.json")
        if (file.exists()) {
            val obj = Json.decodeFromString<RoomSystem>(Scanner(file).nextLine())
            insideRoomSystem = obj
            return SUCCESS
        }
        return FAIL
    }
}
