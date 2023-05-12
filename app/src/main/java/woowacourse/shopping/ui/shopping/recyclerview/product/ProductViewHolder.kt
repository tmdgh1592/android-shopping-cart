package woowacourse.shopping.ui.shopping.recyclerview.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.model.UiProduct
import woowacourse.shopping.util.setOnSingleClickListener

class ProductViewHolder(parent: ViewGroup, onItemClick: (Int) -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
    ) {
    private val binding = ItemProductBinding.bind(itemView)

    init {
        binding.root.setOnSingleClickListener { onItemClick(bindingAdapterPosition) }
    }

    fun bind(product: UiProduct) {
        binding.product = product
    }
}
