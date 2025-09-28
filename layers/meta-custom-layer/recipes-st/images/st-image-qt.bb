SUMMARY = "ST Example Qt5 image with EGLFS (no Wayland)"
DESCRIPTION = "Qt5 image based on a minimal core image, suitable for running Qt via EGLFS."
LICENSE = "MIT"

include recipes-st/images/st-image.inc

inherit core-image features_check

# let's make sure we have a good image...
REQUIRED_DISTRO_FEATURES = "wayland"

IMAGE_LINGUAS = "en-us"

IMAGE_FEATURES += "\
    splash              \
    package-management  \
    ssh-server-dropbear \
    hwcodecs            \
    tools-profile       \
    eclipse-debug       \
    "

#
# INSTALL addons
#
CORE_IMAGE_EXTRA_INSTALL += " \
    resize-helper \
    st-hostname \
    \
    packagegroup-framework-core-base    \
    packagegroup-framework-tools-base   \
    \
    packagegroup-framework-core         \
    packagegroup-framework-tools        \
    \
    packagegroup-framework-core-extra   \
    \
    ${@bb.utils.contains('COMBINED_FEATURES', 'optee', 'packagegroup-optee-core', '', d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'optee', 'packagegroup-optee-test', '', d)} \
    \
    ${@bb.utils.contains('COMBINED_FEATURES', 'tpm2', 'packagegroup-security-tpm2', '', d)} \
    \
    packagegroup-st-demo \
    "

# NOTE:
#   packagegroup-st-demo are installed on rootfs to populate the package
#   database.


# Core Qt5 + common modules; avoid qtwayland (no Wayland)
IMAGE_INSTALL:append = " \
    qtbase \
    qtbase-plugins \
    qtdeclarative \
    qtquickcontrols \
    qtquickcontrols2 \
    qtgraphicaleffects \
    qtmultimedia \
    qtmultimedia-plugins \
    qtimageformats \
    qtsvg \
    fontconfig \
    tslib tslib-calibrate \
"


IMAGE_INSTALL:append = " \
    iproute2 \
    net-tools \
    ethtool \
    dhcpcd \
"



# Optional: add a simple DRM/EGL test tool if your BSP provides it

# GPU/DRM userspace
# Handy tools
IMAGE_INSTALL:append = " kernel-modules "
# IMAGE_INSTALL:append = " \
#     kernel-modules \
#     libdrm libdrm-tests \
#     libgbm libegl-mesa libgles2-mesa mesa-megadriver \
#     kmscube \
#     usbutils ethtool iproute2 \
# "
#Ethernet
# IMAGE_INSTALL:append = " kernel-module-stmmac kernel-module-dwmac-stm32 kernel-module-libphy kernel-module-realtek"


# #USB host (DWC3 on STM32MP25)
# IMAGE_INSTALL:append = " \
# kernel-module-dwc3 \
# kernel-module-dwc3-st \
# kernel-module-xhci-plat-hcd \
# kernel-module-usb-storage \
#     "

# #Display/GPU (DRM/KMS + Vivante etnaviv)
# IMAGE_INSTALL:append = " \
# kernel-module-drm  \
# kernel-module-drm-kms-helper \
# kernel-module-etnaviv \
# kernel-module-panel-simple   \
#   " 

# HMI bridge chip
# IMAGE_INSTALL:append = " kernel-module-adv7533 "


#add SDK stuff
# Prefer SDK_HOST_TASK on Scarthgap
# qmake is supplied from nativesdk-qtbase (subpackages vary per branch);
# mkspecs/headers from nativesdk-qtbase-dev; Qt tools from qttools.
SDK_HOST_TASK:append = " \
    nativesdk-qtbase \
    nativesdk-qtbase-dev \
    nativesdk-qttools-tools \
    nativesdk-qttools \
    nativesdk-qttools-dev \
    nativesdk-qtdeclarative-tools \
    nativesdk-cmake \
    nativesdk-ninja \
    nativesdk-pkgconfig \
"

# Target-side SDK sysroot packages available to the SDK (not installed into image)
# qtwayland-dev
SDK_TARGET_TASK:append = " \
    qtbase-dev \
    qtbase-plugins \
    qtbase-mkspecs \
    qtdeclarative-dev \
    qtquickcontrols2-dev \
    qtsvg-dev \
    qtimageformats-dev \
    qtmultimedia-dev \
    qtserialport-dev \
"
SDK_TARGET_TASK:append = " optee-client optee-client-dev optee-os-tadevkit "


#enable the generation of emmc and sd card flash layout files
#ENABLE_FLASHLAYOUT_DEFAULT = "1"
FLASHLAYOUT_TYPE = "emmc:optee sdcard:optee"
#FLASHLAYOUT_TYPE += "emmc:extensible sdcard:extensible"

#compress rootfile system
IMAGE_FSTYPES = " ext4  ext4.gz "

#for non development images 
#EXTRA_IMAGE_FEATURES:remove = " dbg-pkgs dev-pkgs src-pkgs ptest-pkgs tools-debug tools-profile tools-testapps"

# Define ROOTFS_MAXSIZE to 3GB
IMAGE_ROOTFS_MAXSIZE = "3145728"

#enable graphics
IMAGE_INSTALL:append = " weston weston-init  "


# Target (image) Qt5: Wayland + GLES2, no X11, no desktop GL, and no target-side tools
# PACKAGECONFIG:remove:pn-qtbase:class-target = " gl xcb tools"
# PACKAGECONFIG:append:pn-qtbase:class-target = " wayland gles2 libinput fontconfig harfbuzz"
