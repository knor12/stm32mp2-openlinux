SUMMARY = "ST Example Qt5 image with EGLFS (no Wayland)"
DESCRIPTION = "Qt5 image based on a minimal core image, suitable for running Qt via EGLFS."
LICENSE = "MIT"

# Prefer an ST core image if your branch has it:
# require recipes-st/images/st-image-core.bb
# Otherwise fall back to Poky:
require recipes-core/images/core-image-minimal.bb

IMAGE_FEATURES += " \
    ssh-server-dropbear \
    package-management \
"

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