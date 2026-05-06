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

// ── All 47 Kenya Counties ────────────────────────────────────────────────────
private val kenyaCounties = listOf(
    KenyaCounty("Nairobi",                "Central",       148),
    KenyaCounty("Mombasa",               "Coast",          87),
    KenyaCounty("Kwale",                 "Coast",          15),
    KenyaCounty("Kilifi",                "Coast",          29),
    KenyaCounty("Tana River",            "Coast",           9),
    KenyaCounty("Lamu",                  "Coast",          11),
    KenyaCounty("Taita Taveta",          "Coast",          17),
    KenyaCounty("Garissa",               "North Eastern",  18),
    KenyaCounty("Wajir",                 "North Eastern",   7),
    KenyaCounty("Mandera",               "North Eastern",   8),
    KenyaCounty("Marsabit",              "Eastern",         6),
    KenyaCounty("Isiolo",                "Eastern",        14),
    KenyaCounty("Meru",                  "Eastern",        35),
    KenyaCounty("Tharaka-Nithi",         "Eastern",        11),
    KenyaCounty("Embu",                  "Eastern",        24),
    KenyaCounty("Kitui",                 "Eastern",        19),
    KenyaCounty("Machakos",              "Eastern",        41),
    KenyaCounty("Makueni",               "Eastern",        21),
    KenyaCounty("Nyandarua",             "Central",        15),
    KenyaCounty("Nyeri",                 "Central",        38),
    KenyaCounty("Kirinyaga",             "Central",        19),
    KenyaCounty("Murang'a",              "Central",        28),
    KenyaCounty("Kiambu",                "Central",        59),
    KenyaCounty("Turkana",               "Rift Valley",     6),
    KenyaCounty("West Pokot",            "Rift Valley",     7),
    KenyaCounty("Samburu",               "Rift Valley",     5),
    KenyaCounty("Trans Nzoia",           "Rift Valley",    18),
    KenyaCounty("Uasin Gishu",           "Rift Valley",    52),
    KenyaCounty("Elgeyo-Marakwet",       "Rift Valley",    10),
    KenyaCounty("Nandi",                 "Rift Valley",    14),
    KenyaCounty("Baringo",               "Rift Valley",    12),
    KenyaCounty("Laikipia",              "Rift Valley",    21),
    KenyaCounty("Nakuru",                "Rift Valley",    74),
    KenyaCounty("Narok",                 "Rift Valley",    16),
    KenyaCounty("Kajiado",               "Rift Valley",    22),
    KenyaCounty("Kericho",               "Rift Valley",    27),
    KenyaCounty("Bomet",                 "Rift Valley",    16),
    KenyaCounty("Kakamega",              "Western",        44),
    KenyaCounty("Vihiga",                "Western",        13),
    KenyaCounty("Bungoma",               "Western",        33),
    KenyaCounty("Busia",                 "Western",        16),
    KenyaCounty("Siaya",                 "Nyanza",         17),
    KenyaCounty("Kisumu",                "Nyanza",         63),
    KenyaCounty("Homa Bay",              "Nyanza",         23),
    KenyaCounty("Migori",                "Nyanza",         20),
    KenyaCounty("Kisii",                 "Nyanza",         31),
    KenyaCounty("Nyamira",               "Nyanza",         14)
)

// ── Mechanics per county (all 47 counties) ───────────────────────────────────
private val mechanicsByCounty: Map<String, List<Mechanic>> = mapOf(

    "Nairobi" to listOf(
        Mechanic(101, "James Mwangi",    "AutoFix Garage",           "Nairobi", listOf("Engine","Brakes","Electrical"),      4.8f, 212, "1.2 km", true,  12, "+254 712 345678"),
        Mechanic(102, "Esther Njoki",    "Njoki Auto Solutions",     "Nairobi", listOf("Diagnostics","AC","Electrical"),     4.7f, 165, "0.9 km", true,   8, "+254 714 112233"),
        Mechanic(103, "Kevin Kariuki",   "Westlands Motors",         "Nairobi", listOf("Engine","Transmission","Suspension"),4.6f, 198, "2.1 km", false, 11, "+254 720 223344"),
        Mechanic(104, "Faith Achieng",   "South B Auto Works",       "Nairobi", listOf("Bodywork","Painting","Brakes"),      4.5f, 134, "3.4 km", true,   6, "+254 722 334455"),
        Mechanic(105, "Brian Njoroge",   "Eastleigh Garage Hub",     "Nairobi", listOf("Diesel","4WD","Engine"),             4.9f, 287, "1.7 km", true,  15, "+254 733 445566")
    ),

    "Mombasa" to listOf(
        Mechanic(201, "Peter Otieno",    "Coast Auto Works",         "Mombasa", listOf("Transmission","AC","Bodywork"),      4.6f, 178, "0.8 km", true,   9, "+254 722 456789"),
        Mechanic(202, "Ali Hassan",      "Mombasa Express Fix",      "Mombasa", listOf("Bodywork","Painting"),               4.3f,  76, "4.5 km", false,  5, "+254 745 890123"),
        Mechanic(203, "Fatuma Said",     "Tudor Road Garage",        "Mombasa", listOf("Engine","Brakes","Electrical"),      4.7f, 143, "1.3 km", true,  10, "+254 711 567890"),
        Mechanic(204, "Hussein Omar",    "Nyali Auto Centre",        "Mombasa", listOf("AC","Diagnostics","Suspension"),     4.5f,  98, "2.6 km", true,   7, "+254 700 678901")
    ),

    "Kwale" to listOf(
        Mechanic(301, "Hamisi Mwamburi", "Kwale Garage",             "Kwale",   listOf("Engine","Brakes"),                  4.2f,  44, "1.5 km", true,   6, "+254 712 100001"),
        Mechanic(302, "Rehema Charo",    "Ukunda Auto Works",        "Kwale",   listOf("Electrical","AC"),                  4.4f,  61, "2.8 km", false,  4, "+254 733 100002")
    ),

    "Kilifi" to listOf(
        Mechanic(401, "Kazungu Masha",   "Kilifi Motors",            "Kilifi",  listOf("Engine","Suspension","4WD"),         4.5f,  89, "1.1 km", true,   8, "+254 722 200001"),
        Mechanic(402, "Zawadi Karisa",   "Malindi Road Garage",      "Kilifi",  listOf("Brakes","Bodywork","Painting"),      4.3f,  57, "3.2 km", true,   5, "+254 700 200002"),
        Mechanic(403, "Kitsao Ngowa",    "Watamu Auto Fix",          "Kilifi",  listOf("Diesel","Transmission"),             4.6f,  72, "0.7 km", false,  9, "+254 711 200003")
    ),

    "Tana River" to listOf(
        Mechanic(501, "Galgalo Abdi",    "Hola Garage",              "Tana River", listOf("Engine","Brakes"),               4.0f,  23, "2.3 km", true,   5, "+254 712 300001"),
        Mechanic(502, "Mwenda Karisa",   "Garsen Auto Works",        "Tana River", listOf("Diesel","4WD"),                  4.2f,  31, "4.1 km", false,  7, "+254 733 300002")
    ),

    "Lamu" to listOf(
        Mechanic(601, "Bakar Shehe",     "Lamu Garage",              "Lamu",    listOf("Engine","Electrical"),               4.1f,  29, "0.6 km", true,   4, "+254 700 400001"),
        Mechanic(602, "Mwana Fatuma",    "Mokowe Motors",            "Lamu",    listOf("Brakes","AC"),                      4.3f,  38, "1.9 km", true,   6, "+254 722 400002")
    ),

    "Taita Taveta" to listOf(
        Mechanic(701, "Mwakio Mghanga",  "Voi Auto Centre",          "Taita Taveta", listOf("Engine","Diesel","4WD"),        4.4f,  55, "1.4 km", true,   8, "+254 711 500001"),
        Mechanic(702, "Lazaro Mwadime",  "Wundanyi Garage",          "Taita Taveta", listOf("Brakes","Suspension"),         4.2f,  42, "3.0 km", false,  5, "+254 712 500002")
    ),

    "Garissa" to listOf(
        Mechanic(801, "Abdi Yusuf",      "Garissa Motors",           "Garissa", listOf("Engine","Diesel","4WD"),            4.3f,  61, "1.0 km", true,   9, "+254 722 600001"),
        Mechanic(802, "Maalim Hassan",   "Riverside Garage",         "Garissa", listOf("Brakes","Electrical"),              4.1f,  38, "2.7 km", true,   5, "+254 733 600002"),
        Mechanic(803, "Farah Omar",      "Garissa Auto Fix",         "Garissa", listOf("Transmission","Suspension"),        4.4f,  47, "0.9 km", false,  7, "+254 700 600003")
    ),

    "Wajir" to listOf(
        Mechanic(901, "Noor Ahmed",      "Wajir Garage",             "Wajir",   listOf("Engine","Diesel"),                  4.0f,  19, "1.5 km", true,   4, "+254 712 700001"),
        Mechanic(902, "Ibrahim Abdi",    "North-East Motors",        "Wajir",   listOf("4WD","Brakes"),                     4.2f,  24, "3.3 km", false,  6, "+254 711 700002")
    ),

    "Mandera" to listOf(
        Mechanic(1001, "Daud Khalif",    "Mandera Auto Works",       "Mandera", listOf("Engine","Diesel","4WD"),            4.1f,  21, "0.8 km", true,   5, "+254 722 800001"),
        Mechanic(1002, "Hassan Muhumed", "Border Garage",            "Mandera", listOf("Brakes","Electrical"),              4.0f,  17, "2.5 km", false,  4, "+254 700 800002")
    ),

    "Marsabit" to listOf(
        Mechanic(1101, "Godana Guyo",    "Marsabit Garage",          "Marsabit",listOf("Engine","4WD","Diesel"),            4.2f,  18, "1.2 km", true,   6, "+254 712 900001"),
        Mechanic(1102, "Boru Jarso",     "Highland Motors",          "Marsabit",listOf("Suspension","Brakes"),              4.0f,  14, "3.6 km", false,  4, "+254 733 900002")
    ),

    "Isiolo" to listOf(
        Mechanic(1201, "Adan Wario",     "Isiolo Auto Centre",       "Isiolo",  listOf("Engine","4WD","Diesel"),            4.3f,  37, "0.9 km", true,   7, "+254 722 010001"),
        Mechanic(1202, "Liban Huka",     "Buffalo Garage",           "Isiolo",  listOf("Brakes","Electrical","Suspension"), 4.4f,  44, "1.8 km", true,   5, "+254 711 010002"),
        Mechanic(1203, "Fatuma Guyo",    "Merille Motors",           "Isiolo",  listOf("Transmission","AC"),                4.1f,  29, "4.0 km", false,  4, "+254 700 010003")
    ),

    "Meru" to listOf(
        Mechanic(1301, "Murungi Kiambi", "Meru Garage & Auto",       "Meru",    listOf("Engine","Diagnostics","Electrical"),4.7f, 112, "1.0 km", true,  10, "+254 712 020001"),
        Mechanic(1302, "Kirimi Njiru",   "Nkubu Auto Works",         "Meru",    listOf("Brakes","Suspension","4WD"),        4.5f,  88, "2.3 km", true,   8, "+254 733 020002"),
        Mechanic(1303, "Mwenda Ntiba",   "Makutano Motors",          "Meru",    listOf("Transmission","Diesel"),            4.6f,  97, "0.5 km", false,  9, "+254 722 020003"),
        Mechanic(1304, "Kagwiria Muita", "Mount Kenya Auto",         "Meru",    listOf("Engine","AC","Bodywork"),           4.4f,  74, "3.1 km", true,   6, "+254 700 020004")
    ),

    "Tharaka-Nithi" to listOf(
        Mechanic(1401, "Mugambi Gitonga","Chuka Garage",             "Tharaka-Nithi", listOf("Engine","Brakes"),           4.3f,  41, "1.4 km", true,   6, "+254 712 030001"),
        Mechanic(1402, "Karimi Muriuki", "Marimanti Auto Works",     "Tharaka-Nithi", listOf("Electrical","Suspension"),   4.2f,  33, "3.7 km", false,  4, "+254 711 030002")
    ),

    "Embu" to listOf(
        Mechanic(1501, "Kariuki Ndung'u","Embu Auto Centre",         "Embu",    listOf("Engine","Diagnostics","Brakes"),   4.6f,  93, "0.8 km", true,   9, "+254 722 040001"),
        Mechanic(1502, "Wanjiru Mugo",   "Runyenjes Garage",         "Embu",    listOf("Electrical","AC","Transmission"),  4.4f,  67, "2.2 km", true,   7, "+254 712 040002"),
        Mechanic(1503, "Gitonga Kariuki","Mount Kenya Garage",        "Embu",    listOf("Diesel","4WD","Suspension"),       4.5f,  78, "1.5 km", false,  8, "+254 733 040003")
    ),

    "Kitui" to listOf(
        Mechanic(1601, "Mutua Kimanzi",  "Kitui Motors",             "Kitui",   listOf("Engine","Diesel","4WD"),           4.4f,  58, "1.3 km", true,   8, "+254 712 050001"),
        Mechanic(1602, "Ndambuki Mweni", "Mwingi Auto Works",        "Kitui",   listOf("Brakes","Suspension","Electrical"),4.3f,  49, "2.9 km", true,   6, "+254 700 050002"),
        Mechanic(1603, "Syovata Mutua",  "Zombe Garage",             "Kitui",   listOf("Bodywork","Painting"),             4.2f,  37, "4.4 km", false,  4, "+254 733 050003")
    ),

    "Machakos" to listOf(
        Mechanic(1701, "Mutuku Musyoka", "Machakos Garage",          "Machakos",listOf("Engine","Brakes","Diagnostics"),   4.7f, 134, "0.7 km", true,  11, "+254 722 060001"),
        Mechanic(1702, "Ndinda Mwololo", "Athi River Motors",        "Machakos",listOf("Electrical","Transmission","AC"),  4.5f, 102, "1.8 km", true,   8, "+254 712 060002"),
        Mechanic(1703, "Mutie Mutuku",   "Masinga Road Garage",      "Machakos",listOf("Diesel","4WD","Suspension"),       4.6f,  89, "2.4 km", false,  9, "+254 700 060003"),
        Mechanic(1704, "Kavata Mutua",   "Wote Auto Centre",         "Machakos",listOf("Bodywork","Painting","Brakes"),    4.4f,  73, "3.1 km", true,   6, "+254 711 060004")
    ),

    "Makueni" to listOf(
        Mechanic(1801, "Muthama Kioko",  "Wote Garage",              "Makueni", listOf("Engine","Diesel","4WD"),           4.4f,  63, "1.0 km", true,   8, "+254 722 070001"),
        Mechanic(1802, "Nduku Mutua",    "Sultan Hamud Motors",      "Makueni", listOf("Brakes","Electrical","Suspension"),4.3f,  51, "2.6 km", true,   6, "+254 712 070002"),
        Mechanic(1803, "Muema Kivutha",  "Emali Auto Works",         "Makueni", listOf("Transmission","AC"),               4.5f,  72, "0.4 km", false,  7, "+254 733 070003")
    ),

    "Nyandarua" to listOf(
        Mechanic(1901, "Kihara Wambu",   "Ol Kalou Garage",          "Nyandarua",listOf("Engine","Brakes","4WD"),          4.3f,  48, "1.6 km", true,   7, "+254 712 080001"),
        Mechanic(1902, "Wanjiku Mwangi", "Ndaragwa Motors",          "Nyandarua",listOf("Electrical","Diagnostics"),       4.2f,  36, "3.3 km", false,  5, "+254 700 080002")
    ),

    "Nyeri" to listOf(
        Mechanic(2001, "Gitau Waweru",   "Nyeri Auto Centre",        "Nyeri",   listOf("Engine","Diagnostics","Brakes"),   4.7f, 118, "0.9 km", true,  11, "+254 722 090001"),
        Mechanic(2002, "Kamau Gichuki",  "Karatina Motors",          "Nyeri",   listOf("Electrical","Transmission","AC"),  4.6f,  96, "1.7 km", true,   9, "+254 712 090002"),
        Mechanic(2003, "Wanjiru Mwangi", "Othaya Garage",            "Nyeri",   listOf("Diesel","4WD","Suspension"),       4.5f,  83, "2.5 km", false,  8, "+254 733 090003"),
        Mechanic(2004, "Ndung'u Githinji","Mukurwe-ini Auto Works",  "Nyeri",   listOf("Bodywork","Painting"),             4.4f,  62, "3.8 km", true,   6, "+254 700 090004")
    ),

    "Kirinyaga" to listOf(
        Mechanic(2101, "Kiragu Mwangi",  "Kerugoya Garage",          "Kirinyaga",listOf("Engine","Brakes","Electrical"),   4.6f,  82, "1.1 km", true,   9, "+254 712 100001"),
        Mechanic(2102, "Wangui Kamau",   "Kutus Auto Works",         "Kirinyaga",listOf("Diagnostics","AC","Transmission"),4.5f,  67, "2.3 km", true,   7, "+254 722 100002"),
        Mechanic(2103, "Gitonga Kiragu", "Sagana Motors",            "Kirinyaga",listOf("Diesel","4WD","Suspension"),      4.4f,  54, "0.6 km", false,  6, "+254 700 100003")
    ),

    "Murang'a" to listOf(
        Mechanic(2201, "Mwangi Kimani",  "Murang'a Auto Centre",     "Murang'a",listOf("Engine","Brakes","Diagnostics"),   4.6f,  91, "0.8 km", true,  10, "+254 712 110001"),
        Mechanic(2202, "Gatheru Kamau",  "Kangema Garage",           "Murang'a",listOf("Electrical","Transmission"),       4.5f,  74, "2.0 km", true,   8, "+254 722 110002"),
        Mechanic(2203, "Njoki Waweru",   "Maragua Motors",           "Murang'a",listOf("Diesel","4WD","Suspension"),       4.4f,  59, "3.4 km", false,  6, "+254 733 110003")
    ),

    "Kiambu" to listOf(
        Mechanic(2301, "Grace Wanjiku",  "Thika Road Garage",        "Kiambu",  listOf("Electrical","Diagnostics"),        4.7f, 143, "3.1 km", true,   7, "+254 700 678901"),
        Mechanic(2302, "Duncan Kamau",   "Kiambu Auto Works",        "Kiambu",  listOf("Engine","Brakes","Transmission"),  4.8f, 176, "1.0 km", true,  13, "+254 712 120002"),
        Mechanic(2303, "Peninah Njoroge","Ruiru Garage",             "Kiambu",  listOf("Bodywork","Painting","AC"),        4.5f, 108, "2.2 km", false,  7, "+254 722 120003"),
        Mechanic(2304, "Joseph Kariuki", "Limuru Motors",            "Kiambu",  listOf("Diesel","4WD","Engine"),           4.6f,  92, "4.5 km", true,   9, "+254 733 120004"),
        Mechanic(2305, "Nyambura Gitau", "Gatundu Auto Centre",      "Kiambu",  listOf("Electrical","Diagnostics","Brakes"),4.7f, 121, "1.6 km", true,  10, "+254 700 120005")
    ),

    "Turkana" to listOf(
        Mechanic(2401, "Lokwawi Ekai",   "Lodwar Garage",            "Turkana", listOf("Engine","Diesel","4WD"),           4.1f,  17, "1.3 km", true,   5, "+254 712 130001"),
        Mechanic(2402, "Nakibo Lorus",   "Turkana Motors",           "Turkana", listOf("Brakes","Suspension"),             4.0f,  14, "3.0 km", false,  4, "+254 722 130002")
    ),

    "West Pokot" to listOf(
        Mechanic(2501, "Pkemoi Chepkwony","Kapenguria Garage",       "West Pokot",listOf("Engine","4WD","Diesel"),         4.1f,  19, "1.0 km", true,   5, "+254 712 140001"),
        Mechanic(2502, "Cheptoo Lotim",  "Sigor Auto Works",         "West Pokot",listOf("Brakes","Suspension"),           4.0f,  15, "4.2 km", false,  4, "+254 700 140002")
    ),

    "Samburu" to listOf(
        Mechanic(2601, "Lesorogol Lekuta","Maralal Garage",          "Samburu", listOf("Engine","4WD","Diesel"),           4.2f,  13, "0.9 km", true,   5, "+254 712 150001"),
        Mechanic(2602, "Lekalaile Leseku","Baragoi Motors",          "Samburu", listOf("Brakes","Suspension"),             4.0f,  10, "5.1 km", false,  3, "+254 733 150002")
    ),

    "Trans Nzoia" to listOf(
        Mechanic(2701, "Wekesa Simiyu",  "Kitale Auto Centre",       "Trans Nzoia",listOf("Engine","Diesel","Brakes"),     4.5f,  67, "0.8 km", true,   8, "+254 712 160001"),
        Mechanic(2702, "Nafula Wafula",  "Endebess Garage",          "Trans Nzoia",listOf("Electrical","AC","Diagnostics"),4.4f,  54, "2.4 km", true,   6, "+254 722 160002"),
        Mechanic(2703, "Kipkoech Chebii","Kiminini Motors",          "Trans Nzoia",listOf("Transmission","4WD","Suspension"),4.3f, 42, "3.9 km", false,  5, "+254 700 160003")
    ),

    "Uasin Gishu" to listOf(
        Mechanic(2801, "Kipchirchir Rono","Eldoret Auto Works",      "Uasin Gishu",listOf("Engine","Brakes","Diagnostics"),4.8f, 189, "0.6 km", true,  13, "+254 712 170001"),
        Mechanic(2802, "Cherotich Bett",  "Pioneer Garage",          "Uasin Gishu",listOf("Electrical","Transmission","AC"),4.6f, 142, "1.9 km", true,  10, "+254 722 170002"),
        Mechanic(2803, "Kiptoo Koech",    "Turbo Motors",            "Uasin Gishu",listOf("Diesel","4WD","Suspension"),   4.7f, 163, "2.7 km", false, 11, "+254 733 170003"),
        Mechanic(2804, "Jepkoech Tuwei",  "Huruma Garage",           "Uasin Gishu",listOf("Bodywork","Painting","Brakes"),4.5f, 118, "3.5 km", true,   8, "+254 700 170004")
    ),

    "Elgeyo-Marakwet" to listOf(
        Mechanic(2901, "Cheruiyot Sang",  "Iten Garage",             "Elgeyo-Marakwet",listOf("Engine","Brakes","4WD"),   4.3f,  39, "1.2 km", true,   6, "+254 712 180001"),
        Mechanic(2902, "Kiplagat Chebet", "Eldoret Road Motors",     "Elgeyo-Marakwet",listOf("Diesel","Suspension"),     4.2f,  31, "3.7 km", false,  5, "+254 711 180002")
    ),

    "Nandi" to listOf(
        Mechanic(3001, "Rotich Chepkurui","Kapsabet Garage",         "Nandi",   listOf("Engine","Diesel","Brakes"),        4.4f,  52, "0.9 km", true,   7, "+254 712 190001"),
        Mechanic(3002, "Chelimo Kosgey",  "Nandi Hills Auto Works",  "Nandi",   listOf("Electrical","AC","Diagnostics"),  4.3f,  44, "2.3 km", true,   6, "+254 722 190002"),
        Mechanic(3003, "Lagat Mutai",     "Mosoriot Motors",         "Nandi",   listOf("Transmission","Suspension"),      4.2f,  36, "4.6 km", false,  4, "+254 700 190003")
    ),

    "Baringo" to listOf(
        Mechanic(3101, "Tirop Koross",    "Kabarnet Garage",         "Baringo",  listOf("Engine","4WD","Diesel"),          4.3f,  41, "1.0 km", true,   7, "+254 712 200001"),
        Mechanic(3102, "Chebet Kemoi",    "Eldama Ravine Motors",    "Baringo",  listOf("Brakes","Electrical","Suspension"),4.2f, 33, "2.8 km", false,  5, "+254 722 200002")
    ),

    "Laikipia" to listOf(
        Mechanic(3201, "Kimani Waweru",   "Nanyuki Auto Centre",     "Laikipia", listOf("Engine","Diagnostics","Brakes"),  4.6f,  78, "0.8 km", true,   9, "+254 712 210001"),
        Mechanic(3202, "Wanjiku Kariuki", "Rumuruti Garage",         "Laikipia", listOf("Electrical","AC","4WD"),          4.4f,  61, "2.6 km", true,   7, "+254 722 210002"),
        Mechanic(3203, "Ndegwa Kamau",    "Doldol Motors",           "Laikipia", listOf("Diesel","Suspension","Transmission"),4.5f, 72, "3.9 km", false,  8, "+254 700 210003")
    ),

    "Nakuru" to listOf(
        Mechanic(3301, "Samuel Kipchoge", "Nakuru Motors",           "Nakuru",   listOf("Diesel","4WD","Suspension"),      4.9f, 305, "2.4 km", false, 16, "+254 733 567890"),
        Mechanic(3302, "Cheruiyot Bett",  "Nakuru Auto Works",       "Nakuru",   listOf("Engine","Brakes","Diagnostics"),  4.7f, 198, "0.7 km", true,  12, "+254 712 220002"),
        Mechanic(3303, "Wanjiku Kamau",   "Naivasha Road Garage",    "Nakuru",   listOf("Electrical","Transmission","AC"), 4.6f, 163, "1.5 km", true,  10, "+254 722 220003"),
        Mechanic(3304, "Rotich Kemboi",   "Gilgil Motors",           "Nakuru",   listOf("Bodywork","Painting","Brakes"),   4.5f, 131, "3.2 km", true,   8, "+254 700 220004"),
        Mechanic(3305, "Faith Njeri",     "Molo Garage",             "Nakuru",   listOf("Engine","4WD","Diesel"),          4.8f, 224, "4.8 km", false, 14, "+254 711 220005")
    ),

    "Narok" to listOf(
        Mechanic(3401, "Saitoti Ronko",   "Narok Auto Centre",       "Narok",   listOf("Engine","4WD","Diesel"),           4.5f,  64, "1.0 km", true,   8, "+254 712 230001"),
        Mechanic(3402, "Nkoitoi Sankale", "Mara Garage",             "Narok",   listOf("Brakes","Suspension","Electrical"),4.4f,  52, "2.5 km", true,   6, "+254 722 230002"),
        Mechanic(3403, "Nompuki Parsitau","Kilgoris Motors",         "Narok",   listOf("Transmission","Bodywork"),         4.3f,  43, "5.3 km", false,  5, "+254 700 230003")
    ),

    "Kajiado" to listOf(
        Mechanic(3501, "Lempurkel Ole Kisio","Kajiado Garage",       "Kajiado", listOf("Engine","Diesel","4WD"),           4.5f,  74, "0.7 km", true,   9, "+254 712 240001"),
        Mechanic(3502, "Nasheri Nkadori", "Ongata Rongai Motors",    "Kajiado", listOf("Brakes","Electrical","AC"),        4.6f,  89, "1.3 km", true,   7, "+254 722 240002"),
        Mechanic(3503, "Kimeli Tipis",    "Ngong Auto Works",        "Kajiado", listOf("Diagnostics","Transmission","Suspension"),4.4f, 61, "2.9 km", false, 6, "+254 733 240003")
    ),

    "Kericho" to listOf(
        Mechanic(3601, "Kipkemboi Rotich","Kericho Auto Centre",     "Kericho",  listOf("Engine","Brakes","Diagnostics"),  4.6f,  94, "0.9 km", true,   9, "+254 712 250001"),
        Mechanic(3602, "Cheruiyot Korir", "Litein Garage",           "Kericho",  listOf("Electrical","Transmission","AC"), 4.5f,  78, "2.2 km", true,   8, "+254 722 250002"),
        Mechanic(3603, "Jeptoo Langat",   "Londiani Motors",         "Kericho",  listOf("Diesel","4WD","Suspension"),      4.7f, 106, "1.5 km", false, 10, "+254 700 250003")
    ),

    "Bomet" to listOf(
        Mechanic(3701, "Sigei Kiplangat", "Bomet Garage",            "Bomet",   listOf("Engine","Brakes","4WD"),           4.3f,  49, "1.1 km", true,   6, "+254 712 260001"),
        Mechanic(3702, "Cheptoo Koech",   "Sotik Auto Works",        "Bomet",   listOf("Diesel","Suspension","Electrical"),4.4f,  58, "2.7 km", false,  7, "+254 722 260002"),
        Mechanic(3703, "Kipkirui Ngetich","Longisa Motors",          "Bomet",   listOf("Transmission","Brakes"),           4.2f,  37, "4.3 km", true,   5, "+254 700 260003")
    ),

    "Kakamega" to listOf(
        Mechanic(3801, "Wafula Simiyu",   "Kakamega Auto Centre",    "Kakamega",listOf("Engine","Brakes","Diagnostics"),   4.6f, 128, "0.8 km", true,  10, "+254 712 270001"),
        Mechanic(3802, "Nafula Khisa",    "Mumias Garage",           "Kakamega",listOf("Electrical","AC","Transmission"),  4.5f, 104, "1.9 km", true,   8, "+254 722 270002"),
        Mechanic(3803, "Barasa Masinde",  "Butere Motors",           "Kakamega",listOf("Diesel","4WD","Suspension"),       4.4f,  86, "3.1 km", false,  7, "+254 733 270003"),
        Mechanic(3804, "Abubakar Wekesa", "Lugari Auto Works",       "Kakamega",listOf("Bodywork","Painting","Brakes"),    4.3f,  71, "4.5 km", true,   6, "+254 700 270004")
    ),

    "Vihiga" to listOf(
        Mechanic(3901, "Ingosi Shitemi",  "Vihiga Garage",           "Vihiga",  listOf("Engine","Brakes","Electrical"),    4.3f,  47, "1.0 km", true,   6, "+254 712 280001"),
        Mechanic(3902, "Khisa Andeso",    "Mbale Auto Works",        "Vihiga",  listOf("Diagnostics","AC"),               4.2f,  38, "2.8 km", false,  4, "+254 722 280002")
    ),

    "Bungoma" to listOf(
        Mechanic(4001, "Wekesa Wafula",   "Bungoma Auto Works",      "Bungoma", listOf("Engine","Brakes","Diagnostics"),   4.5f, 103, "0.7 km", true,   9, "+254 712 290001"),
        Mechanic(4002, "Nasimiyu Masinde","Webuye Garage",           "Bungoma", listOf("Electrical","Transmission","AC"),  4.4f,  82, "1.8 km", true,   7, "+254 722 290002"),
        Mechanic(4003, "Masinde Barasa",  "Kimilili Motors",         "Bungoma", listOf("Diesel","4WD","Suspension"),       4.6f,  95, "2.5 km", false,  8, "+254 733 290003"),
        Mechanic(4004, "Chebet Wangila",  "Sirisia Auto Centre",     "Bungoma", listOf("Bodywork","Painting"),             4.3f,  66, "4.1 km", true,   5, "+254 700 290004")
    ),

    "Busia" to listOf(
        Mechanic(4101, "Odongo Wafula",   "Busia Border Garage",     "Busia",   listOf("Engine","Brakes","Electrical"),    4.4f,  58, "0.6 km", true,   7, "+254 712 300001"),
        Mechanic(4102, "Atieno Auma",     "Malaba Auto Works",       "Busia",   listOf("Diagnostics","AC","Transmission"), 4.3f,  47, "1.7 km", true,   5, "+254 722 300002"),
        Mechanic(4103, "Simiyu Lukhale",  "Nambale Motors",          "Busia",   listOf("Diesel","4WD","Suspension"),       4.2f,  36, "4.0 km", false,  4, "+254 700 300003")
    ),

    "Siaya" to listOf(
        Mechanic(4201, "Odhiambo Otieno", "Siaya Auto Centre",       "Siaya",   listOf("Engine","Brakes","Diagnostics"),   4.4f,  62, "1.0 km", true,   7, "+254 712 310001"),
        Mechanic(4202, "Akinyi Adhiambo", "Ugunja Garage",           "Siaya",   listOf("Electrical","AC","Transmission"),  4.3f,  51, "2.4 km", false,  5, "+254 722 310002"),
        Mechanic(4203, "Ondiek Omondi",   "Bondo Motors",            "Siaya",   listOf("Diesel","Suspension","Bodywork"),  4.5f,  69, "0.9 km", true,   8, "+254 700 310003")
    ),

    "Kisumu" to listOf(
        Mechanic(4301, "David Ochieng",   "Kisumu Auto Centre",      "Kisumu",  listOf("Engine","Brakes"),                 4.5f,  98, "1.9 km", true,  10, "+254 711 789012"),
        Mechanic(4302, "Auma Atieno",     "Kondele Garage",          "Kisumu",  listOf("Electrical","AC","Diagnostics"),   4.6f, 117, "0.6 km", true,   8, "+254 712 320002"),
        Mechanic(4303, "Oloo Odhiambo",   "Mamboleo Motors",         "Kisumu",  listOf("Diesel","4WD","Transmission"),     4.7f, 139, "2.2 km", false, 11, "+254 722 320003"),
        Mechanic(4304, "Ndege Ojwang",    "Kisian Auto Works",       "Kisumu",  listOf("Bodywork","Painting","Brakes"),    4.4f,  83, "3.7 km", true,   6, "+254 733 320004"),
        Mechanic(4305, "Awino Adhiambo",  "Nyamasaria Garage",       "Kisumu",  listOf("Suspension","Engine","Electrical"),4.5f,  96, "1.4 km", true,   9, "+254 700 320005")
    ),

    "Homa Bay" to listOf(
        Mechanic(4401, "Ogada Ochieng",   "Homa Bay Garage",         "Homa Bay",listOf("Engine","Diesel","4WD"),           4.4f,  69, "1.0 km", true,   8, "+254 712 330001"),
        Mechanic(4402, "Otieno Orimba",   "Kendu Bay Motors",        "Homa Bay",listOf("Brakes","Electrical","Suspension"),4.3f,  57, "2.7 km", true,   6, "+254 722 330002"),
        Mechanic(4403, "Awuor Juma",      "Mbita Auto Works",        "Homa Bay",listOf("Transmission","AC"),               4.5f,  74, "0.8 km", false,  7, "+254 700 330003")
    ),

    "Migori" to listOf(
        Mechanic(4501, "Odhiambo Were",   "Migori Auto Centre",      "Migori",  listOf("Engine","Brakes","Diagnostics"),   4.4f,  66, "0.9 km", true,   8, "+254 712 340001"),
        Mechanic(4502, "Akoth Adhiambo",  "Rongo Garage",            "Migori",  listOf("Electrical","AC","Transmission"),  4.3f,  53, "2.1 km", true,   6, "+254 722 340002"),
        Mechanic(4503, "Apiyo Otieno",    "Suna East Motors",        "Migori",  listOf("Diesel","4WD","Suspension"),       4.5f,  71, "3.4 km", false,  7, "+254 700 340003")
    ),

    "Kisii" to listOf(
        Mechanic(4601, "Omari Ondieki",   "Kisii Auto Works",        "Kisii",   listOf("Engine","Brakes","Diagnostics"),   4.5f,  93, "0.8 km", true,   9, "+254 712 350001"),
        Mechanic(4602, "Bosire Nyamweya", "Ogembo Garage",           "Kisii",   listOf("Electrical","Transmission","AC"),  4.4f,  78, "1.9 km", true,   7, "+254 722 350002"),
        Mechanic(4603, "Nyaboke Gekonde", "Suneka Motors",           "Kisii",   listOf("Diesel","4WD","Suspension"),       4.6f, 101, "2.5 km", false,  9, "+254 733 350003"),
        Mechanic(4604, "Onsarigo Kemunto","Keroka Auto Centre",      "Kisii",   listOf("Bodywork","Painting","Brakes"),    4.3f,  64, "4.2 km", true,   5, "+254 700 350004")
    ),

    "Nyamira" to listOf(
        Mechanic(4701, "Mose Omwando",    "Nyamira Garage",          "Nyamira", listOf("Engine","Brakes","Electrical"),    4.3f,  42, "1.1 km", true,   6, "+254 712 360001"),
        Mechanic(4702, "Bochaberi Mokua", "Keroka Road Motors",      "Nyamira", listOf("Diagnostics","Diesel","4WD"),      4.4f,  53, "2.6 km", false,  7, "+254 722 360002"),
        Mechanic(4703, "Kemunto Ongera",  "Manga Auto Works",        "Nyamira", listOf("Transmission","AC","Suspension"),  4.2f,  36, "4.0 km", true,   4, "+254 700 360003")
    )
)

private val specialtyFilters = listOf(
    "All","Engine","Brakes","Electrical","Transmission","AC",
    "Bodywork","Diesel","Suspension","Diagnostics","4WD","Painting"
)

// ── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicFinderScreen(navController: NavController) {

    var searchQuery      by remember { mutableStateOf("") }
    var selectedCounty   by remember { mutableStateOf<KenyaCounty?>(null) }
    var selectedFilter   by remember { mutableStateOf("All") }
    var selectedMechanic by remember { mutableStateOf<Mechanic?>(null) }

    val filteredCounties = remember(searchQuery) {
        if (searchQuery.isBlank()) kenyaCounties
        else kenyaCounties.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.region.contains(searchQuery, ignoreCase = true)
        }
    }

    val displayedMechanics = remember(selectedCounty, selectedFilter) {
        val countyKey = selectedCounty?.name ?: return@remember emptyList()
        val list = mechanicsByCounty[countyKey] ?: emptyList()
        if (selectedFilter == "All") list
        else list.filter { it.specialties.contains(selectedFilter) }
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
                            Icon(Icons.Filled.Build, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("MECHANIC FINDER", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp, color = AccentOrange)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Find Trusted\nMechanics in Kenya", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary, lineHeight = 32.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${kenyaCounties.size} counties · ${kenyaCounties.sumOf { it.mechanicCount }}+ registered mechanics",
                            fontSize = 12.sp, color = TextSecondary
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
                                Icons.Filled.Close, contentDescription = "Clear", tint = TextSecondary,
                                modifier = Modifier.size(18.dp).clickable { searchQuery = "" }
                            )
                        }
                    }
                }
            }

            // ── Region Stats Row ─────────────────────────────────────────
            item {
                val regions = kenyaCounties.groupBy { it.region }
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    regions.forEach { (region, counties) ->
                        RegionChip(region = region, count = counties.sumOf { it.mechanicCount })
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
                        if (searchQuery.isBlank()) "All Counties (${kenyaCounties.size})"
                        else "Results for \"$searchQuery\" (${filteredCounties.size})",
                        fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── County Cards ─────────────────────────────────────────────
            items(filteredCounties.chunked(2)) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { county ->
                        CountyCard(
                            county = county,
                            isSelected = selectedCounty == county,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedCounty = if (selectedCounty == county) null else county
                                selectedFilter = "All"
                                selectedMechanic = null
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
                                "Mechanics in ${selectedCounty?.name}",
                                fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary
                            )
                            Spacer(Modifier.weight(1f))
                            val total = mechanicsByCounty[selectedCounty?.name]?.size ?: 0
                            Surface(shape = RoundedCornerShape(8.dp), color = AccentOrange.copy(alpha = 0.15f)) {
                                Text(
                                    "$total listed",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AccentOrange
                                )
                            }
                        }

                        // Specialty filter chips
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
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
                        EmptyMechanicsState(county = selectedCounty!!.name, filter = selectedFilter)
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
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            containerColor = AccentOrange, contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
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
            Box(modifier = Modifier.size(7.dp).background(AccentOrange, CircleShape))
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
                    Icons.Filled.LocationOn, contentDescription = null,
                    tint = if (isSelected) AccentOrange else TextSecondary, modifier = Modifier.size(16.dp)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AccentOrange.copy(alpha = if (isSelected) 0.25f else 0.12f)
                ) {
                    Text(
                        "${county.mechanicCount}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentOrange
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                county.name, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Brush.linearGradient(listOf(AccentOrange, AccentAmber)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            mechanic.name.split(" ").take(2).joinToString("") { it.first().uppercase() },
                            fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(mechanic.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(mechanic.shop, fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (mechanic.isAvailable) OnlineGreen.copy(alpha = 0.15f) else BusyRed.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(6.dp).background(if (mechanic.isAvailable) OnlineGreen else BusyRed, CircleShape))
                        Spacer(Modifier.width(5.dp))
                        Text(
                            if (mechanic.isAvailable) "Available" else "Busy",
                            fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                            color = if (mechanic.isAvailable) OnlineGreen else BusyRed
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Divider(color = DividerColor, thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem(Icons.Filled.Star,           "${mechanic.rating}",      "Rating",   StarYellow)
                StatItem(Icons.Outlined.ThumbUp,      "${mechanic.reviewCount}", "Reviews",  AccentAmber)
                StatItem(Icons.Filled.LocationOn,     mechanic.distance,         "Distance", AccentOrange)
                StatItem(Icons.Outlined.DateRange,    "${mechanic.yearsExp} yrs","Exp.",     TextSecondary)
            }

            Spacer(Modifier.height(10.dp))

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
                            fontSize = 10.sp, color = AccentOrange, fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

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
                        mechanic.phone, fontSize = 12.sp, color = TextSecondary,
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
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.Build, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text("No mechanics found", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(
            if (filter == "All") "No mechanics registered in $county yet."
            else "No $filter specialists in $county. Try a different filter.",
            fontSize = 13.sp, color = TextSecondary,
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