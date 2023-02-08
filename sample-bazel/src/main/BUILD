load(":ruler.bzl", "ruler_rule")

android_binary(
    name = "app",
    manifest = "AndroidManifest.xml",
    deps = [
        "//src/main/java/com/example/bazel:greeter_activity",
        "//src/main/java/com/example/lib:example_lib",
        "//src/main/java/com/example/kotlinlib:example_kotlin_lib"
    ],
)

ruler_rule(
    name = 'ruler',
    deps = ['app']
)


# bazel build //src/main:ruler