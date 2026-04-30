package com.darrius.vehiclemaintenacetracker.ui.screens.MechanicFinder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// ── Colour tokens ────────────────────────────────────────────────────────────
private val BgDark        = Color(0xFF0D1117)
private val SurfaceDark   = Color(0xFF161B22)
private val CardDark      = Color(0xFF1C2333)
private val AccentOrange  = Color(0xFFFF6B2B)
private val AccentAmber   = Color(0xFFFFB830)
private val TextPrimary   = Color(0xFFF0F6FC)
private val TextSecondary = Color(0xFF8B949E)
private val DividerColor  = Color(0xFF30363D)
private val OnlineGreen   = Color(0xFF3FB950)
private val BusyRed       = Color(0xFFF85149)
private val StarYellow    = Color(0xFFFFD700)

// ── Data models ──────────────────────────────────────────────────────────────
data class KenyaCounty(val name: String, val region: String, val mechanicCount: Int)

data class Mechanic(
    val id: Int,
    val name: String,
    val shop: String,
    val county: String,
    val specialties: List<String>,
    val rating: Float,
    val reviewCount: Int,
    val distance: String,
    val isAvailable: Boolean,
    val yearsExp: Int,
    val phone: String
)

private val kenyaCounties = listOf(
    KenyaCounty("Nairobi",       "Central",      148),
    KenyaCounty("Mombasa",       "Coast",         87),
    KenyaCounty("Kisumu",        "Nyanza",        63),
    KenyaCounty("Nakuru",        "Rift Valley",   74),
    KenyaCounty("Eldoret (Uasin Gishu)", "Rift Valley", 52),
    KenyaCounty("Thika (Kiambu)","Central",       59),
    KenyaCounty("Machakos",      "Eastern",       41),
    KenyaCounty("Nyeri",         "Central",       38),
    KenyaCounty("Meru",          "Eastern",       35),
    KenyaCounty("Kakamega",      "Western",       44),
    KenyaCounty("Kilifi",        "Coast",         29),
    KenyaCounty("Garissa",       "North Eastern", 18),
    KenyaCounty("Malindi",       "Coast",         22),
    KenyaCounty("Kisii",         "Nyanza",        31),
    KenyaCounty("Kericho",       "Rift Valley",   27),
    KenyaCounty("Embu",          "Eastern",       24),
    KenyaCounty("Kitale",        "Rift Valley",   26),
    KenyaCounty("Bungoma",       "Western",       33),
    KenyaCounty("Isiolo",        "Eastern",       14),
    KenyaCounty("Lamu",          "Coast",         11),
    KenyaCounty("Voi (Taita Taveta)", "Coast",    17),
    KenyaCounty("Nanyuki (Laikipia)", "Central",  21),
    KenyaCounty("Naivasha",      "Rift Valley",   19),
    KenyaCounty("Murang'a",      "Central",       28),
    KenyaCounty("Homa Bay",      "Nyanza",        23),
    KenyaCounty("Migori",        "Nyanza",        20),
    KenyaCounty("Bomet",         "Rift Valley",   16),
    KenyaCounty("Kajiado",       "Rift Valley",   22),
    KenyaCounty("Kwale",         "Coast",         15),
    KenyaCounty("Tana River",    "Coast",          9),
    KenyaCounty("Mandera",       "North Eastern",  8),
    KenyaCounty("Wajir",         "North Eastern",  7),
    KenyaCounty("Marsabit",      "Eastern",        6),
    KenyaCounty("Samburu",       "Rift Valley",    5),
    KenyaCounty("West Pokot",    "Rift Valley",    7),
    KenyaCounty("Turkana",       "Rift Valley",    6),
    KenyaCounty("Baringo",       "Rift Valley",   12),
    KenyaCounty("Nandi",         "Rift Valley",   14),
    KenyaCounty("Trans Nzoia",   "Rift Valley",   18),
    KenyaCounty("Elgeyo-Marakwet","Rift Valley",  10),
    KenyaCounty("Siaya",         "Nyanza",        17),
    KenyaCounty("Vihiga",        "Western",       13),
    KenyaCounty("Busia",         "Western",       16),
    KenyaCounty("Tharaka-Nithi", "Eastern",       11),
    KenyaCounty("Kirinyaga",     "Central",       19),
    KenyaCounty("Nyandarua",     "Central",       15),
    KenyaCounty("Makueni",       "Eastern",       21)
)

private val sampleMechanics = listOf(
    Mechanic(1, "James Mwangi",   "AutoFix Garage",         "Nairobi",  listOf("Engine","Brakes","Electrical"), 4.8f, 212, "1.2 km", true,  12, "+254 712 345678"),
    Mechanic(2, "Peter Otieno",   "Coast Auto Works",       "Mombasa",  listOf("Transmission","AC","Bodywork"),  4.6f, 178, "0.8 km", true,   9, "+254 722 456789"),
    Mechanic(3, "Samuel Kipchoge","Nakuru Motors",           "Nakuru",   listOf("Diesel","4WD","Suspension"),    4.9f, 305, "2.4 km", false,  16, "+254 733 567890"),
    Mechanic(4, "Grace Wanjiku",  "Thika Road Garage",      "Kiambu",   listOf("Electrical","Diagnostics"),     4.7f, 143, "3.1 km", true,   7, "+254 700 678901"),
    Mechanic(5, "David Ochieng",  "Kisumu Auto Centre",     "Kisumu",   listOf("Engine","Brakes"),              4.5f,  98, "1.9 km", true,  10, "+254 711 789012"),
    Mechanic(6, "Ali Hassan",     "Mombasa Express Fix",    "Mombasa",  listOf("Bodywork","Painting"),          4.3f,  76, "4.5 km", false,   5, "+254 745 890123")
)

private val specialtyFilters = listOf("All","Engine","Brakes","Electrical","Transmission","AC","Bodywork","Diesel","Suspension","Diagnostics")

// ── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicFinderScreen(navController: NavController) {

    var searchQuery      by remember { mutableStateOf("") }
    var selectedCounty   by remember { mutableStateOf<KenyaCounty?>(null) }
    var selectedFilter   by remember { mutableStateOf("All") }
    var selectedMechanic by remember { mutableStateOf<Mechanic?>(null) }
    var showCountySheet  by remember { mutableStateOf(false) }

    val filteredCounties = remember(searchQuery) {
        if (searchQuery.isBlank()) kenyaCounties
        else kenyaCounties.filter { it.name.contains(searchQuery, ignoreCase = true) || it.region.contains(searchQuery, ignoreCase = true) }
    }

    val displayedMechanics = remember(selectedCounty, selectedFilter) {
        var list = if (selectedCounty == null) sampleMechanics
        else sampleMechanics.filter { it.county.equals(selectedCounty!!.name.substringBefore(" ("), ignoreCase = true) }
        if (selectedFilter != "All") list = list.filter { it.specialties.contains(selectedFilter) }
        list
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ── Hero Header ──────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1A0A00), AccentOrange.copy(alpha = 0.25f), BgDark)
                            )
                        )
                ) {
                    // Decorative circles
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .offset(x = (-40).dp, y = (-30).dp)
                            .background(AccentOrange.copy(alpha = 0.07f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 30.dp, y = 10.dp)
                            .background(AccentAmber.copy(alpha = 0.06f), CircleShape)
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 20.dp, bottom = 20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Build,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "MECHANIC FINDER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 3.sp,
                                color = AccentOrange
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Find Trusted\nMechanics in Kenya",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            lineHeight = 32.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${kenyaCounties.size} locations · ${kenyaCounties.sumOf { it.mechanicCount }}+ registered mechanics",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // ── Search Bar ───────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp))
                        .background(SurfaceDark, RoundedCornerShape(14.dp))
                        .border(1.dp, DividerColor, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 15.sp),
                            cursorBrush = SolidColor(AccentOrange),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) Text("Search county or region…", color = TextSecondary, fontSize = 15.sp)
                                inner()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { searchQuery = "" }
                            )
                        }
                    }
                }
            }

            // ── Region Stats Row ─────────────────────────────────────────
            item {
                val regions = kenyaCounties.groupBy { it.region }
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    regions.forEach { (region, counties) ->
                        RegionChip(
                            region = region,
                            count = counties.sumOf { it.mechanicCount }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Section Title ────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(4.dp, 18.dp).background(AccentOrange, RoundedCornerShape(2.dp)))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (searchQuery.isBlank()) "All Locations (${kenyaCounties.size})"
                        else "Results for \"$searchQuery\" (${filteredCounties.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── County Cards ─────────────────────────────────────────────
            items(filteredCounties.chunked(2)) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { county ->
                        CountyCard(
                            county = county,
                            isSelected = selectedCounty == county,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedCounty = if (selectedCounty == county) null else county
                                showCountySheet = selectedCounty != null
                            }
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            // ── Mechanic Section ─────────────────────────────────────────
            item {
                AnimatedVisibility(
                    visible = selectedCounty != null,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut()
                ) {
                    Column {
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(4.dp, 18.dp).background(AccentAmber, RoundedCornerShape(2.dp)))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Mechanics in ${selectedCounty?.name?.substringBefore(" (")}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        // Specialty filter chips
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            specialtyFilters.forEach { filter ->
                                FilterChip(
                                    selected = selectedFilter == filter,
                                    onClick = { selectedFilter = filter },
                                    label = { Text(filter, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentOrange,
                                        selectedLabelColor = Color.White,
                                        containerColor = SurfaceDark,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = DividerColor,
                                        selectedBorderColor = AccentOrange,
                                        enabled = true,
                                        selected = selectedFilter == filter
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            if (selectedCounty != null) {
                if (displayedMechanics.isEmpty()) {
                    item {
                        EmptyMechanicsState(
                            county = selectedCounty!!.name.substringBefore(" ("),
                            filter = selectedFilter
                        )
                    }
                } else {
                    items(displayedMechanics) { mechanic ->
                        MechanicCard(
                            mechanic = mechanic,
                            isSelected = selectedMechanic == mechanic,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                            onClick = { selectedMechanic = if (selectedMechanic == mechanic) null else mechanic }
                        )
                    }
                }
            }
        }

        // ── FAB ──────────────────────────────────────────────────────────
        FloatingActionButton(
            onClick = { /* open map */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = AccentOrange,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(6.dp))
                Text("Map View", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

// ── Sub-components ───────────────────────────────────────────────────────────

@Composable
private fun RegionChip(region: String, count: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(AccentOrange, CircleShape)
            )
            Spacer(Modifier.width(6.dp))
            Text(region, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(4.dp))
            Text("$count", fontSize = 11.sp, color = AccentAmber, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CountyCard(
    county: KenyaCounty,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        if (isSelected) AccentOrange else DividerColor,
        animationSpec = tween(200), label = "border"
    )
    val elevation by animateDpAsState(
        if (isSelected) 12.dp else 2.dp,
        animationSpec = tween(200), label = "elevation"
    )

    Surface(
        modifier = modifier
            .shadow(elevation, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) CardDark else SurfaceDark,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = if (isSelected) AccentOrange else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AccentOrange.copy(alpha = if (isSelected) 0.25f else 0.12f)
                ) {
                    Text(
                        "${county.mechanicCount}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                county.name.substringBefore(" ("),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (county.name.contains("(")) {
                Text(
                    county.name.substringAfter("(").removeSuffix(")"),
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(county.region, fontSize = 10.sp, color = AccentAmber)
        }
    }
}

@Composable
private fun MechanicCard(
    mechanic: Mechanic,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) CardDark else SurfaceDark,
        border = BorderStroke(1.dp, if (isSelected) AccentOrange else DividerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar + Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.linearGradient(listOf(AccentOrange, AccentAmber)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            mechanic.name.split(" ").take(2).joinToString("") { it.first().uppercase() },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(mechanic.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(mechanic.shop, fontSize = 11.sp, color = TextSecondary)
                    }
                }

                // Availability badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (mechanic.isAvailable) OnlineGreen.copy(alpha = 0.15f) else BusyRed.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(6.dp)
                                .background(if (mechanic.isAvailable) OnlineGreen else BusyRed, CircleShape)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            if (mechanic.isAvailable) "Available" else "Busy",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (mechanic.isAvailable) OnlineGreen else BusyRed
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Divider(color = DividerColor, thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(Icons.Filled.Star, "${mechanic.rating}", "Rating", StarYellow)
                StatItem(Icons.Outlined.ThumbUp, "${mechanic.reviewCount}", "Reviews", AccentAmber)
                StatItem(Icons.Filled.LocationOn, mechanic.distance, "Distance", AccentOrange)
                StatItem(Icons.Outlined.DateRange, "${mechanic.yearsExp} yrs", "Exp.", TextSecondary)
            }

            Spacer(Modifier.height(10.dp))

            // Specialty chips
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                mechanic.specialties.forEach { spec ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentOrange.copy(alpha = 0.12f),
                        border = BorderStroke(0.5.dp, AccentOrange.copy(alpha = 0.3f))
                    ) {
                        Text(
                            spec,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            color = AccentOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Expanded details
            AnimatedVisibility(visible = isSelected) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Divider(color = DividerColor, thickness = 0.5.dp)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { /* call */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Call", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { /* book */ },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, AccentOrange),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentOrange)
                        ) {
                            Icon(Icons.Outlined.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Book", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        mechanic.phone,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String, iconTint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(15.dp))
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(label, fontSize = 9.sp, color = TextSecondary)
    }
}

@Composable
private fun EmptyMechanicsState(county: String, filter: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.Build,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "No mechanics found",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            if (filter == "All") "No mechanics registered in $county yet."
            else "No $filter specialists in $county. Try a different filter.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun MechanicFinderScreenPreview() {
    MechanicFinderScreen(rememberNavController())
}