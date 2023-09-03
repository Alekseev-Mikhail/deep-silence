package io.github.util

import io.github.util.RoomSystemResult.ALREADY
import io.github.util.RoomSystemResult.POINT_DOES_NOT_EXIST
import io.github.util.RoomSystemResult.ROOM_DOES_NOT_EXIST
import io.github.util.RoomSystemResult.SUCCESS

class RoomSystem {
    private val points = mutableMapOf<Int, Pair<Point, Point>>()
    private val rooms = mutableMapOf<String, Room>()

    operator fun get(name: String) = rooms[name]

    fun createRoom(creatorId: Int, name: String): Pair<Point, Point>? {
        if (rooms.containsKey(name)) return null
        val pair = getFirstAndSecondPoint(creatorId) ?: return null

        val absolutePoints = getAbsolutePoints(pair.first, pair.second)
        rooms[name] = Room(name, absolutePoints.first, absolutePoints.second)
        points.remove(creatorId)
        return absolutePoints
    }

    fun deleteRoom(name: String): Boolean {
        val room = rooms[name] ?: return false
        room.links.forEach { (link, _) ->
            link.links.remove(room)
        }
        rooms.remove(name)
        return true
    }

    fun deleteAllRooms() = rooms.clear()

    fun getAllRoomNames(): List<String> {
        val names = mutableListOf<String>()
        rooms.forEach { entry -> names.add(entry.key) }
        return names
    }

    fun link(firstName: String, secondName: String, creatorId: Int): RoomSystemResult {
        val roomPair = getFirstAndSecondRoom(firstName, secondName) ?: return ROOM_DOES_NOT_EXIST
        if (roomPair.first.links.contains(roomPair.second) || roomPair.second.links.contains(roomPair.first)) return ALREADY
        val pointPair = getFirstAndSecondPoint(creatorId) ?: return POINT_DOES_NOT_EXIST
        roomPair.first.links[roomPair.second] = pointPair
        roomPair.second.links[roomPair.first] = Pair(pointPair.second, pointPair.first)
        return SUCCESS
    }

    fun unlink(firstName: String, secondName: String): RoomSystemResult {
        val pair = getFirstAndSecondRoom(firstName, secondName) ?: return ROOM_DOES_NOT_EXIST
        if (pair.first.links.remove(pair.second) == null || pair.second.links.remove(pair.first) == null) return ALREADY
        return SUCCESS
    }

    fun getAllLinkNamesByRoomName(name: String): MutableList<String>? {
        val room = rooms[name] ?: return null
        val names = mutableListOf<String>()
        room.links.forEach { (link, pair) ->
            names.add("(${room.name} - ${link.name} 1: ${pair.first} 2: ${pair.second})")
        }
        return names
    }

    fun getAllLinkNames(): List<String> {
        val names = mutableListOf<String>()
        val was = mutableListOf<Room>()
        rooms.forEach { (_, room) ->
            room.links.forEach { (link, pair) ->
                if (!was.contains(link)) {
                    names.add("(${room.name} - ${link.name} 1: ${pair.first} 2: ${pair.second})")
                }
            }
            was.add(room)
        }
        return names
    }

    fun setFirstPoint(creatorId: Int, point: Point) {
        val pair = points[creatorId]
        if (pair == null) {
            points[creatorId] = Pair(point, EMPTY_POINT)
            return
        }
        points[creatorId] = Pair(point, pair.second)
    }

    fun setSecondPoint(creatorId: Int, point: Point) {
        val pair = points[creatorId]
        if (pair == null) {
            points[creatorId] = Pair(EMPTY_POINT, point)
            return
        }
        points[creatorId] = Pair(pair.first, point)
    }

    private fun getFirstAndSecondPoint(creatorId: Int): Pair<Point, Point>? {
        val first = points[creatorId]?.first
        val second = points[creatorId]?.second
        if (first == null || second == null) return null
        return Pair(first, second)
    }

    private fun getFirstAndSecondRoom(firstName: String, secondName: String): Pair<Room, Room>? {
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
