package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Call the Gemini API to get nutrition feedback.
     */
    suspend fun generateNutritionFeedback(
        productName: String,
        brand: String,
        ingredients: String,
        activeProfile: ProfileEntity?
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not configured. Falling back to local AI rule engine.")
            return@withContext getLocalAiFeedback(productName, brand, ingredients, activeProfile)
        }

        val profileStr = if (activeProfile != null) {
            """
            Active Health Profile:
            - Name: ${activeProfile.name} (${activeProfile.relationship})
            - Age: ${activeProfile.age}, Gender: ${activeProfile.gender}
            - Height: ${activeProfile.height} cm, Weight: ${activeProfile.weight} kg
            - Goals: ${activeProfile.goals}
            - Allergies: ${activeProfile.allergies}
            - Diet: ${activeProfile.diet}
            - Medical Conditions: ${activeProfile.medicalConditions}
            """.trimIndent()
        } else {
            "No active health profile selected. Provide general premium nutrition advice."
        }

        val prompt = """
            You are TruBite's elite AI Nutrition Coach.
            Analyze the following product:
            Product Name: $productName
            Brand: $brand
            Ingredients: $ingredients
            
            $profileStr
            
            Provide an elegant, evidence-based nutritional analysis. Highlight:
            1. Why it gets its TruScore (an estimate out of 100).
            2. High-impact ingredients, processing level, harmful additives, artificial colors, sweeteners, preservatives, and allergen risks.
            3. Detailed personalized warnings if the product conflicts with the user's allergies or medical conditions (e.g. Diabetes, Pregnancy).
            4. Potential side effects and maximum recommended daily quantity in grams/milliliters based on serving size.
            5. Explain how this product affects the user's body visually (for our 3D Body Simulator).
            
            Your response must be in plain, beautifully written paragraphs.
        """.trimIndent()

        try {
            val jsonRequest = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contentsArray)
            }

            val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Request failed: ${response.code} ${response.message}")
                    return@withContext getLocalAiFeedback(productName, brand, ingredients, activeProfile)
                }

                val bodyString = response.body?.string() ?: ""
                val responseJson = JSONObject(bodyString)
                val text = responseJson
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                return@withContext text
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating AI feedback", e)
            return@withContext getLocalAiFeedback(productName, brand, ingredients, activeProfile)
        }
    }

    /**
     * Ask assistant dynamic questions
     */
    suspend fun askAssistant(
        question: String,
        activeProfile: ProfileEntity?,
        historyContext: String = ""
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getLocalAssistantReply(question, activeProfile)
        }

        val profileStr = if (activeProfile != null) {
            """
            Active Profile:
            - Name: ${activeProfile.name}
            - Age: ${activeProfile.age}, Gender: ${activeProfile.gender}
            - Goals: ${activeProfile.goals}
            - Allergies: ${activeProfile.allergies}
            - Diet: ${activeProfile.diet}
            - Medical Conditions: ${activeProfile.medicalConditions}
            """.trimIndent()
        } else {
            "General health-conscious user."
        }

        val prompt = """
            You are TruBite's expert AI Health & Nutrition Assistant.
            
            $profileStr
            
            Recent scan history summary for reference:
            $historyContext
            
            User's Question: $question
            
            Answer this query with high precision, scientific backing, and professional warmth. Include visual formatting (bullet points, clear paragraphs).
            Always append this medical disclaimer as a footnote:
            "Disclaimer: I am an AI grocery & health companion, not a medical doctor. Always consult with a registered healthcare professional for actual medical advice."
        """.trimIndent()

        try {
            val jsonRequest = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contentsArray)
            }

            val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext getLocalAssistantReply(question, activeProfile)
                }
                val bodyString = response.body?.string() ?: ""
                val responseJson = JSONObject(bodyString)
                return@withContext responseJson
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            }
        } catch (e: Exception) {
            return@withContext getLocalAssistantReply(question, activeProfile)
        }
    }

    /**
     * Local rule engine for offline or missing API key scenario
     */
    fun getLocalAiFeedback(
        productName: String,
        brand: String,
        ingredients: String,
        activeProfile: ProfileEntity?
    ): String {
        val lowerName = productName.lowercase()
        val lowerIngredients = ingredients.lowercase()
        val builder = StringBuilder()

        builder.append("### AI Analysis by TruBite Coach\n\n")
        builder.append("This is an analysis of **$productName** by $brand. ")

        // Detect allergies
        val activeAllergies = activeProfile?.allergies?.split(",")?.map { it.trim().lowercase() }?.filter { it.isNotEmpty() } ?: emptyList()
        val triggeredAllergies = mutableListOf<String>()
        for (allergy in activeAllergies) {
            if (lowerIngredients.contains(allergy) || lowerName.contains(allergy)) {
                triggeredAllergies.add(allergy.replaceFirstChar { it.uppercase() })
            }
        }

        // Detect medical issues
        val activeConditions = activeProfile?.medicalConditions?.split(",")?.map { it.trim().lowercase() }?.filter { it.isNotEmpty() } ?: emptyList()
        val isDiabetic = activeConditions.contains("diabetes") || activeConditions.contains("diabetic")
        val isPregnant = activeConditions.contains("pregnancy") || activeConditions.contains("pregnant")

        if (triggeredAllergies.isNotEmpty()) {
            builder.append("⚠️ **CRITICAL ALLERGEN WARNING:** This product contains **${triggeredAllergies.joinToString(", ")}** which directly triggers allergies configured in ${activeProfile?.name ?: "your"}'s profile! Avoid consumption immediately.\n\n")
        }

        if (isDiabetic && (lowerIngredients.contains("sugar") || lowerIngredients.contains("syrup") || lowerIngredients.contains("dextrose"))) {
            builder.append("⚠️ **DIABETES RISK ALERT:** High glycaemic load! Contains active sugars/sweeteners. Consuming this will cause rapid glucose spikes. We highly advise against this item for ${activeProfile?.name}.\n\n")
        }

        if (isPregnant && (lowerIngredients.contains("caffeine") || lowerIngredients.contains("saccharin") || lowerIngredients.contains("preservative"))) {
            builder.append("⚠️ **PREGNANCY SAFETY NOTE:** Contains preservatives, artificial compounds or additives which may be suboptimal during pregnancy. Take caution.\n\n")
        }

        // High processing detection
        if (lowerIngredients.contains("sodium") || lowerIngredients.contains("preservative") || lowerIngredients.contains("artificial") || lowerIngredients.contains("emulsifier")) {
            builder.append("🔬 **Ingredient Quality:** This product contains multiple ultra-processed additives, emulsifiers, or preservatives. Continuous consumption is linked to minor gut barrier disruption.\n\n")
            builder.append("🥦 **Processing Level:** **Ultra-Processed Food (UPF)**. Contains synthetic substances not typically used in home kitchens.\n\n")
        } else {
            builder.append("🔬 **Ingredient Quality:** Mostly whole-food components with clean, identifiable elements. Excellent source of organic fuel.\n\n")
            builder.append("🥦 **Processing Level:** **Minimally Processed**. Retains almost all its natural molecular structure.\n\n")
        }

        // Daily limit
        val maxQty = if (lowerIngredients.contains("sugar")) "45g" else "150g"
        builder.append("🕒 **Daily Consumption Limit:** Recommended maximum of **$maxQty** daily for a health-conscious lifestyle.\n\n")

        // 3D Simulator advice
        builder.append("🩸 **3D Body Simulation Impact:**\n")
        if (lowerIngredients.contains("sugar")) {
            builder.append("- **Pancreas:** Direct glucose overload triggers aggressive insulin synthesis, leading to eventual lethargy and energy crash.\n")
            builder.append("- **Liver:** Excess fructose conversion leads to fatty liver deposits over time.\n")
        } else if (lowerIngredients.contains("sodium")) {
            builder.append("- **Cardiovascular System:** Excess sodium content increases vascular osmotic pressure, raising blood tension markers.\n")
        } else {
            builder.append("- **Metabolism:** Optimal cellular energy conversion. Sustained slow-release energy avoids hormonal spikes.\n")
        }

        return builder.toString()
    }

    private fun getLocalAssistantReply(question: String, activeProfile: ProfileEntity?): String {
        val q = question.lowercase()
        val name = activeProfile?.name ?: "there"
        val condition = activeProfile?.medicalConditions ?: "none"

        return when {
            q.contains("diabetic") || q.contains("diabetes") -> {
                """
                ### Diabetics Food Guide
                For a user like $name, managing glycemic triggers is critical:
                
                1. **Focus on Glycemic Index (GI):** Choose whole foods with low GI (under 55) like beans, spinach, oats, and nuts.
                2. **Fiber is your Ally:** Soluble fiber delays sugar absorption. Aim for 30g+ daily.
                3. **Read Labels for hidden sugars:** Avoid Maltodextrin, High Fructose Corn Syrup, and Dextrose.
                
                *Check TruBite's Grocery Optimizer to replace high-sugar foods instantly!*
                
                *Disclaimer: I am an AI grocery & health companion, not a medical doctor. Always consult with a registered healthcare professional for actual medical advice.*
                """.trimIndent()
            }
            q.contains("pregnancy") || q.contains("pregnant") -> {
                """
                ### Pregnancy Nutrition Guidelines
                Based on active pregnancy profile guidelines:
                
                1. **Micro-nutrients Priority:** Ensure adequate intake of Folic Acid, Iron, Calcium, and Vitamin D.
                2. **Avoid Raw or Unpasteurized items:** Check food labels strictly for pasteurization indicators.
                3. **Minimize synthetic additives:** Steer clear of artificial sweeteners (like Saccharin) and artificial colors.
                
                *TruBite automatically screens for pregnancy-unfriendly chemicals on every scan!*
                
                *Disclaimer: I am an AI grocery & health companion, not a medical doctor. Always consult with a registered healthcare professional for actual medical advice.*
                """.trimIndent()
            }
            q.contains("additive") || q.contains("preservative") -> {
                """
                ### Food Additive Breakdown
                Additives are categorized into preservatives, emulsifiers, and artificial colorings:
                
                - **Highly Avoidable:** Carrageenan (gut inflamer), Aspartame (sweetener), Red 40 (artificial dye).
                - **Moderately Safe:** Citric acid, Ascorbic acid (Vitamin C preservative).
                
                Use TruBite's Smart Shopping Camera to color-code additives in real-time as you scan shelves!
                
                *Disclaimer: I am an AI grocery & health companion, not a medical doctor. Always consult with a registered healthcare professional for actual medical advice.*
                """.trimIndent()
            }
            else -> {
                """
                ### Premium Wellness Response
                Hi $name! Here are three smart shopping habits supported by wellness science:
                
                1. **Shop the Perimeter:** Fresh produce, dairy, and lean proteins are usually on the edges of the store. Processed items reside in center aisles.
                2. **Rule of 5:** If an ingredient list has more than 5 items, check its TruScore.
                3. **Active Family Syncing:** Always toggle your active health twin before buying. What's excellent for you might trigger allergens in your child!
                
                How else can I help you make smart shopping decisions today?
                
                *Disclaimer: I am an AI grocery & health companion, not a medical doctor. Always consult with a registered healthcare professional for actual medical advice.*
                """.trimIndent()
            }
        }
    }
}
