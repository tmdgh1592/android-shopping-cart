package woowacourse.shopping.data.database.dao.cart

import android.annotation.SuppressLint
import android.content.ContentValues
import android.provider.BaseColumns
import android.util.Log
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.database.contract.CartContract
import woowacourse.shopping.data.database.contract.ProductContract
import woowacourse.shopping.data.entity.CartEntity
import woowacourse.shopping.data.model.CartProduct
import woowacourse.shopping.data.model.DataCart
import woowacourse.shopping.data.model.DataCartProduct
import woowacourse.shopping.data.model.DataPage
import woowacourse.shopping.data.model.DataPrice
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.ProductCount
import woowacourse.shopping.util.extension.safeSubList

class CartDaoImpl(private val database: ShoppingDatabase) : CartDao {
    @SuppressLint("Range")
    override fun getAllCartEntity(): List<CartEntity> {
        val db = database.readableDatabase
        val cartEntities = mutableListOf<CartEntity>()
        val cursor = db.rawQuery(GET_ALL_CART_ENTITY_QUERY, null)
        while (cursor.moveToNext()) {
            val cartId: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.CART_ID))
            val productId: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.PRODUCT_ID))
            val count: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.COLUMN_COUNT))
            val isChecked: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.COLUMN_CHECKED))
            cartEntities.add(CartEntity(cartId, productId, count, isChecked))
        }
        cursor.close()
        return cartEntities
    }

    @SuppressLint("Range")
    override fun getCartEntity(productId: Int): CartEntity {
        val db = database.readableDatabase
        val cursor = db.rawQuery(GET_CART_ENTITY_QUERY, arrayOf(productId.toString()))
        val cartEntity = if (cursor.moveToNext()) {
            val cartId: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.CART_ID))
            val count: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.COLUMN_COUNT))
            val isChecked: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.COLUMN_CHECKED))
            CartEntity(cartId, productId, count, isChecked)
        } else {
            CartEntity(0, productId, 0, 0)
        }
        cursor.close()
        return cartEntity
    }

    @SuppressLint("Range")
    override fun getCartEntitiesByPage(page: DataPage): List<CartEntity> {
        val cartEntities = mutableListOf<CartEntity>()

        val db = database.readableDatabase
        val cursor = db.rawQuery(GET_ALL_CART_ENTITY_QUERY, null)

        while (cursor.moveToNext()) {
            val cartId: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.CART_ID))
            val productId: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.PRODUCT_ID))
            val count: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.COLUMN_COUNT))
            val isChecked: Int =
                cursor.getInt(cursor.getColumnIndex(CartContract.COLUMN_CHECKED))
            cartEntities.add(CartEntity(cartId, productId, count, isChecked))
        }
        cursor.close()
        return cartEntities.safeSubList(page.start, page.end + 1)
    }


    override fun insert(product: Product, count: Int) {
        val contentValues = ContentValues().apply {
            put(CartContract.PRODUCT_ID, product.id)
            put(CartContract.COLUMN_CREATED, System.currentTimeMillis())
            put(CartContract.COLUMN_COUNT, count)
        }

        database.writableDatabase.insert(CartContract.TABLE_NAME, null, contentValues)
    }

    override fun getProductInCartSize(): Int {
        val db = database.writableDatabase
        val cursor = db.rawQuery(GET_PRODUCT_IN_CART_SIZE, null)
        cursor.moveToNext()

        val productInCartSize = cursor.getInt(0)
        cursor.close()
        return productInCartSize
    }

    override fun deleteByProductId(id: Int) {
        database.writableDatabase.delete(
            CartContract.TABLE_NAME,
            "${CartContract.PRODUCT_ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun update(cartProduct: DataCartProduct) {
        val contentValues = ContentValues().apply {
            put(CartContract.PRODUCT_ID, cartProduct.product.id)
            put(CartContract.COLUMN_COUNT, cartProduct.selectedCount.value)
            put(CartContract.COLUMN_CHECKED, cartProduct.isChecked)
        }

        database.writableDatabase.update(
            CartContract.TABLE_NAME,
            contentValues,
            "${CartContract.PRODUCT_ID} = ?",
            arrayOf(cartProduct.product.id.toString())
        )
    }

    @SuppressLint("Range")
    override fun addProductCount(product: Product, count: Int) {
        when (val originCount = count(product)) {
            0 -> insert(product, count)
            else -> updateCount(product, originCount + count)
        }
    }

    @SuppressLint("Range")
    override fun minusProductCount(product: Product, count: Int) {
        when (val originCount = count(product)) {
            0 -> return
            else -> updateCount(product, originCount - count)
        }
    }

    override fun updateCount(product: Product, count: Int) {
        val contentValues = ContentValues().apply {
            put(CartContract.PRODUCT_ID, product.id)
            put(CartContract.COLUMN_COUNT, count)
        }

        database.writableDatabase.update(
            CartContract.TABLE_NAME,
            contentValues,
            "${CartContract.PRODUCT_ID} = ?",
            arrayOf(product.id.toString())
        )
    }

    override fun getCheckedProductCount(): Int {
        val db = database.writableDatabase
        val cursor = db.rawQuery(GET_CHECKED_PRODUCT_COUNT, null)
        cursor.moveToNext()

        val checkedProductCount = cursor.getInt(0)
        cursor.close()
        return checkedProductCount
    }

    override fun deleteCheckedProducts() {
        database.writableDatabase.delete(
            CartContract.TABLE_NAME,
            "${CartContract.COLUMN_CHECKED} = ?",
            arrayOf("1")
        )
    }

    override fun contains(product: Product): Boolean {
        val db = database.writableDatabase
        val cursor = db.rawQuery(
            """
            SELECT * FROM ${CartContract.TABLE_NAME}
            WHERE ${CartContract.PRODUCT_ID} = ?
        """.trimIndent(), arrayOf(product.id.toString())
        )

        val result = cursor.count > 0
        cursor.close()
        return result
    }

    @SuppressLint("Range")
    override fun count(product: Product): Int {
        val db = database.writableDatabase
        val cursor = db.rawQuery(
            """
            SELECT * FROM ${CartContract.TABLE_NAME} 
            WHERE ${CartContract.PRODUCT_ID} = ?
        """.trimIndent(), arrayOf(product.id.toString())
        )

        val count = if (cursor.count > 0) {
            cursor.moveToNext()
            val realCount = cursor.getInt(cursor.getColumnIndex(CartContract.COLUMN_COUNT))
            if (realCount == -1) 0 else realCount
        } else {
            0
        }

        cursor.close()
        return count
    }

    companion object {
        private val GET_ALL_CART_ENTITY_QUERY = """
            SELECT * FROM ${CartContract.TABLE_NAME}
        """.trimIndent()

        private val GET_CART_ENTITY_QUERY = """
            SELECT * FROM ${CartContract.TABLE_NAME}
            WHERE ${CartContract.PRODUCT_ID} = ?
        """.trimIndent()

        private val GET_ALL_CART_PRODUCT_QUERY = """
            SELECT * FROM ${ProductContract.TABLE_NAME} as product 
            LEFT JOIN ${CartContract.TABLE_NAME} as cart
            ON cart.${CartContract.PRODUCT_ID} = product.${BaseColumns._ID}
        """.trimIndent()

        private val GET_ALL_CART_PRODUCT_IN_CART_QUERY = """
            SELECT * FROM ${ProductContract.TABLE_NAME} as product
            LEFT JOIN ${CartContract.TABLE_NAME} as cart
            ON cart.${CartContract.PRODUCT_ID} = product.${BaseColumns._ID}
            WHERE ${CartContract.COLUMN_COUNT} > 0
        """.trimIndent()

        private val GET_PRODUCT_IN_CART_SIZE = """
            SELECT SUM(${CartContract.COLUMN_COUNT}) FROM ${CartContract.TABLE_NAME}
            WHERE ${CartContract.COLUMN_COUNT} > 0
        """.trimIndent()

        private val GET_TOTAL_PRICE = """
            SELECT SUM(${ProductContract.COLUMN_PRICE} * ${CartContract.COLUMN_COUNT}) FROM ${ProductContract.TABLE_NAME} as product
            LEFT JOIN ${CartContract.TABLE_NAME} as cart
            ON cart.${CartContract.PRODUCT_ID} = product.${BaseColumns._ID}
            WHERE ${CartContract.COLUMN_COUNT} > 0 AND ${CartContract.COLUMN_CHECKED} = 1
        """.trimIndent()

        private val GET_CHECKED_PRODUCT_COUNT = """
            SELECT COUNT(*) FROM ${CartContract.TABLE_NAME}
            WHERE ${CartContract.COLUMN_COUNT} > 0 AND ${CartContract.COLUMN_CHECKED} = 1
        """.trimIndent()
    }
}
