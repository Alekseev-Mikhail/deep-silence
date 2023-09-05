package io.github.util

import io.github.PATH
import io.github.util.RoomSystemResult.FAIL
import io.github.util.RoomSystemResult.SUCCESS
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.NullPointerException
import java.util.Scanner

class RoomSystemStorage {
    private var insideRoomSystem = RoomSystem()

    val roomSystem
        get() = insideRoomSystem

    fun save(name: String): RoomSystemResult {
        return try {
            val file = FileWriter("$PATH/$name/rs.json")
            val string = Json.encodeToString(roomSystem)

            println(string)

            file.write(string)
            file.close()
            SUCCESS
        } catch (e: IOException) {
            FAIL
        }
    }

    fun read(name: String): RoomSystemResult {
        return try {
            val file = File("$PATH/$name/rs.json")
            val obj = Json.decodeFromString<RoomSystem>(Scanner(file).nextLine())

            println(obj)

            insideRoomSystem = obj
            SUCCESS
        } catch (e: NullPointerException) {
            FAIL
        }
    }
}
