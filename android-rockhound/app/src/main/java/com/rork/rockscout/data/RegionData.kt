package com.rork.rockscout.data

/**
 * Structured location data for the user profile.
 *
 * The profile location is restricted to **state/province + country** only.
 * Cities and towns are never accepted — if a user wants to disclose a more
 * specific location they can do so through private messages.
 */

object RegionData {

    data class Country(val displayName: String, val shortName: String, val subdivisions: List<String>)

    // ---- Subdivision lists (declared before `countries` so initialization order is valid) ----

    private val usStates = listOf(
        "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
        "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho",
        "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
        "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
        "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada",
        "New Hampshire", "New Jersey", "New Mexico", "New York",
        "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon",
        "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
        "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
        "West Virginia", "Wisconsin", "Wyoming", "District of Columbia",
        "Puerto Rico",
    )

    private val canadianProvinces = listOf(
        "Alberta", "British Columbia", "Manitoba", "New Brunswick",
        "Newfoundland and Labrador", "Northwest Territories", "Nova Scotia",
        "Nunavut", "Ontario", "Prince Edward Island", "Quebec",
        "Saskatchewan", "Yukon",
    )

    private val australianStates = listOf(
        "New South Wales", "Victoria", "Queensland", "Western Australia",
        "South Australia", "Tasmania", "Australian Capital Territory",
        "Northern Territory",
    )

    private val ukRegions = listOf(
        "England", "Scotland", "Wales", "Northern Ireland",
    )

    private val mexicanStates = listOf(
        "Aguascalientes", "Baja California", "Baja California Sur",
        "Campeche", "Chiapas", "Chihuahua", "Coahuila", "Colima",
        "Durango", "Guanajuato", "Guerrero", "Hidalgo", "Jalisco",
        "Mexico City (CDMX)", "Michoacán", "Morelos", "Nayarit",
        "Nuevo León", "Oaxaca", "Puebla", "Querétaro", "Quintana Roo",
        "San Luis Potosí", "Sinaloa", "Sonora", "Tabasco", "Tamaulipas",
        "Tlaxcala", "Veracruz", "Yucatán", "Zacatecas",
    )

    private val germanStates = listOf(
        "Baden-Württemberg", "Bavaria", "Berlin", "Brandenburg", "Bremen",
        "Hamburg", "Hesse", "Lower Saxony", "Mecklenburg-Vorpommern",
        "North Rhine-Westphalia", "Rhineland-Palatinate", "Saarland",
        "Saxony", "Saxony-Anhalt", "Schleswig-Holstein", "Thuringia",
    )

    private val frenchRegions = listOf(
        "Auvergne-Rhône-Alpes", "Bourgogne-Franche-Comté", "Brittany",
        "Centre-Val de Loire", "Corsica", "Grand Est", "Hauts-de-France",
        "Île-de-France", "Normandy", "Nouvelle-Aquitaine", "Occitanie",
        "Pays de la Loire", "Provence-Alpes-Côte d'Azur",
        "Overseas Territories",
    )

    private val italianRegions = listOf(
        "Abruzzo", "Aosta Valley", "Apulia", "Basilicata", "Calabria",
        "Campania", "Emilia-Romagna", "Friuli-Venezia Giulia", "Lazio",
        "Liguria", "Lombardy", "Marche", "Molise", "Piedmont", "Sardinia",
        "Sicily", "Trentino-South Tyrol", "Tuscany", "Umbria",
        "Valle d'Aosta", "Veneto",
    )

    private val spanishRegions = listOf(
        "Andalusia", "Aragón", "Asturias", "Balearic Islands", "Basque Country",
        "Canary Islands", "Cantabria", "Castile and León", "Castilla-La Mancha",
        "Catalonia", "Extremadura", "Galicia", "La Rioja", "Madrid",
        "Murcia", "Navarre", "Valencia",
    )

    private val brazilianStates = listOf(
        "Acre", "Alagoas", "Amapá", "Amazonas", "Bahia", "Ceará",
        "Distrito Federal", "Espírito Santo", "Goiás", "Maranhão",
        "Mato Grosso", "Mato Grosso do Sul", "Minas Gerais", "Pará",
        "Paraíba", "Paraná", "Pernambuco", "Piauí", "Rio de Janeiro",
        "Rio Grande do Norte", "Rio Grande do Sul", "Rondônia", "Roraima",
        "Santa Catarina", "São Paulo", "Sergipe", "Tocantins",
    )

    private val argentineProvinces = listOf(
        "Buenos Aires", "Catamarca", "Chaco", "Chubut", "Córdoba",
        "Corrientes", "Entre Ríos", "Formosa", "Jujuy", "La Pampa",
        "La Rioja", "Mendoza", "Misiones", "Neuquén", "Río Negro",
        "Salta", "San Juan", "San Luis", "Santa Cruz", "Santa Fe",
        "Santiago del Estero", "Tierra del Fuego", "Tucumán",
    )

    private val southAfricanProvinces = listOf(
        "Eastern Cape", "Free State", "Gauteng", "KwaZulu-Natal",
        "Limpopo", "Mpumalanga", "North West", "Northern Cape",
        "Western Cape",
    )

    private val indianStates = listOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
        "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh",
        "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh",
        "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
        "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu",
        "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand",
        "West Bengal", "Delhi", "Jammu and Kashmir",
    )

    private val chineseProvinces = listOf(
        "Anhui", "Beijing", "Chongqing", "Fujian", "Gansu", "Guangdong",
        "Guangxi", "Guizhou", "Hainan", "Hebei", "Heilongjiang",
        "Henan", "Hubei", "Hunan", "Inner Mongolia", "Jiangsu",
        "Jiangxi", "Jilin", "Liaoning", "Ningxia", "Qinghai", "Shaanxi",
        "Shandong", "Shanghai", "Shanxi", "Sichuan", "Tianjin", "Tibet",
        "Xinjiang", "Yunnan", "Zhejiang",
    )

    private val japanesePrefectures = listOf(
        "Hokkaido", "Aomori", "Iwate", "Miyagi", "Akita", "Yamagata",
        "Fukushima", "Ibaraki", "Tochigi", "Gunma", "Saitama", "Chiba",
        "Tokyo", "Kanagawa", "Niigata", "Toyama", "Ishikawa", "Fukui",
        "Yamanashi", "Nagano", "Gifu", "Shizuoka", "Aichi", "Mie", "Shiga",
        "Kyoto", "Osaka", "Hyogo", "Nara", "Wakayama", "Tottori",
        "Shimane", "Okayama", "Hiroshima", "Yamaguchi", "Tokushima",
        "Kagawa", "Ehime", "Kochi", "Fukuoka", "Saga", "Nagasaki",
        "Kumamoto", "Oita", "Miyazaki", "Kagoshima", "Okinawa",
    )

    private val nzRegions = listOf(
        "Northland", "Auckland", "Waikato", "Bay of Plenty", "Gisborne",
        "Hawke's Bay", "Taranaki", "Manawatū-Whanganui", "Wellington",
        "Tasman", "Nelson", "Marlborough", "West Coast", "Canterbury",
        "Otago", "Southland",
    )

    private val irishCounties = listOf(
        "Carlow", "Cavan", "Clare", "Cork", "Donegal", "Dublin", "Galway",
        "Kerry", "Kildare", "Kilkenny", "Laois", "Leitrim", "Limerick",
        "Longford", "Louth", "Mayo", "Meath", "Monaghan", "Offaly",
        "Roscommon", "Sligo", "Tipperary", "Waterford", "Westmeath",
        "Wexford", "Wicklow",
    )

    private val norwegianCounties = listOf(
        "Innlandet", "Agder", "Vestland", "Rogaland", "Vestfold og Telemark",
        "Møre og Romsdal", "Trøndelag", "Nordland", "Troms og Finnmark",
        "Viken", "Oslo",
    )

    private val swedishCounties = listOf(
        "Stockholm", "Västra Götaland", "Skåne", "Uppsala", "Södermanland",
        "Östergötland", "Jönköping", "Kronoberg", "Kalmar", "Gotland",
        "Blekinge", "Halland", "Västerbotten", "Norrbotten", "Värmland",
        "Dalarna", "Gävleborg", "Västernorrland", "Jämtland", "Västmanland",
        "Örebro",
    )

    private val finnishRegions = listOf(
        "Uusimaa", "Southwest Finland", "Satakunta", "Kanta-Häme",
        "Pirkanmaa", "Päijät-Häme", "Kymenlaakso", "South Karelia",
        "Etelä-Savo", "Pohjois-Savo", "North Karelia", "Central Finland",
        "South Ostrobothnia", "Ostrobothnia", "Central Ostrobothnia",
        "North Ostrobothnia", "Kainuu", "Lapland", "Åland",
    )

    private val portugueseDistricts = listOf(
        "Aveiro", "Beja", "Braga", "Bragança", "Castelo Branco", "Coimbra",
        "Évora", "Faro", "Guarda", "Leiria", "Lisbon", "Portalegre",
        "Porto", "Santarém", "Setúbal", "Viana do Castelo", "Vila Real",
        "Viseu", "Azores", "Madeira",
    )

    private val dutchProvinces = listOf(
        "Drenthe", "Flevoland", "Friesland", "Gelderland", "Groningen",
        "Limburg", "North Brabant", "North Holland", "Overijssel",
        "South Holland", "Utrecht", "Zeeland",
    )

    private val swissCantons = listOf(
        "Aargau", "Appenzell Ausserrhoden", "Appenzell Innerrhoden",
        "Basel-Landschaft", "Basel-Stadt", "Bern", "Fribourg", "Geneva",
        "Glarus", "Graubünden", "Jura", "Lucerne", "Neuchâtel", "Nidwalden",
        "Obwalden", "Schaffhausen", "Schwyz", "Solothurn", "St. Gallen",
        "Thurgau", "Ticino", "Uri", "Valais", "Vaud", "Zug", "Zurich",
    )

    private val austrianStates = listOf(
        "Burgenland", "Carinthia", "Lower Austria", "Upper Austria",
        "Salzburg", "Styria", "Tyrol", "Vorarlberg", "Vienna",
    )

    private val polishProvinces = listOf(
        "Greater Poland", "Kuyavian-Pomeranian", "Lesser Poland", "Łódź",
        "Lower Silesian", "Lublin", "Lubusz", "Masovian", "Opole",
        "Podkarpackie", "Podlaskie", "Pomeranian", "Silesian",
        "Świętokrzyskie", "Warmian-Masurian", "West Pomeranian",
    )

    private val czechRegions = listOf(
        "Prague", "Central Bohemia", "South Bohemia", "Plzeň", "Karlovy Vary",
        "Ústí nad Labem", "Liberec", "Hradec Králové", "Pardubice",
        "Vysočina", "South Moravia", "Olomouc", "Zlín", "Moravian-Silesian",
    )

    private val russianRegions = listOf(
        "Moscow", "Saint Petersburg", "Moscow Oblast", "Leningrad Oblast",
        "Krasnodar Krai", "Sverdlovsk Oblast", "Novosibirsk Oblast",
        "Tatarstan", "Bashkortostan", "Primorsky Krai", "Other",
    )

    /** Display name → list of state/province display names. */
    val countries: List<Country> = listOf(
        Country("United States", "USA", usStates),
        Country("Canada", "Canada", canadianProvinces),
        Country("Australia", "Australia", australianStates),
        Country("United Kingdom", "UK", ukRegions),
        Country("Mexico", "Mexico", mexicanStates),
        Country("Germany", "Germany", germanStates),
        Country("France", "France", frenchRegions),
        Country("Italy", "Italy", italianRegions),
        Country("Spain", "Spain", spanishRegions),
        Country("Brazil", "Brazil", brazilianStates),
        Country("Argentina", "Argentina", argentineProvinces),
        Country("South Africa", "South Africa", southAfricanProvinces),
        Country("India", "India", indianStates),
        Country("China", "China", chineseProvinces),
        Country("Japan", "Japan", japanesePrefectures),
        Country("New Zealand", "New Zealand", nzRegions),
        Country("Ireland", "Ireland", irishCounties),
        Country("Norway", "Norway", norwegianCounties),
        Country("Sweden", "Sweden", swedishCounties),
        Country("Finland", "Finland", finnishRegions),
        Country("Portugal", "Portugal", portugueseDistricts),
        Country("Netherlands", "Netherlands", dutchProvinces),
        Country("Switzerland", "Switzerland", swissCantons),
        Country("Austria", "Austria", austrianStates),
        Country("Poland", "Poland", polishProvinces),
        Country("Czech Republic", "Czech Republic", czechRegions),
        Country("Russia", "Russia", russianRegions),
        Country("Other / Not listed", "Other", emptyList()),
    )

    /** Parse a stored "State, Country" or "Country" string back into parts. */
    fun parse(stored: String): Pair<String?, String?> {
        val trimmed = stored.trim()
        if (trimmed.isEmpty()) return null to null
        val comma = trimmed.lastIndexOf(',')
        return if (comma > 0) {
            trimmed.substring(0, comma).trim() to trimmed.substring(comma + 1).trim()
        } else {
            null to trimmed
        }
    }

    /** Build the stored display string from a subdivision + country short name. */
    fun format(subdivision: String?, countryShort: String): String {
        val country = countries.firstOrNull { it.shortName == countryShort }?.displayName ?: countryShort
        return if (subdivision.isNullOrBlank()) country else "$subdivision, $country"
    }
}
