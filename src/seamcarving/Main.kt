package seamcarving

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val input = File(args[1])
    val output = File(args[3])
    val image = ImageIO.read(input)
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val color = Color(image.getRGB(x, y))
            val r = 255 - color.red
            val g = 255 - color.green
            val b = 255 - color.blue
            val negative = Color(r, g, b)
            image.setRGB(x, y, negative.rgb)
        }
    }
    ImageIO.write(image, "png", output)
}
