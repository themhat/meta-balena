DESCRIPTION = "Resin state reset"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BALENA_COREBASE}/COPYING.Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = " \
    file://resin-state-reset \
    file://resin-state-reset.service \
    "
S = "${WORKDIR}"

inherit allarch systemd

RDEPENDS:${PN} = " \
    bash \
    coreutils \
    "

SYSTEMD_SERVICE:${PN} = "resin-state-reset.service"

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_build[noexec] = "1"

BALENA_STATE_MOUNT_POINT = "/mnt/state"

do_install() {
    install -d ${D}${bindir}/
    install -m 0755 ${WORKDIR}/resin-state-reset ${D}${bindir}/

    sed -i -e 's,@BALENA_STATE_MP@,${BALENA_STATE_MOUNT_POINT},g' \
          ${D}${bindir}/resin-state-reset

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system/
        install -d ${D}${sysconfdir}/systemd/system/multi-user.target.wants/
        install -m 0644 ${WORKDIR}/resin-state-reset.service ${D}${systemd_unitdir}/system/

        sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
            -e 's,@SBINDIR@,${sbindir},g' \
            -e 's,@BINDIR@,${bindir},g' \
            -e 's,@BALENA_STATE_MP@,${BALENA_STATE_MOUNT_POINT},g' \
            ${D}${systemd_unitdir}/system/*.service
    fi
}
