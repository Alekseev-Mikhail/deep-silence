package io.github.util

import io.github.util.RoomSystemResult.ALREADY
import io.github.util.RoomSystemResult.ONE_WAY_LINK
import io.github.util.RoomSystemResult.POINT_DOES_NOT_EXIST
import io.github.util.RoomSystemResult.ROOM_DOES_NOT_EXIST
import io.github.util.RoomSystemResult.SAME_ROOM
import io.github.util.RoomSystemResult.SUCCESS
import kotlinx.serialization.Serializable

@Serializable
class RoomSystem() {
    private val points = mutableMapOf<Int, Pair<Point, Point>>()
    private val rooms = mutableMapOf<String, Room>()

    constructor(roomSystem: RoomSystem) : this() {
        roomSystem.rooms.forEach { (name, room) -> rooms[name] = room }
    }

    fun createRoom(playerId: Int, name: String): RoomSystemResult {
        if (rooms.containsKey(name)) return ALREADY
        val pair = points[playerId] ?: return POINT_DOES_NOT_EXIST
        if (pair.first.isEmpty || pair.second.isEmpty) return POINT_DOES_NOT_EXIST
        val absolutePoints = getAbsolutePoints(pair.first, pair.second)
        rooms[name] = Room(name, absolutePoints.first, absolutePoints.second)
        points.remove(playerId)
        return SUCCESS
    }

    fun deleteRoom(name: String): RoomSystemResult {
        val room = rooms[name] ?: return ROOM_DOES_NOT_EXIST
        var wasOneWay = false
        rooms.remove(name)
        room.links.forEach { (linkName, _) ->
            val link = rooms[linkName]
            if (link != null) link.links.remove(room.name) else wasOneWay = true
        }
        return if (wasOneWay) ONE_WAY_LINK else SUCCESS
    }

    fun deleteAllRooms() = rooms.clear()

    fun getRoom(name: String) = rooms[name]

    fun getAllRoomNames(): List<String> {
        val names = mutableListOf<String>()
        rooms.forEach { entry -> names.add(entry.key) }
        return names
    }

    fun link(playerId: Int, firstName: String, secondName: String): RoomSystemResult {
        if (firstName == secondName) return SAME_ROOM
        val roomPair = getRooms(firstName, secondName) ?: return ROOM_DOES_NOT_EXIST
        if (roomPair.first.links.contains(roomPair.second.name) || roomPair.second.links.contains(roomPair.first.name)) return ALREADY
        val pointPair = points[playerId] ?: return POINT_DOES_NOT_EXIST
        roomPair.first.links[roomPair.second.name] = pointPair
        roomPair.second.links[roomPair.first.name] = Pair(pointPair.second, pointPair.first)
        points.remove(playerId)
        return SUCCESS
    }

    fun unlink(firstName: String, secondName: String): RoomSystemResult {
        if (firstName == secondName) return SAME_ROOM
        val pair = getRooms(firstName, secondName) ?: return ROOM_DOES_NOT_EXIST
        if (pair.first.links.remove(pair.second.name) == null || pair.second.links.remove(pair.first.name) == null) return ALREADY
        return SUCCESS
    }

    fun unlinkAll() {
        rooms.forEach { (_, room) ->
            room.links.clear()
        }
    }

    fun getAllLinkNamesByRoomName(name: String): MutableList<String>? {
        val room = rooms[name] ?: return null
        val names = mutableListOf<String>()
        room.links.forEach { (linkName, pair) ->
            names.add("(${room.name} - $linkName 1: ${pair.first} 2: ${pair.second})")
        }
        return names
    }

    fun getAllLinkNames(): List<String> {
        val names = mutableListOf<String>()
        val was = mutableListOf<String>()
        rooms.forEach { (_, room) ->
            room.links.forEach { (linkName, pair) ->
                if (!was.contains(linkName)) {
                    names.add("(${room.name} - $linkName 1: (${pair.first}) 2: (${pair.second}))")
                }
            }
            was.add(room.name)
        }
        return names
    }

    fun setFirstPoint(playerId: Int, point: Point) {
        val pair = points[playerId]
        if (pair == null) {
            points[playerId] = Pair(point, EMPTY_POINT)
            return
        }
        points[playerId] = Pair(point, pair.second)
    }

    fun setSecondPoint(playerId: Int, point: Point) {
        val pair = points[playerId]
        if (pair == null) {
            points[playerId] = Pair(EMPTY_POINT, point)
            return
        }
        points[playerId] = Pair(pair.first, point)
    }

    fun getPoints(playerId: Int): Pair<Point, Point>? = points[playerId]

    private fun getRooms(firstName: String, secondName: String): Pair<Room, Room>? {
        val first = rooms[firstName]
        val second = rooms[secondName]
        if (first == null || second == null) return null
        return Pair(first, second)
    }

    private fun getAbsolutePoints(firstPoint: Point, secondPoint: Point): Pair<Point, Point> {
        var firstX = firstPoint.x
        var firstY = firstPoint.y
        var firstZ = firstPoint.z

        var secondX = secondPoint.x
        var secondY = secondPoint.y
        var secondZ = secondPoint.z

        if (firstX <= secondX) secondX += 1 else firstX += 1
        if (firstY <= secondY) secondY += 1 else firstY += 1
        if (firstZ <= secondZ) secondZ += 1 else firstZ += 1

        return Pair(Point(firstX, firstY, firstZ), Point(secondX, secondY, secondZ))
    }
}

fun withoutPoints(roomSystem: RoomSystem) = RoomSystem(roomSystem)
