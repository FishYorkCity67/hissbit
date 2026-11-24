plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21" apply false
    id("com.chaquo.python") version "17.1.0" apply false
}

// Build-Ausgaben liegen außerhalb des OneDrive-Ordners, damit nicht Tausende
// Zwischendateien in die Cloud synchronisiert werden.
val externalBuildRoot: File? = System.getenv("LOCALAPPDATA")?.let { File(it, "PyQuestBuild") }
allprojects {
    if (externalBuildRoot != null) {
        layout.buildDirectory.set(File(externalBuildRoot, project.name))
    }
}
