load("//tools/build_rules:genproto.bzl", "java_proto_library")

java_proto_library(
    name = "build_data_proto",
    src = "build_data.proto",
)

java_library(
    name = "builder",
    srcs = ["*.java"],
    deps = [
        ":build_data_proto",
        "//src/main/java/com/google/devtools/build/lib:shell",
        "//third_party:guava",
    ],
)
