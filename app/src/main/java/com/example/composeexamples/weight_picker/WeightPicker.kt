package com.example.composeexamples.weight_picker

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withRotation
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


/**
 * A composable function that displays a weight picker component.
 * It centers a `Scale` composable within a `Box` that fills the entire screen.
 * This acts as a container and entry point for the weight picker UI.
 */
@Composable
fun WeightPicker(){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Scale(
            style = ScaleStyle(
                scaleWidth = 150.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) { }
    }
}


/**
 * A composable that draws a circular scale for weight selection.
 *
 * This function creates a canvas element that draws an arc, representing a scale.
 * It uses a native canvas to draw a shadow effect for the scale.
 * The scale's appearance can be customized using the `ScaleStyle`.
 *
 * @param modifier The modifier to be applied to the canvas.
 * @param style The style configuration for the scale, including radius and width.
 * @param minWeight The minimum weight value on the scale.
 * @param maxWeight The maximum weight value on the scale.
 * @param initialWeight The initial weight value to be displayed.
 * @param onWeightChange A callback function that is invoked when the selected weight changes. It receives the new weight as an `Int`.
 */
@Composable
fun Scale(
    modifier: Modifier = Modifier,
    style: ScaleStyle = ScaleStyle(),
    minWeight: Int = 20,
    maxWeight: Int = 250,
    initialWeight: Int = 80,
    onWeightChange: (Int) -> Unit,
){
    // Retrieve the radius and scale width from the provided style.
    val radius = style.radius
    val scaleWidth = style.scaleWidth
    
    // State to store the center of the Canvas (DrawScope).
    var center by remember {
        mutableStateOf(Offset.Zero)
    }
    // State to store the calculated center of the scale circle.
    // This center is adjusted so the arc is visible within the screen bounds.
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    // State to store the current rotation angle, likely controlled by touch input later.
    // Fixed at 0f for now as the touch logic is missing.
    val angle by remember { mutableFloatStateOf(0f) }
    
    Canvas(
        modifier = modifier
    ) {
        // Get the center of the current DrawScope (the Canvas composable).
        center = this.center
        
        // Calculate the center of the drawing circle.
        // It's placed at the horizontal center (center.x) of the Canvas.
        // The vertical center (circleCenter.y) is pushed down (off-screen or near the edge)
        // to make the resulting arc appear centered horizontally and near the top.
        // The adjustment `scaleWidth.toPx() / 2f + radius.toPx()` places the center
        // exactly at the bottom of the visible arc's radius.
        circleCenter = Offset(
            // This is the center of the DrawScope
            center.x,
            // scaleWidth is the stroke width of the circle and we want to calculate the radius of the circle from the
            // Mid of that scaleWidth.
            scaleWidth.toPx() / 2f + radius.toPx()
        )
        
        // After applying the scaleWidth there is two radius
        // Calculate the Outer and Inner radii of the scale ring based on the style's radius and width.
        // 1. OuterRadius: From circleCenter to the outside edge of the stroke.
        val outerRadius = radius.toPx() + scaleWidth.toPx() / 2f
        // 2. InnerRadius: From circleCenter to the inside edge of the stroke.
        val innerRadius = radius.toPx() - scaleWidth.toPx() / 2f
        
        // We can not draw shadows in compose canvas so we need to use the
        // Native canvas here to draw the circular scale with a shadow effect.
        drawContext.canvas.nativeCanvas.apply {
            drawCircle(
                circleCenter.x,
                circleCenter.y,
                radius.toPx(),
                Paint().apply {
                    // Set up the Paint object for the scale ring.
                    strokeWidth = scaleWidth.toPx()
                    color = Color.WHITE // The color of the scale ring itself
                    setStyle(Paint.Style.STROKE) // Draw only the outline/stroke
                    
                    // Apply a shadow effect using a shadow layer, which requires the native canvas.
                    setShadowLayer(
                        60f, // Blur radius of the shadow
                        0f,  // Horizontal offset
                        0f,  // Vertical offset
                        Color.argb(50,0,0,0) // Shadow color (transparent black)
                    )
                }
            )
        }
        
        // Draw Lines (Scale Markings)
        for(i in minWeight..maxWeight) {
            // Calculate the angle for the current weight mark (i).
            // (i - initialWeight) shifts the scale so 'initialWeight' is at the starting position (e.g., top).
            // 'angle' is the user's rotation adjustment.
            // -90 is applied to start the angle calculation from the positive x-axis (standard polar coordinate system),
            // which corresponds to the right side of the circle, making the top of the circle 90 degrees.
            val angleInRad = (i - initialWeight + angle - 90) * (PI / 180f).toFloat()
            
            // Determine the type of line (mark) based on the weight value.
            val lineType = when {
                i % 10 == 0 -> LineType.TenStep // Major mark (e.g., 50, 60, 70)
                i % 5 == 0 -> LineType.FiveStep // Medium mark (e.g., 55, 65, 75)
                else -> LineType.Normal // Minor mark
            }
            
            // Determine the length and color based on the line type.
            val lineLength = when(lineType) {
                LineType.Normal -> style.normalLineLength.toPx()
                LineType.FiveStep -> style.fiveStepLineLength.toPx()
                LineType.TenStep -> style.tenStepLineLength.toPx()
            }
            val lineColor = when(lineType) {
                LineType.Normal -> style.normalLineColor
                LineType.FiveStep -> style.fiveStepLineColor
                LineType.TenStep -> style.tenStepLineColor
            }
            
            // Calculate the starting position (inner end) of the line mark.
            // Radius is measured from the circleCenter.
            // The length is subtracted from the outerRadius to get the starting point.
            val lineStart = Offset(
                x = (outerRadius - lineLength) * cos(angleInRad) + circleCenter.x,
                y = (outerRadius - lineLength) * sin(angleInRad) + circleCenter.y
            )
            // Calculate the ending position (outer end) of the line mark.
            val lineEnd = Offset(
                x = (outerRadius ) * cos(angleInRad) + circleCenter.x,
                y = (outerRadius ) * sin(angleInRad) + circleCenter.y
            )
            
            // Use native canvas to draw the weight text for TenStep marks.
            drawContext.canvas.nativeCanvas.apply {
                if(lineType is LineType.TenStep){
                    // Calculate the radius for the text placement, placing it slightly inside the line start.
                    val textRadius = outerRadius - lineLength - 5.dp.toPx() - style.textSize.toPx()
                    
                    // Calculate the position (x, y) for the text on the circle.
                    val x = (textRadius) *
                            cos(angleInRad) + circleCenter.x
                    val y = (textRadius) *
                            sin(angleInRad) + circleCenter.y
                    
                    // Rotate the text so it is perpendicular to the scale arc.
                    withRotation(
                        degrees = angleInRad * (180f / PI.toFloat()) + 90f, // +90 to align text vertically
                        pivotX = x,
                        pivotY = y
                    ) {
                        // Draw the weight value as text.
                        drawText(
                            abs(i).toString(),
                            x,
                            y,
                            Paint().apply {
                                textSize = style.textSize.toPx()
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    }
                }
            }
            
            // Draw the actual line mark on the Compose Canvas.
            drawLine(
                color = lineColor,
                start = lineStart,
                end = lineEnd,
                strokeWidth = 3.dp.toPx()
            )
        }
        
    }
    
    
}

@Preview(showBackground = true)
@Composable
fun WeightPickerPreview(){
    WeightPicker()
}

