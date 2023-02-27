package com.aldajo92.xyparametricequations

import androidx.compose.ui.geometry.Offset

data class Point(val x: Float = 0f, val y: Float = 0f) {
    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)
    operator fun times(value: Float) = Point(x * value, y * value)
    operator fun div(value: Float) = Point(x / value, y / value)
}

fun Point.toOffset(step: Float) = Offset(x * step, y * step)
fun Point.translate(a: Float, b: Float) = Point(x + a, y + b)
fun Point.translate(point: Point) = Point(x + point.x, y + point.y)
fun Point.revertTranslate(a: Float, b: Float) = Point(x - a, y - b)
fun Point.revertTranslate(point: Point) = Point(x - point.x, y - point.y)
fun Point.invertYaxis() = Point(x, -y)
operator fun Float.times(point: Point): Point = Point(point.x * this, point.y * this)

fun Offset.translate(offset: Offset) = Offset(x + offset.x, y + offset.y)