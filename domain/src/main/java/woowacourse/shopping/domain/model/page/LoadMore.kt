package woowacourse.shopping.domain.model.page

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.util.safeSubList

typealias DomainLoadMore = LoadMore

class LoadMore(
    value: Int = 1,
    sizePerPage: Int = 20,
) : Page(value, sizePerPage) {
    override fun getStartPage(): Page = LoadMore(1, sizePerPage)

    override fun hasPrevious(): Boolean = value > 1

    override fun hasNext(cart: Cart): Boolean = cart.items.size >= value * sizePerPage

    override fun next(): Page = LoadMore(value + 1, sizePerPage)

    override fun update(value: Int): Page = LoadMore(value, sizePerPage)

    override fun takeItems(cart: Cart): List<CartProduct> =
        cart.items.take(value * sizePerPage)

    override fun getCheckedProductSize(cart: Cart): Int = cart.items
        .safeSubList(0, value * sizePerPage)
        .count { item -> item.isChecked }
}
