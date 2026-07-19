package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class TruBiteRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val profileDao = db.profileDao()
    private val productDao = db.productDao()
    private val cartDao = db.cartDao()

    val allProfiles: Flow<List<ProfileEntity>> = profileDao.getAllProfiles()
    val scanHistory: Flow<List<ProductEntity>> = productDao.getHistory()
    val favoritesList: Flow<List<ProductEntity>> = productDao.getFavorites()
    val cartItems: Flow<List<CartItemEntity>> = cartDao.getCartItems()

    suspend fun getActiveProfile(): ProfileEntity? = profileDao.getActiveProfile()

    suspend fun insertProfile(profile: ProfileEntity) = profileDao.insertProfile(profile)

    suspend fun updateProfile(profile: ProfileEntity) = profileDao.updateProfile(profile)

    suspend fun deleteProfile(id: Int) = profileDao.deleteProfile(id)

    suspend fun setActiveProfile(id: Int) = profileDao.setActiveProfile(id)

    suspend fun getProduct(barcode: String): ProductEntity? = productDao.getProductByBarcode(barcode)

    suspend fun insertProduct(product: ProductEntity) = productDao.insertProduct(product)

    suspend fun deleteProduct(barcode: String) = productDao.deleteProductByBarcode(barcode)

    suspend fun toggleFavorite(barcode: String) {
        val existing = productDao.getProductByBarcode(barcode)
        if (existing != null) {
            val updated = existing.copy(isFavorite = !existing.isFavorite)
            productDao.updateProduct(updated)
        }
    }

    suspend fun clearHistory() = productDao.clearHistory()

    // Cart operations
    suspend fun addToCart(barcode: String, quantity: Int = 1) {
        cartDao.insertCartItem(CartItemEntity(barcode, quantity))
    }

    suspend fun removeFromCart(barcode: String) {
        cartDao.deleteCartItem(barcode)
    }

    suspend fun clearCart() = cartDao.clearCart()

    /**
     * Analyze and save product scans
     */
    suspend fun scanProduct(
        barcode: String,
        name: String,
        brand: String,
        category: String,
        ingredients: String,
        allergens: String
    ): ProductEntity {
        val activeProfile = getActiveProfile()

        // 1. Estimate baseline nutrients based on product properties
        val calories: Double
        val protein: Double
        val carbs: Double
        val fat: Double
        val sugar: Double
        val fiber: Double
        val sodium: Double
        val baseScore: Int
        val servingSize: String
        val procLevel: String

        val lowerName = name.lowercase()
        val lowerIngredients = ingredients.lowercase()

        when {
            lowerName.contains("chocolate") || lowerName.contains("cookie") || lowerName.contains("candy") || lowerIngredients.contains("cocoa") -> {
                calories = 450.0 + Random.nextInt(0, 80)
                protein = 4.0 + Random.nextDouble() * 3
                carbs = 60.0 + Random.nextDouble() * 10
                fat = 25.0 + Random.nextDouble() * 5
                sugar = 45.0 + Random.nextDouble() * 12
                fiber = 2.0
                sodium = 0.15
                baseScore = 24 + Random.nextInt(0, 15) // Poor
                servingSize = "40g"
                procLevel = "Ultra-Processed"
            }
            lowerName.contains("oat") || lowerName.contains("granola") || lowerName.contains("cereal") -> {
                calories = 360.0 + Random.nextInt(0, 40)
                protein = 10.0 + Random.nextDouble() * 4
                carbs = 68.0 + Random.nextDouble() * 5
                fat = 6.0 + Random.nextDouble() * 2
                sugar = 8.0 + Random.nextDouble() * 5
                fiber = 9.0 + Random.nextDouble() * 3
                sodium = 0.05
                baseScore = 78 + Random.nextInt(0, 12) // Good
                servingSize = "50g"
                procLevel = "Minimally Processed"
            }
            lowerName.contains("soda") || lowerName.contains("coke") || lowerName.contains("drink") -> {
                calories = if (lowerName.contains("diet") || lowerName.contains("zero")) 0.0 else 140.0
                protein = 0.0
                carbs = if (calories == 0.0) 0.0 else 39.0
                fat = 0.0
                sugar = if (calories == 0.0) 0.0 else 39.0
                fiber = 0.0
                sodium = 0.04
                baseScore = if (calories == 0.0) 42 else 12 // Moderate / Poor
                servingSize = "355ml"
                procLevel = "Ultra-Processed"
            }
            lowerName.contains("yogurt") || lowerName.contains("curd") -> {
                calories = 120.0
                protein = 6.5
                carbs = 12.0
                fat = 3.2
                sugar = 9.0
                fiber = 0.0
                sodium = 0.08
                baseScore = 82
                servingSize = "150g"
                procLevel = "Moderately Processed"
            }
            lowerName.contains("noodle") || lowerName.contains("ramen") || lowerName.contains("pasta") -> {
                calories = 380.0
                protein = 9.0
                carbs = 54.0
                fat = 14.0
                sugar = 2.0
                fiber = 2.5
                sodium = 0.98 // high sodium
                baseScore = 35
                servingSize = "85g"
                procLevel = "Ultra-Processed"
            }
            else -> {
                // Default healthy snack defaults
                calories = 180.0
                protein = 5.0
                carbs = 20.0
                fat = 8.0
                sugar = 5.0
                fiber = 3.0
                sodium = 0.12
                baseScore = 65
                servingSize = "50g"
                procLevel = "Moderately Processed"
            }
        }

        // Apply personalizations for Score deductions
        var finalScore = baseScore
        val activeConditions = activeProfile?.medicalConditions?.split(",")?.map { it.trim().lowercase() } ?: emptyList()
        val activeAllergies = activeProfile?.allergies?.split(",")?.map { it.trim().lowercase() } ?: emptyList()

        // Allergy deduction
        for (allergy in activeAllergies) {
            if (allergy.isNotEmpty() && (lowerIngredients.contains(allergy) || lowerName.contains(allergy))) {
                finalScore -= 35 // Extreme deduction
            }
        }

        // Diabetic sugar deduction
        if (activeConditions.contains("diabetes") || activeConditions.contains("diabetic")) {
            if (sugar > 10.0) finalScore -= 25
        }

        if (finalScore < 0) finalScore = 0

        // Create warnings string
        val warningsBuilder = StringBuilder()
        for (allergy in activeAllergies) {
            if (allergy.isNotEmpty() && (lowerIngredients.contains(allergy) || lowerName.contains(allergy))) {
                warningsBuilder.append("Contains allergen: ${allergy.replaceFirstChar { it.uppercase() }}. ")
            }
        }
        if (sugar > 15.0 && (activeConditions.contains("diabetes") || activeConditions.contains("diabetic"))) {
            warningsBuilder.append("HIGH GLYCAEMIC RISK! Extreme sugar for diabetic condition.")
        }

        // Get AI Explanation
        val aiExp = GeminiService.generateNutritionFeedback(name, brand, ingredients, activeProfile)

        val sideEffects = if (sugar > 20.0) {
            "Rapid insulin spikes, dental erosion, hyper-activity followed by lethargic crashes."
        } else if (sodium > 0.6) {
            "Increases systemic blood pressure, raises cardiovascular workload, and causes localized water retention."
        } else {
            "None expected when consumed in suggested moderate diet amounts."
        }

        val dailyLimit = if (calories > 300.0) "60g" else "150g"

        // Healthier alternatives names/barcodes matching current category
        val alternatives = when (category) {
            "Snacks", "Sweets" -> "TruBite Organic Raw Almonds, OatPure Whole Grain Granola"
            "Beverages" -> "Coconut Water organic, Matcha Green Tea unsweetened"
            "Dairy" -> "A2 Organic Plain Greek Yogurt, Organic Almond Milk"
            else -> "Organic Whole Chia Seeds, Harvest Fresh Mixed Greens"
        }

        val product = ProductEntity(
            barcode = barcode,
            name = name,
            brand = brand,
            category = category,
            truScore = finalScore,
            calories = calories,
            servingSize = servingSize,
            protein = protein,
            carbs = carbs,
            fat = fat,
            sugar = sugar,
            fiber = fiber,
            sodium = sodium,
            ingredients = ingredients,
            allergens = allergens,
            warnings = warningsBuilder.toString(),
            processingLevel = procLevel,
            aiExplanation = aiExp,
            sideEffects = sideEffects,
            dailyRecommendedQty = dailyLimit,
            alternatives = alternatives,
            timestamp = System.currentTimeMillis()
        )

        // Save in room history
        productDao.insertProduct(product)
        return product
    }

    /**
     * Pre-populate database with realistic default profiles & history
     */
    suspend fun populateDemoData() {
        val currentProfiles = allProfiles.firstOrNull() ?: emptyList()
        if (currentProfiles.isEmpty()) {
            val selfProfile = ProfileEntity(
                name = "Aria",
                relationship = "Self",
                age = 29,
                weight = 68.0,
                height = 172.0,
                gender = "Female",
                goals = "Stay energetic, Build endurance",
                allergies = "Nuts",
                diet = "Whole foods, balanced",
                medicalConditions = "None",
                isActive = true
            )
            val dadProfile = ProfileEntity(
                name = "Ravi (Dad)",
                relationship = "Parent",
                age = 62,
                weight = 81.0,
                height = 168.0,
                gender = "Male",
                goals = "Manage blood glucose, Hearth health",
                allergies = "None",
                diet = "Low Glycemic",
                medicalConditions = "Diabetes, Hypertension",
                isActive = false
            )
            val babyProfile = ProfileEntity(
                name = "Kian (Son)",
                relationship = "Child",
                age = 4,
                weight = 16.0,
                height = 104.0,
                gender = "Male",
                goals = "Growth, Clean organic ingredients",
                allergies = "Dairy",
                diet = "Dairy-free nutrition",
                medicalConditions = "None",
                isActive = false
            )

            profileDao.insertProfile(selfProfile)
            profileDao.insertProfile(dadProfile)
            profileDao.insertProfile(babyProfile)

            // Let's insert a couple of demo products scanned to show right away in the History!
            scanProduct(
                barcode = "8901234567890",
                name = "ChocoBlox Sugar Bars",
                brand = "SweetFoods",
                category = "Sweets",
                ingredients = "Sugar, Cocoa butter, Milk solids, Palm oil, Emulsifiers, Soy lecithin, Artificial vanillin flavoring, Sodium benzoate, Red Dye 40",
                allergens = "Milk, Soy"
            )
            scanProduct(
                barcode = "8901234567891",
                name = "OatPure Whole Grain Oats",
                brand = "Harvest Organics",
                category = "Snacks",
                ingredients = "100% Organic Rolled Oats, Organic Flax Seeds, Organic Chia Seeds",
                allergens = "Gluten free"
            )
        }
    }
}
