package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.basket.BasketDataSource
import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.PageNumber
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.DomainBasketRepository

class BasketRepositoryImpl(private val localBasketDataSource: BasketDataSource.Local) :
    DomainBasketRepository {
    override fun getProductByPage(page: PageNumber): Basket =
        localBasketDataSource.getProductByPage(page.toData()).toDomain(page.sizePerPage)

    override fun getProductInBasketByPage(page: PageNumber): Basket =
        localBasketDataSource.getProductInBasketByPage(page.toData()).toDomain(page.sizePerPage)

    override fun getProductInRange(startPage: PageNumber, endPage: PageNumber): Basket {
        val start = startPage.toData()
        val end = endPage.toData()
        return localBasketDataSource.getProductInRange(start, end).toDomain(startPage.sizePerPage)
    }

    override fun increaseCartCount(product: Product, count: Int) {
        localBasketDataSource.increaseCartCount(product.toData(), count)
    }

    override fun update(basket: Basket) {
        localBasketDataSource.update(basket.toData())
    }

    override fun getTotalPrice(): Int =
        localBasketDataSource.getTotalPrice()

    override fun getCheckedProductCount(): Int =
        localBasketDataSource.getCheckedProductCount()

    override fun removeCheckedProducts() {
        localBasketDataSource.removeCheckedProducts()
    }

    override fun decreaseCartCount(product: Product, count: Int) {
        localBasketDataSource.decreaseCartCount(product.toData(), count)
    }

    override fun deleteByProductId(productId: Int) {
        localBasketDataSource.deleteByProductId(productId)
    }

    override fun getProductInBasketSize(): Int =
        localBasketDataSource.getProductInBasketSize()
}
