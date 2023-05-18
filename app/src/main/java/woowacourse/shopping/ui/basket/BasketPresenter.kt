package woowacourse.shopping.ui.basket

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.PageNumber
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toUi
import woowacourse.shopping.model.UiBasketProduct
import woowacourse.shopping.ui.basket.BasketContract.Presenter
import woowacourse.shopping.ui.basket.BasketContract.View

class BasketPresenter(
    view: View,
    private val basketRepository: BasketRepository,
) : Presenter(view) {
    private var basket: Basket = Basket(loadUnit = BASKET_PAGING_SIZE)
    private var currentPage: PageNumber = PageNumber()

    override fun fetchBasket(page: Int) {
        currentPage = currentPage.copy(page)

        val currentBasket = basketRepository.getProductInBasketByPage(currentPage)
        basket = currentBasket

        view.updateBasket(basket.takeItemsUpTo(currentPage).map { it.toUi() })
        view.updateNavigatorEnabled(currentPage.hasPrevious(), basket.canLoadMore(currentPage))
        view.updatePageNumber(currentPage.toUi())
    }

    override fun deleteBasketProduct(basketProduct: UiBasketProduct) {
        basketRepository.minusProductCount(basketProduct.product.toDomain())
        fetchBasket(currentPage.value)
    }

    override fun closeScreen() {
        view.closeScreen()
    }

    companion object {
        private const val BASKET_PAGING_SIZE = 5
    }
}
