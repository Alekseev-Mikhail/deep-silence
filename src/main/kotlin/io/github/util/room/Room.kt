package io.github.util.room

import io.github.util.Point
import kotlinx.serialization.Serializable

@Serializable
data class Room(val name: String, val firstPoint: Point, val secondPoint: Point) {
    val links = mutableMapOf<String, Pair<Point, Point>>()
}
