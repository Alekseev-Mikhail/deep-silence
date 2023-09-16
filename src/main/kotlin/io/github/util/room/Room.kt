package io.github.util.room

import io.github.util.EMPTY_POINT
import io.github.util.Point
import kotlinx.serialization.Serializable

val EMPTY_ROOM = Room("empty", EMPTY_POINT, EMPTY_POINT)

@Serializable
data class Room(val name: String, val firstPoint: Point, val secondPoint: Point) {
    val links = mutableMapOf<String, Pair<Point, Point>>()
}
