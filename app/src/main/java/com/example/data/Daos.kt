package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM family_profiles")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM family_profiles WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveProfile(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Query("UPDATE family_profiles SET isActive = 0")
    suspend fun deactivateAll()

    @Transaction
    suspend fun setActiveProfile(id: Int) {
        deactivateAll()
        deactivateAllActiveQuery(id)
    }

    @Query("UPDATE family_profiles SET isActive = 1 WHERE id = :id")
    suspend fun deactivateAllActiveQuery(id: Int)

    @Query("DELETE FROM family_profiles WHERE id = :id")
    suspend fun deleteProfile(id: Int)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE barcode = :barcode")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE barcode = :barcode")
    suspend fun deleteProductByBarcode(barcode: String)

    @Query("DELETE FROM products")
    suspend fun clearHistory()
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE barcode = :barcode")
    suspend fun deleteCartItem(barcode: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
