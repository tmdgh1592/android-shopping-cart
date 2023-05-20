package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.PageNumber
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.RecentProducts
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.domain.repository.DomainProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toUi
import woowacourse.shopping.model.UiProduct
import woowacourse.shopping.model.UiProductCount
import woowacourse.shopping.model.UiRecentProduct
import woowacourse.shopping.ui.shopping.ShoppingContract.Presenter
import woowacourse.shopping.ui.shopping.ShoppingContract.View

class ShoppingPresenter(
    view: View,
    private val productRepository: DomainProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val basketRepository: BasketRepository,
) : Presenter(view) {
    private var basket = Basket(loadUnit = LOAD_PRODUCT_SIZE_AT_ONCE)
    private var recentProducts = RecentProducts()
    private var currentPage: PageNumber = PageNumber(sizePerPage = TOTAL_LOAD_PRODUCT_SIZE_AT_ONCE)
    private val productInBasketSize: UiProductCount
        get() = UiProductCount(basketRepository.getProductInBasketSize())

    override fun fetchAll() {
        fetchProducts()
        fetchRecentProducts()
    }

    override fun refreshProduct() {
        val refreshedProducts =
            basketRepository.getProductInRange(currentPage.getStartPage(), currentPage)
        basket = basket.update(refreshedProducts)
        updateBasketView()
    }

    override fun fetchProducts() {
        basket += basketRepository.getProductByPage(currentPage)
        updateBasketView()
        view.updateLoadMoreVisible()
    }

    override fun loadMore() {
        currentPage = currentPage.next()
        basket += basketRepository.getProductByPage(currentPage)
        updateBasketView()
        view.updateLoadMoreVisible()
    }

    override fun fetchRecentProducts() {
        recentProducts = RecentProducts(recentProductRepository.getPartially(RECENT_PRODUCT_SIZE))
        view.updateRecentProducts(recentProducts.getItems().map { it.toUi() })
    }

    private fun View.updateLoadMoreVisible() {
        if (basket.canLoadMore(currentPage)) {
            showLoadMoreButton()
        } else {
            hideLoadMoreButton()
        }
    }

    override fun inquiryProductDetail(product: UiProduct) {
        val recentProduct = RecentProduct(product = product.toDomain())
        view.navigateToProductDetail(product, recentProducts.getLatest()?.toUi())
        updateRecentProducts(recentProduct)
    }

    private fun updateRecentProducts(recentProduct: RecentProduct) {
        recentProducts += recentProduct
        recentProductRepository.add(recentProduct)
        view.updateRecentProducts(recentProducts.getItems().map { it.toUi() })
    }

    override fun inquiryRecentProductDetail(recentProduct: UiRecentProduct) {
        view.navigateToProductDetail(recentProduct.product, recentProducts.getLatest()?.toUi())
        recentProductRepository.add(recentProduct.toDomain())
    }

    override fun openBasket() {
        view.navigateToBasket()
    }

    override fun addBasketProduct(product: UiProduct, count: Int) {
        val newProduct = product.toDomain()
        basket = basket.add(newProduct, count)
        basketRepository.addProductCount(newProduct, count)

        updateBasketView()
    }

    override fun removeBasketProduct(product: UiProduct) {
        val removingProduct = product.toDomain()
        basket -= removingProduct
        basketRepository.minusProductCount(removingProduct)

        updateBasketView()
    }

    private fun updateBasketView() {
        view.updateBasketProductBadge(productInBasketSize)
        view.updateProducts(basket.takeItemsUpTo(currentPage).map { it.toUi() })
    }

    companion object {
        private const val RECENT_PRODUCT_SIZE = 10
        private const val LOAD_PRODUCT_SIZE_AT_ONCE = 20
        private const val PRODUCT_SIZE_FOR_HAS_NEXT = 1
        private const val TOTAL_LOAD_PRODUCT_SIZE_AT_ONCE =
            LOAD_PRODUCT_SIZE_AT_ONCE + PRODUCT_SIZE_FOR_HAS_NEXT
    }
}
