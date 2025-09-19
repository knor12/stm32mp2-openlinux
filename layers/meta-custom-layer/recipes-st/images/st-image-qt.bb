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

# Optional: add a simple DRM/EGL test tool if your BSP provides it
# IMAGE_INSTALL:append = " kmscube "