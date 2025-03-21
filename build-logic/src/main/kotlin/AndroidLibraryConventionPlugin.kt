import com.android.build.gradle.LibraryExtension
import com.loong.android.media.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Android lib约定配置
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")

            configure<LibraryExtension> {
                compileSdk = 35

                defaultConfig {
                    minSdk = 21

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
                configure<KotlinAndroidProjectExtension> {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_11)
                    }
                }
            }

            dependencies {
                "implementation"(libs.findLibrary("androidx.core.ktx").get())
                "implementation"(libs.findLibrary("androidx.appcompat").get())
                "testImplementation"(libs.findLibrary("junit").get())
                "androidTestImplementation"(libs.findLibrary("androidx.junit").get())
                "androidTestImplementation"(libs.findLibrary("androidx.espresso.core").get())
            }
        }
    }
}