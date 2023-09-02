package io.github.util

class RoomSystem {
    private val points = mutableMapOf<Int, Pair<Point, Point>>()
    private val rooms = mutableMapOf<String, Room>()

    operator fun get(name: String) = rooms[name]

    fun createRoom(creatorId: Int, name: String): Pair<Point, Point>? {
        if (rooms.containsKey(name)) return null
        val first = points[creatorId]?.first
        val second = points[creatorId]?.second
        if (first == null || second == null) return null

        val absolutePoints = getAbsolutePoints(first, second)
        rooms[name] = Room(name, absolutePoints.first, absolutePoints.second)
        points.remove(creatorId)
        return absolutePoints
    }

    fun deleteRoom(name: String): Boolean {
        val room = rooms[name] ?: return false
        room.links.forEach { link ->
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

    fun link(firstName: String, secondName: String): Boolean {
        val first = rooms[firstName]
        val second = rooms[secondName]
        if (first == null || second == null) return false
        first.links.add(second)
        second.links.add(first)
        return true
    }

    fun unlink(firstName: String, secondName: String): Boolean {
        val first = rooms[firstName]
        val second = rooms[secondName]
        if (first == null || second == null) return false
        first.links.remove(second)
        second.links.remove(first)
        return true
    }

    fun getAllLinkNames(): List<String> {
        val names = mutableListOf<String>()
        val was = mutableListOf<Room>()
        rooms.forEach { (_, room) ->
            room.links.forEach { link ->
                if (!was.contains(link)) {
                    names.add("(${room.name} - ${link.name})")
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
