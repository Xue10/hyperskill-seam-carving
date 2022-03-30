package seamcarving

import java.awt.Color
import java.io.File
import java.util.Collections.max
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val input = File(args[1])
    val output = File(args[3])
    val image = ImageIO.read(input)
    val energies = mutableMapOf<Pair<Int, Int>, Double>()
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            var leftX = x - 1
            var rightX = x + 1
            var aboveY = y - 1
            var belowY = y + 1

            if (x == 0) {
                leftX++
                rightX++
            } else if (x == image.width - 1) {
                leftX--
                rightX--
            }
            if (y == 0) {
                aboveY++
                belowY++
            } else if (y == image.height - 1) {
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
    val maxEnergy = max(energies.values)
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val intensity = (255.0 * energies[Pair(x, y)]!! / maxEnergy).toInt()
            val energyColor = Color(intensity, intensity, intensity)
            image.setRGB(x, y, energyColor.rgb)
        }
    }
    ImageIO.write(image, "png", output)
}
