package io.github.util

data class Room(val name: String, val firstPoint: Point, val secondPoint: Point) {
    val links = mutableMapOf<Room, Pair<Point, Point>>()
}
