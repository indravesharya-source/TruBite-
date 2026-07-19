package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==========================================
// CENTRAL NAVIGATION DESTINATIONS
// ==========================================
object Destination {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val PROFILE_CREATION = "profile_creation"
    const val HOME = "home"
    const val SCANNER = "scanner"
    const val PRODUCT_DETAILS = "product_details"
    const val SEARCH = "search"
    const val CHAT = "chat"
    const val CART_OPTIMIZER = "cart_optimizer"
    const val SMART_CAMERA = "smart_camera"
    const val FAMILY_DASHBOARD = "family_dashboard"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
}

// ==========================================
// COLOR UTILS
// ==========================================
fun getScoreColor(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF22C55E) // Excellent Green
        score >= 60 -> Color(0xFFF59E0B) // Good Orange/Yellow
        score >= 40 -> Color(0xFFEAB308) // Moderate
        else -> Color(0xFFEF4444) // Poor Red
    }
}

fun getScoreLabel(score: Int): String {
    return when {
        score >= 80 -> "Excellent"
        score >= 60 -> "Good"
        score >= 40 -> "Moderate"
        else -> "Poor"
    }
}

// ==========================================
// CUSTOM STABLE TOP APP BAR
// ==========================================
@Composable
fun TruBiteTopAppBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (navigationIcon != null) {
                navigationIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                title()
            }
            if (actions != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }
    }
}


// ==========================================
// SPLASH SCREEN
// ==========================================
@Composable
fun SplashScreen(onNavigateToOnboarding: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Vector-like Brand Logo
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(scale)
                    .background(Color(0xFF22C55E), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "TruBite Logo",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "TruBite",
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Know Before You Buy",
                color = Color(0xFF94A3B8),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        LaunchedEffect(Unit) {
            delay(2000)
            onNavigateToOnboarding()
        }
    }
}

// ==========================================
// ONBOARDING SCREEN (4 Pages)
// ==========================================
@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }

    val titles = listOf(
        "AI-Powered Health Twin",
        "Smart Barcode Scanner",
        "OCR Ingredient Analysis",
        "Grocery Cart Optimizer"
    )

    val descriptions = listOf(
        "Create personalized health profiles for your entire family. TruBite screens ingredients against allergies & medical conditions instantly.",
        "Scan any packaged food item to reveal the TruScore, hidden sugar contents, raw processing levels, and clean alternatives.",
        "Snap a photo of ingredient labels. Our AI highlights harmful chemical additives, synthetic colors, and artificial sweeteners.",
        "Scan your entire basket. TruBite summarizes cumulative calories, sugar load, monthly wellness impact, and budget optimizations."
    )

    val icons = listOf(
        Icons.Default.Group,
        Icons.Default.QrCodeScanner,
        Icons.Default.DocumentScanner,
        Icons.Default.ShoppingCart
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Illustration Core
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icons[currentPage],
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Text info
            Text(
                text = titles[currentPage],
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = descriptions[currentPage],
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Page indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { idx ->
                    Box(
                        modifier = Modifier
                            .size(if (currentPage == idx) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentPage == idx) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }

        // Action Buttons at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.testTag("skip_button")
            ) {
                Text("Skip", color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = {
                    if (currentPage < 3) {
                        currentPage++
                    } else {
                        onNavigateToLogin()
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("next_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (currentPage == 3) "Get Started" else "Next", color = Color.White)
            }
        }
    }
}

// ==========================================
// LOGIN / SIGNUP SCREEN
// ==========================================
@Composable
fun LoginScreen(onNavigateToProfileCreation: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Heading Icon
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSignUp) "Create Account" else "Welcome to TruBite",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = if (isSignUp) "Join the premium health community today" else "Sign in to access your AI health twins",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Action Button
            Button(
                onClick = onNavigateToProfileCreation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (isSignUp) "Sign Up" else "Log In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mock OAuth Button
            OutlinedButton(
                onClick = onNavigateToProfileCreation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue with Google", color = MaterialTheme.colorScheme.onBackground)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Toggle Signup/Login
            TextButton(onClick = { isSignUp = !isSignUp }) {
                Text(
                    text = if (isSignUp) "Already have an account? Sign In" else "New to TruBite? Create Account",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ==========================================
// HEALTH PROFILE CREATION SCREEN
// ==========================================
@Composable
fun ProfileCreationScreen(viewModel: TruBiteViewModel, onNavigateToHome: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Self") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var goals by remember { mutableStateOf("Weight Loss") }
    var allergies by remember { mutableStateOf("") }
    var diet by remember { mutableStateOf("Balanced") }
    var medicalConditions by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Build Your Health Twin",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "This fuels our TruScore algorithm and personalized warning systems.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Inputs
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_name_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Allergies (e.g. Nuts, Dairy, Gluten, Soy)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it },
                placeholder = { Text("Leave blank if none") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Medical Conditions (e.g. Diabetes, Hypertension, Pregnancy)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            OutlinedTextField(
                value = medicalConditions,
                onValueChange = { medicalConditions = it },
                placeholder = { Text("Leave blank if none") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Diet Focus", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val dietsList = listOf("Balanced", "Keto", "Vegan", "Vegetarian", "Paleo", "Sugar-Free")
                items(dietsList) { item ->
                    FilterChip(
                        selected = diet == item,
                        onClick = { diet = item },
                        label = { Text(item) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Primary Wellness Goal", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val goalsList = listOf("Weight Loss", "Manage Blood Sugar", "Clean Eating", "Heart Health", "Allergy Protection")
                items(goalsList) { item ->
                    FilterChip(
                        selected = goals == item,
                        onClick = { goals = item },
                        label = { Text(item) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        viewModel.createFamilyProfile(
                            name = name,
                            relationship = relationship,
                            age = age.toIntOrNull() ?: 28,
                            weight = weight.toDoubleOrNull() ?: 70.0,
                            height = height.toDoubleOrNull() ?: 170.0,
                            gender = gender,
                            goals = goals,
                            allergies = allergies,
                            diet = diet,
                            medicalConditions = medicalConditions
                        )
                    }
                    onNavigateToHome()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_profile_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Create Health Twin", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ==========================================
// HOME SCREEN (Main Dashboard)
// ==========================================
@Composable
fun HomeScreen(
    viewModel: TruBiteViewModel,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToChat: () -> Unit = {},
    onNavigateToCartOptimizer: () -> Unit = {}
) {
    val activeProfile by viewModel.activeProfile.collectAsState()
    val scanHistory by viewModel.scanHistory.collectAsState()
    val profilesList by viewModel.allProfiles.collectAsState()

    var showProfileDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // T brand block icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF22C55E), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "T",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        Column {
                            Text(
                                text = "TruBite",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "KNOW BEFORE YOU BUY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Active Health Twin Selector / Avatar Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f), CircleShape)
                            .clip(CircleShape)
                            .clickable { showProfileDropdown = !showProfileDropdown },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("👤", fontSize = 18.sp)
                            // Green notification/active state dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // AI Health Twin Status Card (gorgeous large corner rounded gradient/green)
                item {
                    val activeTwin = activeProfile
                    val tip = when {
                        activeTwin == null -> "Make sure to scan products to see instant science-backed ingredient breakdowns!"
                        activeTwin.allergies.contains("Nuts", ignoreCase = true) -> "Double scanning is highly recommended today! Avoid nut-based items carefully."
                        activeTwin.medicalConditions.contains("Diabetes", ignoreCase = true) -> "Keep glucose curves flat! Target snacks under 5g of total added sugar."
                        else -> "High-protein day! Target oat-based snacks to secure stable sustained metabolic energy release."
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(32.dp), // rounded-[2rem]
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF22C55E))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Absolute right circular overlay glow (blur decorative)
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 16.dp, y = (-16).dp)
                            )

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            text = "AI Health Twin Status",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White.copy(alpha = 0.8f),
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = if (activeTwin != null) "Optimization: 84% Sync" else "Twin Status: Offline",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE NOW",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "“$tip”",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 20.sp,
                                    color = Color.White.copy(alpha = 0.95f)
                                )
                            }
                        }
                    }
                }

                // Standard Primary Actions: Scan & Search
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onNavigateToScanner,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Scan Product", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = onNavigateToSearch,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Search Items", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Quick Action Grid (2 column beautifully styled cards from the Sleek HTML theme)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cart Optimizer Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(112.dp)
                                .clickable { onNavigateToCartOptimizer() },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp)), // orange-100
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🛒", fontSize = 16.sp)
                                }
                                Column {
                                    Text(
                                        text = "Cart Optimizer",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "Optimize cart items",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }

                        // Ask AI Assistant Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(112.dp)
                                .clickable { onNavigateToChat() },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFFF3E8FF), RoundedCornerShape(8.dp)), // purple-100
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("💬", fontSize = 16.sp)
                                }
                                Column {
                                    Text(
                                        text = "Ask AI Assistant",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "Safety checks & tips",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Active Health Twin Snapshot Card
                item {
                    Text(
                        text = "Active Health Twin Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    val twin = activeProfile
                    if (twin != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(twin.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Text("Dietary Model: ${twin.diet}", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 13.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(twin.relationship, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Allergies", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontSize = 11.sp)
                                        Text(if (twin.allergies.isNotEmpty()) twin.allergies else "None", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Medical Note", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontSize = 11.sp)
                                        Text(if (twin.medicalConditions.isNotEmpty()) twin.medicalConditions else "None", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Body Target", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontSize = 11.sp)
                                        Text(twin.goals, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No profile selected. Create or switch twins below.")
                        }
                    }
                }

                // Recent scan history list
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Scanned Items",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (scanHistory.isNotEmpty()) {
                            TextButton(onClick = { viewModel.handleBarcodeScan("8901234567891") }) {
                                Text("Scan Demo Oat", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                if (scanHistory.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Your scan history is empty",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Scan packaged food items at stores to populate live score analyses instantly.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(scanHistory) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.handleBarcodeScan(product.barcode)
                                    onNavigateToProduct(product.barcode)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Dynamic Color Ring representing Score
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(getScoreColor(product.truScore).copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = product.truScore.toString(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = getScoreColor(product.truScore)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${product.brand} • ${product.calories.toInt()} kcal",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }

                                // Warning indicator
                                if (product.warnings.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Safe",
                                        tint = Color(0xFF22C55E),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            // Quick Active Twin Selector Dropdown Overlay
            if (showProfileDropdown) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { showProfileDropdown = false }
                ) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 60.dp, end = 24.dp)
                            .width(220.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Switch Health Twin",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(8.dp)
                            )
                            profilesList.forEach { profile ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.switchActiveProfile(profile.id)
                                            showProfileDropdown = false
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = profile.isActive,
                                        onClick = {
                                            viewModel.switchActiveProfile(profile.id)
                                            showProfileDropdown = false
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = profile.name,
                                        fontSize = 14.sp,
                                        fontWeight = if (profile.isActive) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SCANNER SCREEN (Interactive Camera Simulation)
// ==========================================
@Composable
fun ScannerScreen(viewModel: TruBiteViewModel, onNavigateToProduct: (String) -> Unit) {
    val isScanning by viewModel.isScanning.collectAsState()
    val scannedProduct by viewModel.scannedProduct.collectAsState()
    val activeProfile by viewModel.activeProfile.collectAsState()

    var manualBarcode by remember { mutableStateOf("") }
    var ocrMode by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
        ) {
            // Simulated Active Camera Viewfinder
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header HUD
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (ocrMode) "OCR Label Scanner" else "Smart Barcode Scanner",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Targeting twin: ${activeProfile?.name}",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = { ocrMode = !ocrMode },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (ocrMode) Icons.Default.QrCode else Icons.Default.DocumentScanner,
                            contentDescription = "Toggle OCR",
                            tint = Color.White
                        )
                    }
                }

                // Camera Scan Area Frame overlay
                Box(
                    modifier = Modifier
                        .size(if (ocrMode) 320.dp else 260.dp)
                        .border(3.dp, Color(0xFF22C55E), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Animating horizontal red scanning laser line
                    val infiniteTransition = rememberInfiniteTransition(label = "laser")
                    val laserOffset by infiniteTransition.animateFloat(
                        initialValue = -120f,
                        targetValue = 120f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "laser_pos"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .offset(y = laserOffset.dp)
                            .background(Color(0xFFEF4444))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (ocrMode) Icons.Default.CameraAlt else Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (ocrMode) "Place Ingredient Label Inside" else "Align Barcode in Frame",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }

                // Interactive Demo catalog & Manual Trigger Drawer
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Or Select Demo Products to Scan Instantly:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Curated demo barcode scans
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val items = listOf(
                                "8901234567890" to "ChocoBlox (Poor)",
                                "8901234567891" to "OatPure (Excellent)",
                                "8901234567892" to "Diet Cola (Moderate)",
                                "8901234567893" to "A2 Yogurt (Good)",
                                "8901234567894" to "Ramen (Poor)"
                            )
                            items(items) { (barcode, label) ->
                                Button(
                                    onClick = {
                                        viewModel.handleBarcodeScan(barcode)
                                        scope.launch {
                                            delay(500)
                                            onNavigateToProduct(barcode)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(label, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Manual Barcode Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = manualBarcode,
                                onValueChange = { manualBarcode = it },
                                placeholder = { Text("Enter barcode manually") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    if (manualBarcode.isNotEmpty()) {
                                        viewModel.handleBarcodeScan(manualBarcode)
                                        scope.launch {
                                            delay(500)
                                            onNavigateToProduct(manualBarcode)
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Go")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SEARCH SCREEN (With Voice Feature)
// ==========================================
@Composable
fun SearchScreen(
    viewModel: TruBiteViewModel,
    onNavigateToProduct: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val isListeningVoice by viewModel.isListeningVoice.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search product name, brand or barcode") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.toggleVoiceListening() }) {
                            Icon(
                                imageVector = if (isListeningVoice) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Voice",
                                tint = if (isListeningVoice) Color.Red else MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (results.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (query.isEmpty()) "Find what to eat" else "No matching products found",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if (query.isEmpty()) "Search our database or tap the microphone to dictate product keywords."
                        else "Try checking spelling, searching a general term like \"Oats\", or type \"Choco\" to list desserts.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(results) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.handleBarcodeScan(product.barcode)
                                    onNavigateToProduct(product.barcode)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(getScoreColor(product.truScore).copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = product.truScore.toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = getScoreColor(product.truScore)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(product.brand, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
            }

            // Voice search dictating UI dialog overlays
            if (isListeningVoice) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Listening",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(54.dp)
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Listening to Voice...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Say \"Whole grain oats\" or \"Yogurt\"",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// PRODUCT DETAILS SCREEN (With 3D Simulator!)
// ==========================================
@Composable
fun ProductDetailsScreen(
    viewModel: TruBiteViewModel,
    onNavigateBack: () -> Unit
) {
    val product by viewModel.scannedProduct.collectAsState()
    val activeProfile by viewModel.activeProfile.collectAsState()

    // 3D Body simulator visual toggle state
    var show3DPreview by remember { mutableStateOf(false) }
    var selectedOrganInfo by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    val item = product ?: return

    Scaffold(
        topBar = {
            TruBiteTopAppBar(
                title = { Text(item.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(item.barcode) }) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { viewModel.addToCart(item.barcode) }) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Color-Coded Score Ribbon
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = getScoreColor(item.truScore).copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Score arc circular progress Canvas
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val scoreColor = getScoreColor(item.truScore)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = scoreColor.copy(alpha = 0.2f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = scoreColor,
                                startAngle = -90f,
                                sweepAngle = (item.truScore * 3.6f),
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.truScore.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = scoreColor
                            )
                            Text(
                                text = "TRUSCORE",
                                fontSize = 8.sp,
                                color = scoreColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = getScoreLabel(item.truScore),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = getScoreColor(item.truScore)
                        )
                        Text(
                            text = "Brand: ${item.brand}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Category: ${item.category}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Warning Alerts panel if diabetic sugar warning / allergies warning is active
            if (item.warnings.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, Color(0xFFEF4444))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dangerous,
                            contentDescription = "Danger Warning",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.warnings,
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Nutrients summary metrics cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val metrics = listOf(
                    Triple("${item.calories.toInt()} kcal", "Calories", Icons.Default.LocalFireDepartment),
                    Triple(item.servingSize, "Serving", Icons.Default.FitnessCenter),
                    Triple(item.processingLevel, "Process", Icons.Default.Memory)
                )
                metrics.forEach { (valStr, label, icon) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(valStr, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // Macro Nutrients chart
            Text("Macro Nutrients Breakdowns", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val macros = listOf(
                        Triple("Carbs", item.carbs, Color(0xFF3B82F6)),
                        Triple("Sugar", item.sugar, Color(0xFFF59E0B)),
                        Triple("Protein", item.protein, Color(0xFF22C55E)),
                        Triple("Fat", item.fat, Color(0xFFEF4444)),
                        Triple("Sodium", item.sodium * 1000, Color(0xFF8B5CF6)) // multiply by 1000 to show in mg
                    )

                    macros.forEach { (name, gVal, color) ->
                        val limit = if (name == "Sodium") 2300.0 else 100.0
                        val percentage = (gVal / limit).coerceIn(0.0, 1.0).toFloat()
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = if (name == "Sodium") "${gVal.toInt()} mg" else "${gVal} g",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(percentage)
                                        .height(6.dp)
                                        .background(color, CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            // 3D Body Simulator Educational Section!
            Text("3D Human Body Impact Simulator", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Simulate Nutrient Processing", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("See how the ingredients affect organ targets in real-time.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                        Button(
                            onClick = { show3DPreview = !show3DPreview },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (show3DPreview) "Close" else "Launch")
                        }
                    }

                    // Interactive 3D wireframe simulator body UI
                    if (show3DPreview) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Rotating cellular/wireframe elements using an infinite rotating modifier animation
                            val infiniteTransition = rememberInfiniteTransition(label = "sim_rotation")
                            val rotationAngle by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(12000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "angle"
                            )

                            // Graphic representation of body
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .rotate(rotationAngle),
                                contentAlignment = Alignment.Center
                            ) {
                                // Draw stylized human skeletal outline on Canvas
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color(0xFF22C55E).copy(alpha = 0.05f),
                                        radius = size.minDimension / 2.2f,
                                        style = Stroke(width = 1.dp.toPx())
                                    )
                                    drawCircle(
                                        color = Color(0xFF22C55E).copy(alpha = 0.15f),
                                        radius = size.minDimension / 3.5f,
                                        style = Stroke(width = 2.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                                    )
                                }
                            }

                            // Pulsing core organ nodes to click on overlay
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceAround,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    // Organ: Brain
                                    OrganNode("Brain (Hormones)") {
                                        selectedOrganInfo = "Brain: " + if (item.sugar > 15.0) {
                                            "Sugar spikes block satiety centers and induce high craving pathways. Moderation is key."
                                        } else {
                                            "A2 healthy fatty lipids support sound membrane potential in glial brain networks."
                                        }
                                    }
                                    // Organ: Heart
                                    OrganNode("Heart (Vascular)") {
                                        selectedOrganInfo = "Cardiovascular: " + if (item.sodium > 0.5) {
                                            "Sodium levels overload water content in blood vessels, increasing systolic heart rate markers."
                                        } else {
                                            "Clean whole foods help avoid vascular plaque formation, sustaining cardiac elasticity."
                                        }
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    // Organ: Pancreas
                                    OrganNode("Pancreas (Insulin)") {
                                        selectedOrganInfo = "Pancreas: " + if (item.sugar > 12.0) {
                                            "High glycemic sugars force severe, continuous insulin surge. Regular spikes degrade beta-cell efficiency."
                                        } else {
                                            "Low glycemic fibers maintain flat glucose curves, protecting baseline insulin receptors."
                                        }
                                    }

                                    // Organ: Stomach
                                    OrganNode("Liver & Gut") {
                                        selectedOrganInfo = "Liver & Gut: " + if (item.processingLevel.contains("Ultra")) {
                                            "Ultra-processed synthetic additives or preservative elements degrade beneficial microflora cultures."
                                        } else {
                                            "Beneficial soluble raw oats fiber fuels healthy colon bifidobacteria populations."
                                        }
                                    }
                                }
                            }
                        }

                        // Display selected organ details pop-up explanation
                        if (selectedOrganInfo != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("Simulation Analysis", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                        IconButton(onClick = { selectedOrganInfo = null }, modifier = Modifier.size(18.dp)) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(selectedOrganInfo!!, fontSize = 12.sp, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }

            // AI Explanation Panel
            Text("Personalised AI Health Coach Explanations", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.aiExplanation,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Ingredient Chip lists
            Text("Full Ingredients List", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp
            ) {
                item.ingredients.split(",").forEach { ingredient ->
                    val cleanIng = ingredient.trim()
                    if (cleanIng.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(cleanIng, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Side effects & Daily limits
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Scale, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Suggested Daily Limit", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text("We recommend a maximum serving limit of **${item.dailyRecommendedQty}** per single day based on caloric concentration.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chronic Side Effects Warning", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text(item.sideEffects, fontSize = 13.sp)
                }
            }

            // Clean Alternatives
            Text("Suggested Clean Alternatives", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            item.alternatives.split(",").forEach { alternative ->
                val altName = alternative.trim()
                if (altName.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Simulate scanning alternative
                                viewModel.handleBarcodeScan("8901234567891")
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFF22C55E).copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(altName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun OrganNode(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E).copy(alpha = 0.25f)),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF22C55E), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// FlowRow layout implementation backport to avoid Compose compilation issues
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val mainSpacingPx = mainAxisSpacing.roundToPx()
        val crossSpacingPx = crossAxisSpacing.roundToPx()

        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0
        var totalHeight = 0

        placeables.forEach { placeable ->
            if (currentRowWidth + placeable.width > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                totalHeight += currentRow.maxOf { it.height } + crossSpacingPx
                currentRow = mutableListOf()
                currentRowWidth = 0
            }
            currentRow.add(placeable)
            currentRowWidth += placeable.width + mainSpacingPx
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
            totalHeight += currentRow.maxOf { it.height }
        }

        layout(constraints.maxWidth, totalHeight) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + mainSpacingPx
                }
                y += row.maxOf { it.height } + crossSpacingPx
            }
        }
    }
}

// ==========================================
// AI CHAT HEALTH ASSISTANT (With Disclaimer)
// ==========================================
@Composable
fun ChatScreen(viewModel: TruBiteViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isGeneratingChat by viewModel.isGeneratingChat.collectAsState()
    val activeProfile by viewModel.activeProfile.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TruBiteTopAppBar(
                title = {
                    Column {
                        Text("AI Nutrition Twin Coach", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = "Answering personalized for: ${activeProfile?.name ?: "All"}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // High-contrast warning medical disclaimer alert
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                    .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Medical Disclaimer: AI advice is for general food awareness education only. Do not replace professional clinician diagnostics.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Suggestion quick questions
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val suggestions = listOf(
                    "Can diabetics eat sugar-free products?",
                    "Highlight artificial sweeteners risks",
                    "Is pasteurized yogurt safe during pregnancy?",
                    "Explain ultra-processed foods (UPF)"
                )
                items(suggestions) { prompt ->
                    Button(
                        onClick = { viewModel.sendChatMessage(prompt) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(prompt, color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
                    }
                }
            }

            // Scrolling chats list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatMessages) { msg ->
                    val bubbleColor = if (msg.isUser) MaterialTheme.colorScheme.primary
                                      else MaterialTheme.colorScheme.surfaceVariant
                    val textColor = if (msg.isUser) Color.White
                                    else MaterialTheme.colorScheme.onSurface
                    val align = if (msg.isUser) Alignment.End else Alignment.Start

                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
                        Box(
                            modifier = Modifier
                                .background(bubbleColor, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = textColor,
                                fontSize = 14.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // AI typing indicator
                if (isGeneratingChat) {
                    item {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .width(80.dp)
                        ) {
                            Text("Thinking...", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Sticky Bottom Input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask health coach Twin questions...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotEmpty()) {
                            viewModel.sendChatMessage(textInput)
                            textInput = ""
                            scope.launch {
                                delay(300)
                                listState.animateScrollToItem(chatMessages.size)
                            }
                        }
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

// ==========================================
// GROCERY CART HEALTH OPTIMIZER
// ==========================================
@Composable
fun CartOptimizerScreen(viewModel: TruBiteViewModel, onNavigateToProduct: (String) -> Unit) {
    val cartItems by viewModel.cartItems.collectAsState()
    val scanHistory by viewModel.scanHistory.collectAsState()

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TruBiteTopAppBar(title = { Text("Cart Health Optimizer", fontWeight = FontWeight.Bold) })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Aggregated Cart Metrics card
            item {
                // Calculate cumulative score and metrics dynamically
                var totalCalories = 0.0
                var totalSugar = 0.0
                var totalSodium = 0.0
                var totalProtein = 0.0
                var totalItemsCount = 0
                var cumulativeScoreSum = 0

                cartItems.forEach { cartItem ->
                    val product = scanHistory.find { it.barcode == cartItem.barcode }
                    if (product != null) {
                        totalCalories += product.calories * cartItem.quantity
                        totalSugar += product.sugar * cartItem.quantity
                        totalSodium += product.sodium * cartItem.quantity
                        totalProtein += product.protein * cartItem.quantity
                        cumulativeScoreSum += product.truScore * cartItem.quantity
                        totalItemsCount += cartItem.quantity
                    }
                }

                val avgScore = if (totalItemsCount > 0) cumulativeScoreSum / totalItemsCount else 100

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Cumulative Cart Health", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                                Text("Based on $totalItemsCount grocery items.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(getScoreColor(avgScore), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(avgScore.toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Nutrients aggregated list
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${totalCalories.toInt()} kcal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Total Calories", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${totalSugar.toInt()}g", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (totalSugar > 40.0) Color.Red else MaterialTheme.colorScheme.onBackground)
                                Text("Added Sugar", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${(totalSodium * 1000).toInt()}mg", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Total Sodium", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${totalProtein.toInt()}g", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF22C55E))
                                Text("Protein Fuel", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }

            // Monthly impact estimation card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Estimated Monthly Impact", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Consuming these grocery choices over 30 days is estimated to yield a healthy lipid count balance.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            // Cart Items list
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Grocery Items Basket", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    TextButton(onClick = { viewModel.clearCart() }) {
                        Text("Clear Basket", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (cartItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Grocery Cart is empty", fontWeight = FontWeight.Bold)
                            Text("Add products from scan detail to test optimizer analyses.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                    }
                }
            } else {
                items(cartItems) { item ->
                    val product = scanHistory.find { it.barcode == item.barcode }
                    if (product != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToProduct(product.barcode) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(getScoreColor(product.truScore).copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(product.truScore.toString(), fontWeight = FontWeight.Bold, color = getScoreColor(product.truScore))
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Qty: ${item.quantity} • ${product.brand}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { viewModel.removeFromCart(product.barcode) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }

                // Healthy replacement suggestions section
                item {
                    Text("Cart Replacements Recommendations", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF22C55E).copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Swap \"ChocoBlox Sugar Bars\" Out", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF22C55E))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Swap for **OatPure Whole Grain Oats**! Doing so cuts 35g of pure processed sugar and adds wholesome organic flax seeds, raising your cumulative basket score.", fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

// ==========================================
// SMART SHOPPING AR VIEW CAMERA
// ==========================================
@Composable
fun SmartCameraScreen(viewModel: TruBiteViewModel) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
        ) {
            // Simulated AR background elements
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("AI Smart Shopping Camera", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Real-time shelf item analysis overlay (Simulated AR)", color = Color.LightGray, fontSize = 11.sp)
                }

                // AR Overlay Marker widgets
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ARMarker(label = "ChocoBlox Chocolates", score = 24, badgeColor = Color(0xFFEF4444), action = "🔴 AVOID (High glycemic sugar)")
                    ARMarker(label = "OatPure Harvest Oats", score = 92, badgeColor = Color(0xFF22C55E), action = "🟢 RECOMMENDED (Excellent fiber)")
                    ARMarker(label = "Diet Fizzy Cola", score = 42, badgeColor = Color(0xFFF59E0B), action = "🟡 LIMIT (Contains Aspartame)")
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 80.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Hold camera in front of store shelves to see active M3 score indicators overlayed directly over packages.", color = Color.White, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ARMarker(label: String, score: Int, badgeColor: Color, action: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f)),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.5.dp, badgeColor),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(score.toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(action, color = Color.LightGray, fontSize = 11.sp)
            }
        }
    }
}

// ==========================================
// FAMILY HEALTH DASHBOARD
// ==========================================
@Composable
fun FamilyDashboardScreen(viewModel: TruBiteViewModel, onNavigateToCreateProfile: () -> Unit) {
    val profilesList by viewModel.allProfiles.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateProfile,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_family_member_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Member")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Family Health Dashboard", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text("Manage dietary models & active twins for tailored warnings.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }

            items(profilesList) { profile ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.switchActiveProfile(profile.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (profile.isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = if (profile.isActive) 2.dp else 1.dp,
                        color = if (profile.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = profile.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (profile.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text("Diet focusing: ${profile.diet}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }

                            RadioButton(
                                selected = profile.isActive,
                                onClick = { viewModel.switchActiveProfile(profile.id) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Allergies List", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                Text(if (profile.allergies.isNotEmpty()) profile.allergies else "None", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Medical Profile", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                Text(if (profile.medicalConditions.isNotEmpty()) profile.medicalConditions else "None", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

// ==========================================
// FAVORITES SCREEN
// ==========================================
@Composable
fun FavoritesScreen(viewModel: TruBiteViewModel, onNavigateToProduct: (String) -> Unit) {
    val favoritesList by viewModel.favoritesList.collectAsState()

    Scaffold(
        topBar = {
            TruBiteTopAppBar(title = { Text("Favorite Products", fontWeight = FontWeight.Bold) })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (favoritesList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No favorites saved", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Tap the heart icon on any scanned food item details page to bookmark it here.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favoritesList) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.handleBarcodeScan(product.barcode)
                                    onNavigateToProduct(product.barcode)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(getScoreColor(product.truScore).copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(product.truScore.toString(), fontWeight = FontWeight.Bold, color = getScoreColor(product.truScore))
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(product.brand, fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                }

                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favorite",
                                    tint = Color.Red,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SETTINGS SCREEN
// ==========================================
@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TruBiteTopAppBar(title = { Text("Settings", fontWeight = FontWeight.Bold) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Branded Banner Premium Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CardMembership, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("TruBite Premium Member", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Unlock unlimited AI Health Twin scans, advanced OCR processing, and high precision counterfeit detectors.", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, lineHeight = 16.sp)
                }
            }

            Text("Application Configurations", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

            SettingsRow(icon = Icons.Default.Translate, title = "Language", subtitle = "English (US)")
            SettingsRow(icon = Icons.Default.Notifications, title = "Push Alerts Notifications", subtitle = "Recall warnings on saved favorites")
            SettingsRow(icon = Icons.Default.Security, title = "Data Privacy & Encrypted Cache", subtitle = "Local health twins are fully offline")

            Divider()

            Text("Support & Transparency", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

            SettingsRow(icon = Icons.Default.QuestionMark, title = "Help & Live Support Chat", subtitle = "Contact nutrition twin builders")
            SettingsRow(icon = Icons.Default.LibraryBooks, title = "Scientific Evidence Database", subtitle = "Peer reviewed nutrition score methodology")

            Divider()

            Text("disclaimer note", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
            Text(
                text = "TruBite uses modern Gemini AI text modeling. Science notes are educational references and DO NOT replace professional medical consults. Always check with doctors on chronic diseases.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun SettingsRow(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}
