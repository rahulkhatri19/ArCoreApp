package `in`.rahulkhatri.arcoreexplore

import `in`.rahulkhatri.arcoreexplore.common.rendering.ObjectRenderer
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.ar.core.Anchor
import java.io.IOException
import com.google.ar.core.Pose
import com.google.ar.core.AugmentedImage

class MainActivity : AppCompatActivity() {

    // Add a member variable to hold the maze model.
    // Add a member variable to hold the maze model.
    val mazeRenderer = ObjectRenderer()

    private val TINT_INTENSITY = 0.1f
    private val TINT_ALPHA = 1.0f
    private val TINT_COLORS_HEX = intArrayOf(
        0x000000, 0xF44336, 0xE91E63, 0x9C27B0, 0x673AB7, 0x3F51B5, 0x2196F3, 0x03A9F4, 0x00BCD4,
        0x009688, 0x4CAF50, 0x8BC34A, 0xCDDC39, 0xFFEB3B, 0xFFC107, 0xFF9800
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Replace the definition of the createOnGlThread() function with the
    // following code, which loads GreenMaze.obj.
    @Throws(IOException::class)
    fun createOnGlThread(context: Context?) {
        mazeRenderer.createOnGlThread(
            context, "models/green-maze/GreenMaze.obj", "models/frame_base.png"
        )
        mazeRenderer.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f)
    }

    // Adjust size of detected image and render it on-screen
    fun draw(
        viewMatrix: FloatArray?,
        projectionMatrix: FloatArray?,
        augmentedImage: AugmentedImage,
        centerAnchor: Anchor,
        colorCorrectionRgba: FloatArray?
    ) {
        val tintColor: FloatArray? = convertHexToColor(TINT_COLORS_HEX[augmentedImage.index % TINT_COLORS_HEX.size])
        val mazeEdgeSize = 492.65f // Magic number of maze size
        val maxImageEdgeSize = Math.max(
            augmentedImage.extentX,
            augmentedImage.extentZ
        ) // Get largest detected image edge size
        val anchorPose: Pose = centerAnchor.pose
        val mazeScaleFactor = maxImageEdgeSize / mazeEdgeSize // scale to set Maze to image size
        val modelMatrix = FloatArray(16)

        // OpenGL Matrix operation is in the order: Scale, rotation and Translation
        // So the manual adjustment is after scale
        // The 251.3f and 129.0f is magic number from the maze obj file
        // You mustWe need to do this adjustment because the maze obj file
        // is not centered around origin. Normally when you
        // work with your own model, you don't have this problem.
        val mazeModelLocalOffset = Pose.makeTranslation(
            -251.3f * mazeScaleFactor,
            0.0f,
            129.0f * mazeScaleFactor
        )
        anchorPose.compose(mazeModelLocalOffset).toMatrix(modelMatrix, 0)
        mazeRenderer.updateModelMatrix(
            modelMatrix,
            mazeScaleFactor,
            mazeScaleFactor / 10.0f,
            mazeScaleFactor
        ) // This line relies on a change in ObjectRenderer.updateModelMatrix later in this codelab.
        mazeRenderer.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor)
    }


    private fun convertHexToColor(colorHex: Int): FloatArray? {
        // colorHex is in 0xRRGGBB format
        val red: Float =
            (colorHex and 0xFF0000 shr 16) / 255.0f * TINT_INTENSITY
        val green: Float =
            (colorHex and 0x00FF00 shr 8) / 255.0f * TINT_INTENSITY
        val blue: Float =
            (colorHex and 0x0000FF) / 255.0f * TINT_INTENSITY
        return floatArrayOf(
            red,
            green,
            blue,
            TINT_ALPHA
        )
    }
}