package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util.Collections.min
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val input = File(args[1])
    val output = File(args[3])
    val widthToRemove = args[5].toInt()
    val heightToRemove = args[7].toInt()
    val image = ImageIO.read(input)
    var outImage = image
    while (outImage.width + widthToRemove > image.width) {
        val energies = calculateEnergy(outImage)
        val verticalSeam = getVerticalSeam(outImage, energies)
        outImage = removeVerticalSeam(outImage, verticalSeam)
    }
    while (outImage.height + heightToRemove > image.height) {
        val energies = calculateEnergy(outImage)
        val horizontalSeam = getHorizontalSeam(outImage, energies)
        outImage = removeHorizontalSeam(outImage, horizontalSeam)
    }

    ImageIO.write(outImage, "png", output)
}

fun calculateEnergy(image: BufferedImage): Map<Pair<Int, Int>, Double> {
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
    return energies
}

fun getVerticalSeam(image: BufferedImage, energies: Map<Pair<Int, Int>, Double>): Map<Int, Int> {
    val minEnergyMap = mutableMapOf<Pair<Int, Int>, Double>()
    val width = image.width
    val height = image.height
    for (x in 0 until width) {
        minEnergyMap[Pair(x, 0)] = energies[Pair(x, 0)]!!
    }
    for (y in 1 until height) {
        for (x in 0 until width) {
            val cellList = mutableListOf<Double>()
            minEnergyMap[Pair(x - 1, y - 1)]?.let { cellList.add(it) }
            minEnergyMap[Pair(x, y - 1)]?.let { cellList.add(it) }
            minEnergyMap[Pair(x + 1, y - 1)]?.let { cellList.add(it) }
            minEnergyMap[Pair(x, y)] = min(cellList) + energies[Pair(x, y)]!!
        }
    }
    val seam = mutableMapOf<Int, Int>()
    val lastLine = mutableMapOf<Pair<Int, Int>, Double>()
    for (x in 0 until width) {
        lastLine[Pair(x, height - 1)] = minEnergyMap[Pair(x, height - 1)]!!
    }
    val minEnergySum = min(lastLine.values)
    var minKey = Pair(-1, -1)
    for (e in lastLine) {
        if (e.value == minEnergySum) {
            seam[e.key.second] = e.key.first
            minKey = e.key
            break
        }
    }
    while (minKey.second > 0) {
        val h = minKey.second - 1
        var tempMinKey = Pair(minKey.first, h)
        var tempMin = minEnergyMap[tempMinKey]!!
        for (i in minKey.first -1 .. minKey.first + 1) {
            if ((minEnergyMap[Pair(i, h)] ?: tempMin) < tempMin) {
                tempMinKey = Pair(i, h)
                tempMin = minEnergyMap[tempMinKey]!!
            }
        }
        seam[tempMinKey.second] = tempMinKey.first
        minKey = tempMinKey
    }
    return seam
}

fun getHorizontalSeam(image: BufferedImage, energies: Map<Pair<Int, Int>, Double>): Map<Int, Int> {
    val minEnergyMap = mutableMapOf<Pair<Int, Int>, Double>()
    val width = image.width
    val height = image.height
    for (y in 0 until height) {
        minEnergyMap[Pair(0, y)] = energies[Pair(0, y)]!!
    }
    for (x in 1 until width) {
        for (y in 0 until height) {
            val cellList = mutableListOf<Double>()
            minEnergyMap[Pair(x - 1, y - 1)]?.let { cellList.add(it) }
            minEnergyMap[Pair(x - 1, y)]?.let { cellList.add(it) }
            minEnergyMap[Pair(x - 1, y + 1)]?.let { cellList.add(it) }
            minEnergyMap[Pair(x, y)] = min(cellList) + energies[Pair(x, y)]!!
        }
    }
    val seam = mutableMapOf<Int, Int>()
    val lastLine = mutableMapOf<Pair<Int, Int>, Double>()
    for (y in 0 until height) {
        lastLine[Pair(width - 1, y)] = minEnergyMap[Pair(width - 1, y)]!!
    }
    val minEnergySum = min(lastLine.values)
    var minKey = Pair(-1, -1)
    for (e in lastLine) {
        if (e.value == minEnergySum) {
            seam[e.key.first] = e.key.second
            minKey = e.key
            break
        }
    }
    while (minKey.first > 0) {
        val w = minKey.first - 1
        var tempMinKey = Pair(w, minKey.second)
        var tempMin = minEnergyMap[tempMinKey]!!
        for (i in minKey.second -1 .. minKey.second + 1) {
            if ((minEnergyMap[Pair(w, i)] ?: tempMin) < tempMin) {
                tempMinKey = Pair(w, i)
                tempMin = minEnergyMap[tempMinKey]!!
            }
        }
        seam[tempMinKey.first] = tempMinKey.second
        minKey = tempMinKey
    }
    return seam
}

fun removeVerticalSeam(image: BufferedImage, seam: Map<Int, Int>): BufferedImage {
    val width = image.width
    val height = image.height
    val newImage = BufferedImage(width - 1, height, image.type)
    for (y in 0 until height) {
        val xSeam = seam[y]!!
        for (x in 0 until width) {
            if (x == xSeam) {
                continue
            }
            val xOut = if (x > xSeam) x - 1 else x
            newImage.setRGB(xOut, y, image.getRGB(x, y))
        }
    }
    return newImage
}

fun removeHorizontalSeam(image: BufferedImage, seam: Map<Int, Int>): BufferedImage {
    val width = image.width
    val height = image.height
    val newImage = BufferedImage(width, height - 1, image.type)
    for (x in 0 until width) {
        val ySeam = seam[x]!!
        for (y in 0 until height) {
            if (y == ySeam) {
                continue
            }
            val yOut = if (y > ySeam) y - 1 else y
            newImage.setRGB(x, yOut, image.getRGB(x, y))
        }
    }
    return newImage
}