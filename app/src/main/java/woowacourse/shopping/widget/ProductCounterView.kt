package woowacourse.shopping.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ViewCounterBinding
import woowacourse.shopping.model.UiProduct
import kotlin.properties.Delegates

class ProductCounterView : ConstraintLayout {
    private val binding by lazy {
        ViewCounterBinding.inflate(LayoutInflater.from(context), this, true)
    }
    var count: Int by Delegates.observable(INITIAL_COUNT) { _, _, newCount ->
        binding.countTextView.text = newCount.toString()
    }
    private var minCount: Int = DEFAULT_MIN_COUNT
    private var maxCount: Int = DEFAULT_MAX_COUNT

    init {
        binding.count = count
        binding.counterPlusButton.setOnClickListener { plusCount() }
        binding.counterMinusButton.setOnClickListener { minusCount() }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initTypedArrayValue(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initTypedArrayValue(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initTypedArrayValue(attrs)
    }

    private fun initTypedArrayValue(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.ProductCounterView).use {
            count = it.getInt(R.styleable.ProductCounterView_count, INITIAL_COUNT)
            minCount = it.getInt(R.styleable.ProductCounterView_min_count, DEFAULT_MIN_COUNT)
            maxCount = it.getInt(R.styleable.ProductCounterView_max_count, DEFAULT_MAX_COUNT)
        }
    }

    fun setOnPlusClickListener(onPlusClick: (view: ProductCounterView, newCount: Int) -> Unit) {
        binding.counterPlusButton.setOnClickListener {
            plusCount()
            onPlusClick(this, count)
        }
    }

    fun setOnMinusClickListener(onMinusClick: (view: ProductCounterView, newCount: Int) -> Unit) {
        binding.counterMinusButton.setOnClickListener {
            minusCount()
            onMinusClick(this, count)
        }
    }

    private fun plusCount() {
        if (count < maxCount) ++count
    }

    private fun minusCount() {
        if (count > minCount) --count
    }

    companion object {
        private const val INITIAL_COUNT: Int = 1
        private const val DEFAULT_MIN_COUNT = 1
        private const val DEFAULT_MAX_COUNT = 99
    }

    interface OnClickListener {
        fun onClickCounterPlus(product: UiProduct)
        fun onClickCounterMinus(product: UiProduct)
    }
}
