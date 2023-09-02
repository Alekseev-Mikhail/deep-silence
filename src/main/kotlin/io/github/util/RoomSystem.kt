package io.github.util

class RoomSystem {
    private val points = mutableMapOf<Int, Pair<Point, Point>>()
    private val rooms = mutableMapOf<String, Room>()

    fun addRoom(creatorId: Int, name: String): Pair<Point, Point>? {
        val first = points[creatorId]?.first
        val second = points[creatorId]?.second
        if (first == null || second == null) return null

        val absolutePoints = getAbsolutePoints(first, second)
        rooms[name] = Room(absolutePoints.first, absolutePoints.second)
        points.remove(creatorId)
        return absolutePoints
    }

    fun deleteRoom(name: String): Boolean = rooms.remove(name) != null

    fun deleteAllRoom() = rooms.clear()

    operator fun get(name: String) = rooms[name]

    fun getAllName(): List<String> {
        val listOfRooms = mutableListOf<String>()
        rooms.forEach { entry -> listOfRooms.add(entry.key) }
        return listOfRooms
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
