package woowacourse.shopping.data.database.contract

import android.provider.BaseColumns

object BasketContract {
    internal const val TABLE_NAME = "BASKET_TABLE"
    internal const val COLUMN_NAME = "name"
    internal const val COLUMN_PRICE = "price"
    internal const val COLUMN_IMAGE_URL = "image_url"
    internal const val COLUMN_CREATED = "created"

    internal val CREATE_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS $TABLE_NAME (
            ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT,
            $COLUMN_PRICE INTEGER,
            $COLUMN_IMAGE_URL TEXT,
            $COLUMN_CREATED LONG
        )
    """.trimIndent()

    internal val DELETE_TABLE_QUERY = """
        DROP TABLE IF EXISTS $TABLE_NAME
    """.trimIndent()
}
