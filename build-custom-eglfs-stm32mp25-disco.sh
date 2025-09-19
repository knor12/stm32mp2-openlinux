#!/bin/bash

# === DEFAULTS ===
DISTRO_VERSION="openstlinux-eglfs"
IMAGE_NAME="st-image-qt"
MACHINE_NAME="stm32mp25-disco"  # Default; override with --machine
BUILD_DIRECTORY="build-custom-eglfs-stm32mp25-disco"

#info needed for creating sd card images
SD_CARD_IMAGE_NAME=$MACHINE_NAME"_sd_image.raw"
IMAGES_PATH="./tmp-glibc/deploy/images/stm32mp25-disco/"
SD_CARD_IMAGE_CREATOR="./scripts/create_sdcard_from_flashlayout.sh"
#SD_CARD_TSV_FILE="./flashlayout_st-image-weston/optee/FlashLayout_sdcard_stm32mp257f-dk-optee.tsv"
SD_CARD_TSV_FILE="./flashlayout_st-image-weston/optee/FlashLayout_sdcard_stm32mp257f-dk-extensible.tsv"


#definition for printing commands in color
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color


# === FUNCTIONS ===

function source_env() {
    echo "Setting up build environment for machine: $MACHINE_NAME"
    if [[ ! -f "layers/meta-st/scripts/envsetup.sh" ]]; then
        echo "Error: envsetup.sh not found. Please run the script from your OpenSTLinux root directory."
        exit 1
    fi
    # echo DISTRO=$DISTRO_VERSION MACHINE=$MACHINE_NAME source layers/meta-st/scripts/envsetup.sh $BUILD_DIRECTORY
# Assign the command to a variable (no spaces around =)
cmd="source layers/meta-st/scripts/envsetup.sh $BUILD_DIRECTORY"

# Echo the command
echo -e "${YELLOW} running: ${GREEN} $cmd ${NC}"

# Run the command using 'eval' so 'source' works correctly
eval "$cmd"
    #source layers/meta-st/scripts/envsetup.sh $BUILD_DIRECTORY
    #DISTRO=$DISTRO_VERSION MACHINE=$MACHINE_NAME source layers/meta-st/scripts/envsetup.sh
    #source layers/meta-st/scripts/envsetup.sh "$DISTRO_VERSION" "$MACHINE_NAME"
}

function build_image() {
    
    #build the command
    cmd="MACHINE=$MACHINE_NAME DISTRO=$DISTRO_VERSION bitbake $IMAGE_NAME"

    # Echo the command
    echo -e "${YELLOW} running: ${GREEN} $cmd ${NC}"

    # Run the command using 'eval' so 'source' works correctly
    eval "$cmd"

   
}


function build_sdk() {

    #build the command
    cmd="MACHINE=$MACHINE_NAME DISTRO=$DISTRO_VERSION bitbake -c populate_sdk $IMAGE_NAME" 

    # Echo the command
    echo -e "${YELLOW} running: ${GREEN} $cmd ${NC}"

    # Run the command using 'eval' so 'source' works correctly
    eval "$cmd"
}

function build_sd() {
    echo "Building sd card imge: $IMAGE_NAME for machine: $MACHINE_NAME, sd card name $SD_CARD_IMAGE_NAME"
    MYDIR="$(pwd)"
 
    pwd 

    cd "$IMAGES_PATH"
    
    #create image
    

        #build the command 
    cmd="$SD_CARD_IMAGE_CREATOR  $SD_CARD_TSV_FILE "

    # Echo the command
    echo -e "${YELLOW} running: ${GREEN} $cmd ${NC}"

    # Run the command using 'eval' so 'source' works correctly
    eval "$cmd"

    #return to working directory
    cd "$MYDIR"
}

function usage() {
    echo "Usage: $0 [--machine <machine_name>] [--image <image_name>] [build|env|all]"
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


#usage
# Build with default machine and image
#./build_openstlinux.sh build

# Specify machine and image
#./build_openstlinux.sh --machine stm32mp157c-dk2 --image st-image-core build
