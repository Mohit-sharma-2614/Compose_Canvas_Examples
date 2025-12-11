package com.example.composeexamples

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import kotlin.random.Random


@Composable
fun CanvasExamples(){
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        drawRect(
            color = Color.Black,
            size = size/2f,
            topLeft = Offset(
                x = canvasWidth/3.5f,
                y = canvasHeight/3.5f
            )
        )
        drawLine(
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = canvasWidth, y = canvasHeight),
            color = Color.Blue
        )
        drawLine(
            start = Offset(x = canvasWidth/3.5f, y = canvasHeight/1.5f),
            end = Offset(x = canvasWidth/1.5f, y = canvasHeight/3.5f),
            color = Color.Blue
        )
    }
//    val textMeasurer = rememberTextMeasurer()
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        drawText(textMeasurer, "Hello")
//    }


    val textMeasurer = rememberTextMeasurer()

    Spacer(
        modifier = Modifier
            .drawWithCache {
                val measuredText =
                    textMeasurer.measure(
                        AnnotatedString("longTextSample"),
                        constraints = Constraints.fixedWidth((size.width * 2f / 3f).toInt()),
                        style = TextStyle(fontSize = 18.sp)
                    )

                onDrawBehind {
                    drawRect(color = Color.Cyan, size = measuredText.size.toSize())
                    drawText(measuredText)
                }
            }
            .fillMaxSize()
    )


}

@Composable
fun MyCanvas2(){
    Box(
        modifier = Modifier
            .size(height = 400.dp, width = 400.dp)
            .drawBehind{
                drawArc(
                    color = Color.Red,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = true,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
    )
}

@Composable
fun MyCanvas() {
    Canvas(
        modifier = Modifier
            .padding(20.dp)
            .size(300.dp)
    ) {
        drawRect(
            color = Color.Black,
            size = size
        )
        drawRect(
            color = Color.Red,
            topLeft = Offset(
                100f,
                100f
            ),
            size = Size(
                100f,
                100f
            ),
            style = Stroke(
                width = 3.dp.toPx(),
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Yellow),
                center = center,
                radius = 60.dp.toPx()
            ),
            radius = 70.dp.toPx(),
            center = center
        )
        drawArc(
            color = Color.Green,
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = true, // Try false here and it will show that the edge of ark is connected directly
            topLeft = Offset(100f,500f),
            size = Size(200f,200f),
            style = Stroke(
                width = 3.dp.toPx()
            )
        )
        drawOval(
            color = Color.Magenta,
            topLeft = Offset(500f,100f),
            size = Size(200f, 300f),
        )
        drawLine(
            color = Color.Cyan,
            start = Offset(300f,700f),
            end = Offset(700f,700f),
            strokeWidth = 5.dp.toPx()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCanvas(){
    MyCanvas()
}
