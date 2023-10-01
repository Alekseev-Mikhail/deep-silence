package io.github.util

import kotlin.math.pow
import kotlin.math.sqrt

val Int.tick: Double
    get() = (this * 20).toDouble()

val Double.tSeconds
    get() = this / 20

fun stepIn2(first: Location, second: Location, step: Double) = first + (second - first) * (step / distanceIn2(first, second))

fun distanceIn2(first: Location, second: Location) = sqrt((first.z - second.z).pow(2) + (first.x - second.x).pow(2))
