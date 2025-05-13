package ae.oleapp.employee.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

fun toggleSwitch(isToggled: Boolean = false, switchLayout : ConstraintLayout, switchToggleCircle : ImageView) {
    val rotationAngle = if (isToggled) 360f else -360f
    val paddingHorizontal = switchLayout.paddingStart + switchLayout.paddingEnd
    val endPosition = if (isToggled) {
        switchLayout.width - switchToggleCircle.width - paddingHorizontal
    } else {
        0
    }

    val moveAnimation = ObjectAnimator.ofFloat(switchToggleCircle, "translationX", endPosition.toFloat())
    val rotateAnimation = ObjectAnimator.ofFloat(switchToggleCircle, "rotation", 0f, rotationAngle)

    AnimatorSet().apply {
        playTogether(moveAnimation, rotateAnimation)
        duration = 400
        interpolator = AccelerateDecelerateInterpolator()
        start()
    }
}