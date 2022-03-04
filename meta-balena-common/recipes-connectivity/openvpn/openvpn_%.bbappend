FILESEXTRAPATHS:append := ":${THISDIR}/files"

SRC_URI += " \
    file://prepare-openvpn \
    file://prepare-openvpn.service \
    file://openvpn.service \
    file://upscript.sh \
    file://downscript.sh \
    "

inherit useradd
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} += "--system -d / -M --shell /bin/nologin openvpn"

RDEPENDS:${PN} += "balena-config-vars bash"

SYSTEMD_SERVICE:${PN} = "openvpn.service prepare-openvpn.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install:append() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/prepare-openvpn ${D}${bindir}

	install -d ${D}${sysconfdir}/openvpn-misc
	install -m 0755 ${WORKDIR}/upscript.sh ${D}${sysconfdir}/openvpn-misc
	install -m 0755 ${WORKDIR}/downscript.sh ${D}${sysconfdir}/openvpn-misc

	install -d ${D}${systemd_unitdir}/system
	install -c -m 0644 \
		${WORKDIR}/prepare-openvpn.service \
		${WORKDIR}/openvpn.service \
		${D}${systemd_unitdir}/system
}
