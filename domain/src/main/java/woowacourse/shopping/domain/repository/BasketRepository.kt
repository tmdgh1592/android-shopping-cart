package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.PageNumber
import woowacourse.shopping.domain.Product

typealias DomainBasketRepository = BasketRepository

interface BasketRepository {
    fun getProductByPage(page: PageNumber): Basket
    fun getProductInBasketByPage(page: PageNumber): Basket
    fun addProductCount(product: Product, count: Int)
    fun minusProductCount(product: Product)
    fun deleteByProductId(productId: Int)
    fun getProductInBasketSize(): Int
}
