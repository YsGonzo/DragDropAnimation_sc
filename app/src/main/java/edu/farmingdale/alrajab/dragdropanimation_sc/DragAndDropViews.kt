package edu.farmingdale.alrajab.dragdropanimation_sc

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import edu.farmingdale.alrajab.dragdropanimation_sc.databinding.ActivityDragAndDropViewsBinding
/*
*   Author:  Jayson Gonzalez
*   Date:    11/30/2023
*   Purpose: Enable Drag and Drop on all arrows to all placeholders, Draw borders for the placeholders,
*            Highlight the placeholder before dropping, Draw an animation, Add a button to start the animation,
*            Apply translation and rotation animations
*/
class DragAndDropViews : AppCompatActivity() {
    lateinit var binding: ActivityDragAndDropViewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDragAndDropViewsBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.holder01.setOnDragListener(arrowDragListener)
        binding.holder02.setOnDragListener(arrowDragListener)
        binding.holder03.setOnDragListener(arrowDragListener)
        binding.holder04.setOnDragListener(arrowDragListener)
        binding.holder05.setOnDragListener(arrowDragListener)


        binding.upMoveBtn.setOnLongClickListener(onLongClickListener)
        binding.downMoveBtn.setOnLongClickListener(onLongClickListener)
        binding.forwardMoveBtn.setOnLongClickListener(onLongClickListener)
        binding.backMoveBtn.setOnLongClickListener(onLongClickListener)


        val rocketImage: ImageView = findViewById(R.id.rocket_image)
        rocketImage.setBackgroundResource(R.drawable.flying_rocket)
        binding.startAnimationBtn.setOnClickListener {
            val rocketAnimation = rocketImage.background as AnimationDrawable
            if (rocketAnimation.isRunning) {
                    rocketAnimation.stop()
                } else {
                    rocketAnimation.start()
                }
        }

        binding.rotateBtn.setOnClickListener {
            val rotationAnimator = ObjectAnimator.ofFloat(
                rocketImage, "rotation", 0f, 360f
            )
            // The duration of the rotation (in milliseconds)
            rotationAnimator.duration = 1000
            // Sets up the interpolator for smooth rotation
            rotationAnimator.interpolator = LinearInterpolator()
            rotationAnimator.start()
        }

        binding.translateBtn.setOnClickListener {
            val translationAnimator = ObjectAnimator.ofFloat(
                rocketImage, "translationX", 0f, 200f, 0f
            )
            // The duration of the translation (in milliseconds)
            translationAnimator.duration = 1000
            // Sets up the interpolator for smooth translation
            translationAnimator.interpolator = LinearInterpolator()
            translationAnimator.start()
        }
    }



    private val onLongClickListener = View.OnLongClickListener { view: View ->
        (view as? Button)?.let {

            val item = ClipData.Item(it.tag as? CharSequence)

            val dragData = ClipData( it.tag as? CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
            val myShadow = ArrowDragShadowBuilder(it)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(dragData, myShadow, null, 0)
            } else {
                it.startDrag(dragData, myShadow, null, 0)
            }

            true
        }
        false
    }

    private val arrowDragListener = View.OnDragListener { view, dragEvent ->
        (view as? ImageView)?.let {
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    return@OnDragListener true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Highlight the placeholder when the drag enters
                    view.setBackgroundResource(R.drawable.border_highlighted)
                    return@OnDragListener true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    // Reset the placeholder when the drag exits
                    view.setBackgroundResource(R.drawable.border)
                    return@OnDragListener true
                }
                // No need to handle this for our use case.
                DragEvent.ACTION_DRAG_LOCATION -> {
                    return@OnDragListener true
                }

                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = dragEvent.clipData.getItemAt(0)
                    val lbl = item.text.toString()
                    Log.d("BCCCCCCCCCCC", "NOTHING > >  " + lbl)
                        when (lbl.toString()) {
                            "UP" -> view.setImageResource(R.drawable.ic_baseline_arrow_upward_24)
                            "DOWN" -> view.setImageResource(R.drawable.ic_baseline_arrow_downward_24)
                            "BACK" -> view.setImageResource(R.drawable.ic_baseline_arrow_back_24)
                            "FORWARD" -> view.setImageResource(R.drawable.ic_baseline_arrow_forward_24)
                        }

                    // Reset the placeholder when the drag exits or drops
                    view.setBackgroundResource(R.drawable.border)
                    return@OnDragListener true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    return@OnDragListener true
                }
                else -> return@OnDragListener false
            }
        }
        false
    }


    private class ArrowDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {
        private val shadow = view.background
        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            val width: Int = view.width
            val height: Int = view.height
            shadow?.setBounds(0, 0, width, height)
            size.set(width, height)
            touch.set(width / 2, height / 2)
        }
        override fun onDrawShadow(canvas: Canvas) {
            shadow?.draw(canvas)
        }
    }
}