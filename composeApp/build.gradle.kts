import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    
    alias(libs.plugins.jetbrainsCompose)
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Jewel: a Compose for Desktop theme
            // See https://github.com/JetBrains/Jewel/releases for the release notes
            implementation("org.jetbrains.jewel:jewel-int-ui-standalone-241:0.17.3")
            // Optional, for custom decorated windows:
            implementation("org.jetbrains.jewel:jewel-int-ui-decorated-window-241:0.17.3")
            // Do not bring in Material (we use Jewel)
            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material")
            }

            implementation("io.github.dokar3:sonner:0.3.5")
            
            implementation("com.darkrockstudios:mpfilepicker:3.1.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")

            implementation("io.coil-kt.coil3:coil:3.0.0-alpha06")
            implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha06")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0-alpha06")

            val voyagerVersion = "1.0.0"
            // Multiplatform
            // Navigator
            implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
            // Screen Model
            implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")
            // BottomSheetNavigator
            implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:$voyagerVersion")
            // TabNavigator
            implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
            // Transitions
            implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            nativeDistributions {
                modules("jdk.unsupported")
            }
            packageName = "SuperRPhotoViewer"
            packageVersion = "1.0.0"

//            windows {
//                menu = true
//            }
        }
    }
}
