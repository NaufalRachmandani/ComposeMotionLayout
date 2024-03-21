package com.naufal.composemotionlayout

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import com.naufal.composemotionlayout.ui.theme.ComposeMotionLayoutTheme
import kotlinx.coroutines.launch
import java.util.EnumSet

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeMotionLayoutTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {

                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            ProfileHeader(
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMotionApi::class, ExperimentalFoundationApi::class)
@Composable
fun ProfileHeader(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val motionScene = remember {
        context.resources
            .openRawResource(R.raw.motion_scene_2)
            .readBytes()
            .decodeToString()
    }

    val density = LocalDensity.current
    val draggedDownAnchorTop = with(LocalDensity.current) { 400.dp.toPx() }
    val anchors = DraggableAnchors<AnchoredDraggableCardState> {
        AnchoredDraggableCardState.DRAGGED_DOWN at draggedDownAnchorTop
        AnchoredDraggableCardState.DRAGGED_UP at 0f
    }
    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = AnchoredDraggableCardState.DRAGGED_DOWN,
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 1.dp.toPx() } },
            animationSpec = tween(),
        )
    }

    val offset = if (anchoredDraggableState.offset.isNaN()) 0f else anchoredDraggableState.offset
    val progress = (1 - (offset / draggedDownAnchorTop)).coerceIn(0f, 1f)

    MotionLayout(
        motionScene = MotionScene(content = motionScene),
        progress = progress,
        modifier = modifier,
        debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL)
    ) {
        val properties = motionProperties(id = "profile_pic")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFCEE1E7))
                .layoutId("container")
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .anchoredDraggable(anchoredDraggableState, Orientation.Vertical)
                .background(Color(0xFF469AFF))
                .layoutId("box")
        )

        Image(
            painter = painterResource(id = R.drawable.inori1),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = properties.value.color("background"),
                    shape = CircleShape
                )
                .layoutId("profile_pic")
        )

        Text(
            text = "Inori Minase",
            fontSize = 24.sp,
            modifier = Modifier.layoutId("username"),
            color = properties.value.color("background")
        )

        Icon(
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    scope.launch {
                        anchoredDraggableState.animateTo(targetValue = AnchoredDraggableCardState.DRAGGED_UP)
                    }
                }
                .layoutId("btn_expand"),
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "",
            tint = Color.White
        )

        Icon(
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    scope.launch {
                        anchoredDraggableState.animateTo(targetValue = AnchoredDraggableCardState.DRAGGED_DOWN)
                    }
                }
                .layoutId("btn_minimize"),
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "",
            tint = Color.White
        )
    }
}