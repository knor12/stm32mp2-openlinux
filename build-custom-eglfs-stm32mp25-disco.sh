#!/bin/bash

# === DEFAULTS ===
DISTRO_VERSION="openstlinux-eglfs"
IMAGE_NAME="st-image-qt"
MACHINE_NAME="stm32mp25-disco"  # Default; override with --machine
BUILD_DIRECTORY="build-custom-eglfs-stm32mp25-disco"

# SD Card image settings
SD_CARD_IMAGE_NAME=$MACHINE_NAME"_sd_image.raw"
IMAGES_PATH="./tmp-glibc/deploy/images/stm32mp25-disco/"
SD_CARD_IMAGE_CREATOR="./scripts/create_sdcard_from_flashlayout.sh"
SD_CARD_TSV_FILE="./flashlayout_st-image-qt/extensible/FlashLayout_sdcard_stm32mp257f-dk-extensible.tsv"

# === COLORS ===
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# === FUNCTION: run_command ===
function run_command() {
    local cmd="$1"

    echo -e "${YELLOW}Running:${NC} ${GREEN}$cmd${NC}"

    local start_time=$(date +%s%3N)  # milliseconds
    eval "$cmd"
    local exit_code=$?
    local end_time=$(date +%s%3N)    # milliseconds

    local elapsed_ms=$((end_time - start_time))
    local elapsed_sec=$(echo "scale=3; $elapsed_ms / 1000" | bc)
    local elapsed_min=$(echo "scale=2; $elapsed_sec / 60" | bc)
    local elapsed_hr=$(echo "scale=2; $elapsed_min / 60" | bc)

    echo -e "${GREEN}Time taken: ${elapsed_ms} ms | ${elapsed_sec} s | ${elapsed_min} min | ${elapsed_hr} hr${NC}"
    
    return $exit_code
}

# === FUNCTIONS ===

function source_env() {
    echo "Setting up build environment for machine: $MACHINE_NAME"
    if [[ ! -f "layers/meta-st/scripts/envsetup.sh" ]]; then
        echo "Error: envsetup.sh not found. Please run the script from your OpenSTLinux root directory."
        exit 1
    fi

    run_command "source layers/meta-st/scripts/envsetup.sh $BUILD_DIRECTORY"
}

function build_image() {
    run_command "MACHINE=$MACHINE_NAME DISTRO=$DISTRO_VERSION bitbake $IMAGE_NAME"
}

function build_sdk() {
    run_command "MACHINE=$MACHINE_NAME DISTRO=$DISTRO_VERSION bitbake -c populate_sdk $IMAGE_NAME"
}

function build_sd() {
    echo "Building SD card image: $IMAGE_NAME for machine: $MACHINE_NAME"
    local MYDIR
    MYDIR="$(pwd)"

    cd "$IMAGES_PATH" || exit 1

    run_command "$SD_CARD_IMAGE_CREATOR $SD_CARD_TSV_FILE"

    cd "$MYDIR" || exit 1
}

function usage() {
    echo "Usage: $0 [--machine <machine_name>] [--image <image_name>] [build|env|all|sd|sdk]"
    echo "  --machine <name>   Set the machine name (e.g., stm32mp157c-dk2)"
    echo "  --image <name>     Set the image to build (e.g., st-image-core)"
    echo "  env                - Source the environment only"
    echo "  build              - Source the environment and build the image"
    echo "  sd                 - Create the SD card image"
    echo "  all                - Same as 'build'"
    echo "  sdk                - Generate SDK"
}

# === PARSE ARGS ===
POSITIONAL_ARGS=()

while [[ $# -gt 0 ]]; do
    case $1 in
        --machine)
            MACHINE_NAME="$2"
            shift 2
            ;;
        --image)
            IMAGE_NAME="$2"
            shift 2
            ;;
        env|build|all|sd|sdk)
            POSITIONAL_ARGS+=("$1")
            shift
            ;;
        *)
            usage
            exit 1
            ;;
    esac
done

ACTION="${POSITIONAL_ARGS[0]}"

# === MAIN ===
case "$ACTION" in
    env)
        source_env
        ;;
    sd)
        source_env
        build_sd
        ;;
    build|all)
        source_env
        build_image
        ;;
    sdk)
        source_env
        build_sdk
        ;;
    *)
        usage
        ;;
esac
