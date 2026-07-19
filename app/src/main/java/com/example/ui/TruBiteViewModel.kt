package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TruBiteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TruBiteRepository(application)

    // Repository flows
    val allProfiles: StateFlow<List<ProfileEntity>> = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scanHistory: StateFlow<List<ProductEntity>> = repository.scanHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritesList: StateFlow<List<ProductEntity>> = repository.favoritesList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Profile State
    private val _activeProfile = MutableStateFlow<ProfileEntity?>(null)
    val activeProfile: StateFlow<ProfileEntity?> = _activeProfile.asStateFlow()

    // Scanner States
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scannedProduct = MutableStateFlow<ProductEntity?>(null)
    val scannedProduct: StateFlow<ProductEntity?> = _scannedProduct.asStateFlow()

    private val _scannerError = MutableStateFlow<String?>(null)
    val scannerError: StateFlow<String?> = _scannerError.asStateFlow()

    // Search States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ProductEntity>>(emptyList())
    val searchResults: StateFlow<List<ProductEntity>> = _searchResults.asStateFlow()

    private val _isListeningVoice = MutableStateFlow(false)
    val isListeningVoice: StateFlow<Boolean> = _isListeningVoice.asStateFlow()

    // Chat Assistant States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(text = "Hello! I am your personalized TruBite Health Twin coach. Ask me anything, like \"Can diabetics eat high-sugar cereal?\" or \"What are clean snack alternatives?\"", isUser = false)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isGeneratingChat = MutableStateFlow(false)
    val isGeneratingChat: StateFlow<Boolean> = _isGeneratingChat.asStateFlow()

    // Onboarding and Session Status
    val hasCompletedOnboarding = MutableStateFlow(false)
    val isLoggedIn = MutableStateFlow(true) // Start authenticated for demo seamlessness

    init {
        viewModelScope.launch {
            // Populate demo data
            repository.populateDemoData()
            // Observe and update active profile
            allProfiles.collect { profiles ->
                val active = profiles.find { it.isActive }
                _activeProfile.value = active
            }
        }
    }

    // Toggle active profile (Health Twin switching)
    fun switchActiveProfile(id: Int) {
        viewModelScope.launch {
            repository.setActiveProfile(id)
        }
    }

    // Create a new family profile member
    fun createFamilyProfile(
        name: String,
        relationship: String,
        age: Int,
        weight: Double,
        height: Double,
        gender: String,
        goals: String,
        allergies: String,
        diet: String,
        medicalConditions: String
    ) {
        viewModelScope.launch {
            val newProfile = ProfileEntity(
                name = name,
                relationship = relationship,
                age = age,
                weight = weight,
                height = height,
                gender = gender,
                goals = goals,
                allergies = allergies,
                diet = diet,
                medicalConditions = medicalConditions,
                isActive = false
            )
            repository.insertProfile(newProfile)
        }
    }

    // Trigger barcode scan simulation or input
    fun handleBarcodeScan(barcode: String) {
        viewModelScope.launch {
            _isScanning.value = true
            _scannerError.value = null
            _scannedProduct.value = null

            try {
                // Match barcodes or construct a simulated healthy/unhealthy product based on input digits
                val product = when (barcode) {
                    "8901234567890" -> repository.scanProduct(
                        barcode = barcode,
                        name = "ChocoBlox Sugar Bars",
                        brand = "SweetFoods",
                        category = "Sweets",
                        ingredients = "Sugar, Cocoa butter, Milk solids, Palm oil, Emulsifiers, Soy lecithin, Artificial vanillin flavoring, Sodium benzoate, Red Dye 40",
                        allergens = "Milk, Soy"
                    )
                    "8901234567891" -> repository.scanProduct(
                        barcode = barcode,
                        name = "OatPure Whole Grain Oats",
                        brand = "Harvest Organics",
                        category = "Snacks",
                        ingredients = "100% Organic Rolled Oats, Organic Flax Seeds, Organic Chia Seeds",
                        allergens = "Gluten free"
                    )
                    "8901234567892" -> repository.scanProduct(
                        barcode = barcode,
                        name = "Diet Zero Cola",
                        brand = "FizzyCo",
                        category = "Beverages",
                        ingredients = "Carbonated Water, Caramel Color, Phosphoric Acid, Aspartame, Potassium Benzoate, Natural Flavors, Acesulfame Potassium, Caffeine",
                        allergens = "Phenylalanine"
                    )
                    "8901234567893" -> repository.scanProduct(
                        barcode = barcode,
                        name = "A2 Plain Greek Yogurt",
                        brand = "Meadow Farms",
                        category = "Dairy",
                        ingredients = "Pasteurized Grade A Organic Milk, Active Live Cultures (L. Bulgaricus, S. Thermophilus)",
                        allergens = "Milk"
                    )
                    "8901234567894" -> repository.scanProduct(
                        barcode = barcode,
                        name = "Instant Ramen noodles",
                        brand = "QuickMeals",
                        category = "Snacks",
                        ingredients = "Enriched Wheat Flour, Palm Oil, Sodium Tripolyphosphate, Monosodium Glutamate, Dehydrated Soy Sauce, Artificial Beef Flavor, Xanthan Gum",
                        allergens = "Wheat, Soy"
                    )
                    else -> {
                        // Generate a randomized generic barcode product dynamically!
                        val names = listOf("Organic Fruit Strips", "Caramel Popcorn", "Almond Butter unsweetened", "Sparkling Citrus Soda")
                        val brands = listOf("PureNaturals", "SnackMasters", "GreenValley", "WaveDrinks")
                        val categories = listOf("Snacks", "Sweets", "Dairy", "Beverages")
                        val selectedName = names[barcode.hashCode().coerceAtLeast(0) % names.size]
                        val selectedBrand = brands[barcode.hashCode().coerceAtLeast(0) % brands.size]
                        val selectedCategory = categories[barcode.hashCode().coerceAtLeast(0) % categories.size]
                        val ingredients = if (selectedName.contains("Organic") || selectedName.contains("Almond")) {
                            "Organic Raw Almonds, Organic Sunflower Oil, Organic Sea Salt"
                        } else {
                            "High Fructose Corn Syrup, Sugar, Hydrogenated Palm Kernel Oil, Citric Acid, Artificial Colors Red 40, Yellow 5, Titanium Dioxide, Preservative BHT"
                        }

                        repository.scanProduct(
                            barcode = barcode,
                            name = selectedName,
                            brand = selectedBrand,
                            category = selectedCategory,
                            ingredients = ingredients,
                            allergens = if (selectedName.contains("Almond")) "Nuts" else "None"
                        )
                    }
                }
                _scannedProduct.value = product
            } catch (e: Exception) {
                _scannerError.value = "Scanning error: ${e.message}"
            } finally {
                _isScanning.value = false
            }
        }
    }

    // Set search query and filter products
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.trim().isEmpty()) {
                _searchResults.value = emptyList()
            } else {
                // Filter inside local cache (scanned products or demo catalog)
                val allDemoProducts = listOf(
                    "8901234567890" to Pair("ChocoBlox Sugar Bars", "SweetFoods"),
                    "8901234567891" to Pair("OatPure Whole Grain Oats", "Harvest Organics"),
                    "8901234567892" to Pair("Diet Zero Cola", "FizzyCo"),
                    "8901234567893" to Pair("A2 Plain Greek Yogurt", "Meadow Farms"),
                    "8901234567894" to Pair("Instant Ramen noodles", "QuickMeals")
                )

                val filtered = allDemoProducts.filter { (_, item) ->
                    item.first.contains(query, ignoreCase = true) || item.second.contains(query, ignoreCase = true)
                }.map { (barcode, item) ->
                    // Get cached or simulate immediately
                    val cached = repository.getProduct(barcode)
                    cached ?: repository.scanProduct(
                        barcode = barcode,
                        name = item.first,
                        brand = item.second,
                        category = if (barcode == "8901234567892") "Beverages" else "Snacks",
                        ingredients = "Filtered organic elements, trace mineral compounds",
                        allergens = "None"
                    )
                }
                _searchResults.value = filtered
            }
        }
    }

    // Simulate voice search trigger
    fun toggleVoiceListening() {
        if (_isListeningVoice.value) {
            _isListeningVoice.value = false
        } else {
            _isListeningVoice.value = true
            viewModelScope.launch {
                // Mock a quick voice input after 1.5 seconds
                kotlinx.coroutines.delay(1500)
                if (_isListeningVoice.value) {
                    _isListeningVoice.value = false
                    setSearchQuery("Oats")
                }
            }
        }
    }

    // Send a message to the AI Nutrition Twin
    fun sendChatMessage(text: String) {
        if (text.trim().isEmpty()) return
        val userMsg = ChatMessage(text = text, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg
        _isGeneratingChat.value = true

        viewModelScope.launch {
            // Context summary from history for personalized context
            val history = scanHistory.value.take(3).joinToString("; ") { "${it.name} (${it.truScore} score)" }
            val replyText = GeminiService.askAssistant(text, activeProfile.value, history)
            
            _chatMessages.value = _chatMessages.value + ChatMessage(text = replyText, isUser = false)
            _isGeneratingChat.value = false
        }
    }

    // Cart Optimizer actions
    fun addToCart(barcode: String) {
        viewModelScope.launch {
            repository.addToCart(barcode)
        }
    }

    fun removeFromCart(barcode: String) {
        viewModelScope.launch {
            repository.removeFromCart(barcode)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Toggle favorite
    fun toggleFavorite(barcode: String) {
        viewModelScope.launch {
            repository.toggleFavorite(barcode)
            // Refresh scanned detail view if it's currently active
            val current = _scannedProduct.value
            if (current != null && current.barcode == barcode) {
                _scannedProduct.value = repository.getProduct(barcode)
            }
        }
    }
}
