load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")

package(
    default_visibility = ["//src:__subpackages__"],
)

kt_android_library(
    name = "example_kotlin_lib",
    srcs = [
        "KotlinLibraryFile.kt",
    ],
    manifest = "AndroidManifest.xml",
    resource_files = glob(["res/**"]),
    deps = [
        "@maven//:io_reactivex_rxjava3_rxjava",
    ],
)
