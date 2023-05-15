package woowacourse.shopping.domain

typealias DomainProducts = Products

data class Products(
    private val items: List<Product> = emptyList(),
    private val loadUnit: Int = DEFAULT_LOAD_AT_ONCE,
) {
    val lastId: Int
        get() = items.maxOfOrNull { it.id } ?: -1

    fun addAll(newItems: List<Product>): Products = copy(items = items + newItems)

    fun add(newItem: Product): Products = copy(items = items + newItem)

    fun canLoadMore(): Boolean =
        items.size >= loadUnit && (items.size % loadUnit >= 1 || loadUnit == 1 && items.size > loadUnit)

    fun getItems(): List<Product> = items.toList()

    fun getItemsByUnit(): List<Product> = items.take(
        (items.size / loadUnit).coerceAtLeast(1) * loadUnit
    )

    operator fun plus(item: Product): Products = add(item)

    companion object {
        private const val DEFAULT_LOAD_AT_ONCE = 20
    }
}
