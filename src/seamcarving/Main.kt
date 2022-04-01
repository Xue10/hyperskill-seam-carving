package seamcarving

import java.awt.Color
import java.io.File
import java.util.Collections.max
import java.util.Collections.min
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val input = File(args[1])
    val output = File(args[3])
    val image = ImageIO.read(input)
    val width = image.width
    val height = image.height
    val energies = mutableMapOf<Pair<Int, Int>, Double>()
    for (x in 0 until width) {
        for (y in 0 until height) {
            var leftX = x - 1
            var rightX = x + 1
            var aboveY = y - 1
            var belowY = y + 1

            if (x == 0) {
                leftX++
                rightX++
            } else if (x == width - 1) {
                leftX--
                rightX--
            }
            if (y == 0) {
                aboveY++
                belowY++
            } else if (y == height - 1) {
                aboveY--
                belowY--
            }
            val above = Color(image.getRGB(x, aboveY))
            val below = Color(image.getRGB(x, belowY))
            val left = Color(image.getRGB(leftX, y))
            val right = Color(image.getRGB(rightX, y))
            val dx = (left.red - right.red).toDouble().pow(2) +
                    (left.green - right.green).toDouble().pow(2) +
                    (left.blue - right.blue).toDouble().pow(2)
            val dy = (above.red - below.red).toDouble().pow(2) +
                    (above.green - below.green).toDouble().pow(2) +
                    (above.blue - below.blue).toDouble().pow(2)
            val energy = sqrt(dx + dy)
            energies[Pair(x, y)] = energy
        }
    }
    val path = mutableMapOf<Pair<Int, Int>, Double>()
//    val maxEnergy = max(energies.values)
    for (x in 0 until width) {
        path[Pair(x, 0)] = energies[Pair(x, 0)]!!
    }
    for (y in 1 until height) {
        for (x in 0 until width) {
            val cellList = mutableListOf<Double>()
            path[Pair(x - 1, y - 1)]?.let { cellList.add(it) }
            path[Pair(x, y - 1)]?.let { cellList.add(it) }
            path[Pair(x + 1, y - 1)]?.let { cellList.add(it) }
            path[Pair(x, y)] = min(cellList) + energies[Pair(x, y)]!!
        }
    }
    val minKeyList = mutableListOf<Pair<Int, Int>>()
    val lastLine = mutableMapOf<Pair<Int, Int>, Double>()
    for (x in 0 until width) {
        lastLine[Pair(x, height - 1)] = path[Pair(x, height - 1)]!!
    }
    val minEnergySum = min(lastLine.values)
    var minKey = Pair(-1, -1)
    for (e in lastLine) {
        if (e.value == minEnergySum) {
            minKeyList.add(e.key)
            minKey = e.key
            break
        }
    }
    while (minKey.second > 0) {
        val h = minKey.second - 1
        var tempMinKey = Pair(minKey.first, h)
        var tempMin = path[tempMinKey]!!
        for (i in minKey.first -1 .. minKey.first + 1) {
            if ((path[Pair(i, h)] ?: tempMin) < tempMin) {
                tempMinKey = Pair(i, h)
                tempMin = path[tempMinKey]!!
            }
        }
        minKeyList.add(tempMinKey)
        minKey = tempMinKey
    }

    for ((x, y) in minKeyList) {
        image.setRGB(x, y, Color(255, 0, 0).rgb)
    }


    ImageIO.write(image, "png", output)
}
