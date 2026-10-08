package com.example.model

data class TvVirtualApp(
    val id: String,
    val nameEn: String,
    val nameKu: String,
    val category: String,
    val colorHex: Long,
    val iconName: String,
    val description: String
)

data class TvChannel(
    val number: Int,
    val name: String,
    val currentShow: String,
    val category: String
)

data class TvAppState(
    val isPowerOn: Boolean = true,
    val volume: Int = 42,
    val isMuted: Boolean = false,
    val activeAppId: String = "home",
    val cursorX: Float = 0.5f, // Normalized 0..1
    val cursorY: Float = 0.5f,
    val isCursorVisible: Boolean = true,
    val lastClickRippleX: Float = -1f,
    val lastClickRippleY: Float = -1f,
    val lastClickTimestamp: Long = 0L,
    val lastToastMessage: String = "",
    val toastTimestamp: Long = 0L,
    val activeChannelIndex: Int = 0,
    val isMediaPlaying: Boolean = true,
    val mediaProgressSeconds: Int = 145,
    val mediaDurationSeconds: Int = 620,
    val browserUrl: String = "https://kurdish-news.tv",
    val searchTextInput: String = "",
    val connectedClientsCount: Int = 0,
    val connectedDeviceName: String = "",
    val serverIpAddress: String = "192.168.1.105",
    val serverPort: Int = 8989,
    val serverPin: String = "5821"
) {
    val currentChannel: TvChannel
        get() = DEFAULT_CHANNELS.getOrElse(activeChannelIndex) { DEFAULT_CHANNELS[0] }

    companion object {
        val DEFAULT_CHANNELS = listOf(
            TvChannel(1, "Kurdistan TV HD", "هەواڵ و بەرنامەکان", "News"),
            TvChannel(2, "Rudaw News 4K", "رووداوی ئەمڕۆ", "News"),
            TvChannel(3, "Kurdsat Drama", "درامای هەڵبژێردراو", "Drama"),
            TvChannel(4, "K24 News Live", "ڕاپۆرتی بەپەلە", "News"),
            TvChannel(5, "Sports World HD", "یاری کۆتایی چامپیۆنزلیگ", "Sports"),
            TvChannel(6, "Discovery Nature", "نهێنی سروشتی کوردستان", "Documentary"),
            TvChannel(7, "Kids Zone Animation", "کارتۆن و یاری", "Kids")
        )

        val DEFAULT_APPS = listOf(
            TvVirtualApp(
                id = "youtube",
                nameEn = "YouTube",
                nameKu = "یوتیوب",
                category = "Video",
                colorHex = 0xFFFF0000,
                iconName = "youtube",
                description = "پەخشی ڤیدیۆ و بەرنامە بەرزترین کوالێتی"
            ),
            TvVirtualApp(
                id = "netflix",
                nameEn = "Netflix",
                nameKu = "نێتفلیکس",
                category = "Movies",
                colorHex = 0xFFE50914,
                iconName = "netflix",
                description = "فیلم و درامای جیهانی لەگەڵ ژێرنووس"
            ),
            TvVirtualApp(
                id = "live_tv",
                nameEn = "Live TV",
                nameKu = "تیڤی پەخشی ڕاستەوخۆ",
                category = "Channels",
                colorHex = 0xFF0284C7,
                iconName = "tv",
                description = "کەناڵە کوردی و جیهانییەکان بە کوالێتی 4K"
            ),
            TvVirtualApp(
                id = "browser",
                nameEn = "Browser",
                nameKu = "وێبگەڕی ئینتەرنێت",
                category = "Internet",
                colorHex = 0xFF10B981,
                iconName = "browser",
                description = "گەڕان لە ماڵپەڕەکان و سۆشیال میدیا"
            ),
            TvVirtualApp(
                id = "media",
                nameEn = "Media Player",
                nameKu = "پەخشکەری میدیا",
                category = "Local Files",
                colorHex = 0xFF8B5CF6,
                iconName = "media",
                description = "پەخشی وێنە، گۆرانی و ڤیدیۆی مۆبایل"
            ),
            TvVirtualApp(
                id = "settings",
                nameEn = "Settings",
                nameKu = "ڕێکخستنەکانی تیڤی",
                category = "System",
                colorHex = 0xFF64748B,
                iconName = "settings",
                description = "ڕێکخستنی وایفای، کۆنتڕۆڵ و پەیوەندی"
            ),
            TvVirtualApp(
                id = "files",
                nameEn = "File Manager",
                nameKu = "بەڕێوەبەری فایل",
                category = "Tools",
                colorHex = 0xFFF59E0B,
                iconName = "files",
                description = "بینین و ناردنی فایلەکانی مۆبایل بۆ تیڤی"
            ),
            TvVirtualApp(
                id = "app_store",
                nameEn = "App Market",
                nameKu = "کۆگای بەرنامەکان",
                category = "Store",
                colorHex = 0xFF06B6D4,
                iconName = "store",
                description = "داگرتنی نوێترین ئەپەکانی ئەندرۆید تیڤی"
            )
        )
    }
}
