package app.pyquest.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import app.pyquest.data.EditorThemeId

@Immutable
data class EditorTheme(
    val name: String,
    val isDark: Boolean,
    val background: Color,
    val gutter: Color,
    val gutterText: Color,
    val text: Color,
    val keyword: Color,
    val builtin: Color,
    val string: Color,
    val number: Color,
    val comment: Color,
    val function: Color,
    val className: Color,
    val decorator: Color,
    val constant: Color,
    val self: Color,
    val operator: Color,
    val cursor: Color,
    val selection: Color,
)

object EditorThemes {
    // Seit 2.3 in warmen Tönen, passend zum Elfenbein-/Anthrazit-Look der App.
    val MontyLight = EditorTheme(
        name = "Monty Hell", isDark = false,
        background = Color(0xFFF8F6F2), gutter = Color(0xFFF0EDE7), gutterText = Color(0xFFABA499),
        text = Color(0xFF26231F), keyword = Color(0xFFB3315F), builtin = Color(0xFF17728B),
        string = Color(0xFF4E7D2C), number = Color(0xFF7C55C4), comment = Color(0xFF948C80),
        function = Color(0xFF2F67B5), className = Color(0xFFB8611A), decorator = Color(0xFF7C55C4),
        constant = Color(0xFF7C55C4), self = Color(0xFFB8611A), operator = Color(0xFF5E574F),
        cursor = Color(0xFF2F67B5), selection = Color(0x332F67B5),
    )
    val MontyDark = EditorTheme(
        name = "Monty Dunkel", isDark = true,
        background = Color(0xFF1C1A18), gutter = Color(0xFF211F1C), gutterText = Color(0xFF6B655C),
        text = Color(0xFFEAE5DC), keyword = Color(0xFFF28BB1), builtin = Color(0xFF6FD3F0),
        string = Color(0xFFB3DB7E), number = Color(0xFFC9A9F5), comment = Color(0xFF7D766B),
        function = Color(0xFF86BAF5), className = Color(0xFFF5B87A), decorator = Color(0xFFC9A9F5),
        constant = Color(0xFFC9A9F5), self = Color(0xFFF5B87A), operator = Color(0xFFBDB6AA),
        cursor = Color(0xFFE9C46A), selection = Color(0x4486B8F0),
    )
    val Monokai = EditorTheme(
        name = "Monokai", isDark = true,
        background = Color(0xFF272822), gutter = Color(0xFF2F302A), gutterText = Color(0xFF75715E),
        text = Color(0xFFF8F8F2), keyword = Color(0xFFF92672), builtin = Color(0xFF66D9EF),
        string = Color(0xFFE6DB74), number = Color(0xFFAE81FF), comment = Color(0xFF75715E),
        function = Color(0xFFA6E22E), className = Color(0xFFA6E22E), decorator = Color(0xFFA6E22E),
        constant = Color(0xFFAE81FF), self = Color(0xFFFD971F), operator = Color(0xFFF92672),
        cursor = Color(0xFFF8F8F0), selection = Color(0x5549483E),
    )
    val Dracula = EditorTheme(
        name = "Dracula", isDark = true,
        background = Color(0xFF282A36), gutter = Color(0xFF2E303E), gutterText = Color(0xFF6272A4),
        text = Color(0xFFF8F8F2), keyword = Color(0xFFFF79C6), builtin = Color(0xFF8BE9FD),
        string = Color(0xFFF1FA8C), number = Color(0xFFBD93F9), comment = Color(0xFF6272A4),
        function = Color(0xFF50FA7B), className = Color(0xFF8BE9FD), decorator = Color(0xFF50FA7B),
        constant = Color(0xFFBD93F9), self = Color(0xFFFFB86C), operator = Color(0xFFFF79C6),
        cursor = Color(0xFFF8F8F2), selection = Color(0x6644475A),
    )
    val OneDark = EditorTheme(
        name = "One Dark", isDark = true,
        background = Color(0xFF282C34), gutter = Color(0xFF2C313A), gutterText = Color(0xFF5C6370),
        text = Color(0xFFABB2BF), keyword = Color(0xFFC678DD), builtin = Color(0xFF56B6C2),
        string = Color(0xFF98C379), number = Color(0xFFD19A66), comment = Color(0xFF5C6370),
        function = Color(0xFF61AFEF), className = Color(0xFFE5C07B), decorator = Color(0xFF61AFEF),
        constant = Color(0xFFD19A66), self = Color(0xFFE06C75), operator = Color(0xFF56B6C2),
        cursor = Color(0xFF528BFF), selection = Color(0x553E4451),
    )
    val Nord = EditorTheme(
        name = "Nord", isDark = true,
        background = Color(0xFF2E3440), gutter = Color(0xFF323946), gutterText = Color(0xFF616E88),
        text = Color(0xFFD8DEE9), keyword = Color(0xFF81A1C1), builtin = Color(0xFF8FBCBB),
        string = Color(0xFFA3BE8C), number = Color(0xFFB48EAD), comment = Color(0xFF616E88),
        function = Color(0xFF88C0D0), className = Color(0xFF8FBCBB), decorator = Color(0xFFD08770),
        constant = Color(0xFFB48EAD), self = Color(0xFFD08770), operator = Color(0xFF81A1C1),
        cursor = Color(0xFFD8DEE9), selection = Color(0x55434C5E),
    )
    val Gruvbox = EditorTheme(
        name = "Gruvbox", isDark = true,
        background = Color(0xFF282828), gutter = Color(0xFF2E2C2B), gutterText = Color(0xFF7C6F64),
        text = Color(0xFFEBDBB2), keyword = Color(0xFFFB4934), builtin = Color(0xFFFE8019),
        string = Color(0xFFB8BB26), number = Color(0xFFD3869B), comment = Color(0xFF928374),
        function = Color(0xFFFABD2F), className = Color(0xFF8EC07C), decorator = Color(0xFF8EC07C),
        constant = Color(0xFFD3869B), self = Color(0xFF83A598), operator = Color(0xFFEBDBB2),
        cursor = Color(0xFFEBDBB2), selection = Color(0x55504945),
    )
    val Solarized = EditorTheme(
        name = "Solarized", isDark = false,
        background = Color(0xFFFDF6E3), gutter = Color(0xFFF4ECD6), gutterText = Color(0xFF93A1A1),
        text = Color(0xFF586E75), keyword = Color(0xFF859900), builtin = Color(0xFFB58900),
        string = Color(0xFF2AA198), number = Color(0xFFD33682), comment = Color(0xFF93A1A1),
        function = Color(0xFF268BD2), className = Color(0xFFCB4B16), decorator = Color(0xFF6C71C4),
        constant = Color(0xFFD33682), self = Color(0xFFCB4B16), operator = Color(0xFF657B83),
        cursor = Color(0xFF268BD2), selection = Color(0x33268BD2),
    )
    val GitHub = EditorTheme(
        name = "GitHub Hell", isDark = false,
        background = Color(0xFFFFFFFF), gutter = Color(0xFFF6F8FA), gutterText = Color(0xFF8C959F),
        text = Color(0xFF1F2328), keyword = Color(0xFFCF222E), builtin = Color(0xFF953800),
        string = Color(0xFF0A3069), number = Color(0xFF0550AE), comment = Color(0xFF6E7781),
        function = Color(0xFF8250DF), className = Color(0xFF953800), decorator = Color(0xFF8250DF),
        constant = Color(0xFF0550AE), self = Color(0xFF953800), operator = Color(0xFFCF222E),
        cursor = Color(0xFF0969DA), selection = Color(0x330969DA),
    )

    fun resolve(id: EditorThemeId, appDark: Boolean): EditorTheme = when (id) {
        EditorThemeId.AUTO -> if (appDark) MontyDark else MontyLight
        EditorThemeId.MONTY_LIGHT -> MontyLight
        EditorThemeId.MONTY_DARK -> MontyDark
        EditorThemeId.MONOKAI -> Monokai
        EditorThemeId.DRACULA -> Dracula
        EditorThemeId.ONE_DARK -> OneDark
        EditorThemeId.NORD -> Nord
        EditorThemeId.GRUVBOX -> Gruvbox
        EditorThemeId.SOLARIZED -> Solarized
        EditorThemeId.GITHUB -> GitHub
    }
}
