package ae.oleapp.employee.utils

import ae.oleapp.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CustomRoundedImageView : AppCompatImageView {
    private var topLeftRadius = 0f
    private var topRightRadius = 0f
    private var bottomLeftRadius = 0f
    private var bottomRightRadius = 0f

    private val path = Path()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initializeAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initializeAttributes(attrs)
    }

    private fun initializeAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedCornerImageView)

        try {
            topLeftRadius =
                typedArray.getDimension(R.styleable.RoundedCornerImageView_topLeftRadius, 0f)
            topRightRadius =
                typedArray.getDimension(R.styleable.RoundedCornerImageView_topRightRadius, 0f)
            bottomLeftRadius =
                typedArray.getDimension(R.styleable.RoundedCornerImageView_bottomLeftRadius, 0f)
            bottomRightRadius =
                typedArray.getDimension(R.styleable.RoundedCornerImageView_bottomRightRadius, 0f)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        path.reset()
        path.addRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            floatArrayOf(
                topLeftRadius,
                topLeftRadius,
                topRightRadius,
                topRightRadius,
                bottomRightRadius,
                bottomRightRadius,
                bottomLeftRadius,
                bottomLeftRadius
            ),
            Path.Direction.CW
        )

        canvas.clipPath(path)
        super.onDraw(canvas)
    }
}