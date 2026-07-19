package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val relationship: String, // Self, Child, Parent, Spouse, etc.
    val age: Int,
    val weight: Double, // in kg
    val height: Double, // in cm
    val gender: String, // Male, Female, Other
    val goals: String, // e.g. "Healthy Eating", "Weight Loss", "Manage Sugar"
    val allergies: String, // Comma separated e.g. "Nuts, Dairy, Gluten"
    val diet: String, // Vegan, Vegetarian, Keto, Paleo, All
    val medicalConditions: String, // e.g. "Diabetes", "Pregnancy", "Hypertension"
    val isActive: Boolean = false
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val barcode: String,
    val name: String,
    val brand: String,
    val category: String,
    val truScore: Int, // 0 - 100
    val calories: Double, // kcal per serving
    val servingSize: String, // e.g. "100g", "250ml"
    val protein: Double, // grams
    val carbs: Double, // grams
    val fat: Double, // grams
    val sugar: Double, // grams
    val fiber: Double, // grams
    val sodium: Double, // grams
    val ingredients: String, // Comma-separated ingredients list
    val allergens: String, // Comma-separated allergens list
    val warnings: String, // Health/Medical warnings if any
    val processingLevel: String, // Minimally Processed, Ultra-Processed, Moderately Processed
    val aiExplanation: String, // Personalised AI explanation based on active profile
    val sideEffects: String, // Potential side effects or issues
    val dailyRecommendedQty: String, // Recommended daily maximum e.g. "50g"
    val alternatives: String, // Comma-separated healthier alternative barcodes/names
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val barcode: String,
    val quantity: Int = 1
)

// App state models for chat assistant
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
