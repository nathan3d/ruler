package(
    default_visibility = ["//src:__subpackages__"],
)

android_library(
    name = "example_lib",
    srcs = [
        "LibraryFile.java",
    ],
    manifest = "AndroidManifest.xml",
    assets = glob(["assets/**"]),
    assets_dir = "assets/",
    resource_files = glob(["res/**"]),
    deps = [
        "@maven//:io_reactivex_rxjava3_rxjava",
    ],
)
