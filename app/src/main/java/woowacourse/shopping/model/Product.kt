package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiProduct = Product

@Parcelize
data class Product(
    val id: Int,
    val name: String,
    val price: UiPrice,
    val imageUrl: String,
) : Parcelable
