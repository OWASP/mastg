---
title: MobSF
platform: generic
source: https://github.com/MobSF/Mobile-Security-Framework-MobSF
---

MobSF (Mobile Security Framework) is an automated, all-in-one mobile application pentesting framework capable of performing static and dynamic analysis. The easiest way of getting MobSF started is via Docker.

```bash
docker pull opensecurity/mobile-security-framework-mobsf
docker run -it -p 8000:8000 opensecurity/mobile-security-framework-mobsf:latest
```

Or install and start it locally on your host computer by running:

```bash
# Setup
git clone https://github.com/MobSF/Mobile-Security-Framework-MobSF.git
cd Mobile-Security-Framework-MobSF
./setup.sh # For Linux and Mac
setup.bat # For Windows

# Installation process
./run.sh # For Linux and Mac
run.bat # For Windows
```

Once you have MobSF up and running you can open it in your browser by navigating to <http://127.0.0.1:8000>. Simply drag the APK or IPA you want to analyze into the upload area and MobSF will start its job.

While the [official MobSF documentation](https://mobsf.github.io/docs/#/dynamic_analyzer_docker) focuses on virtual jailbroken or rooted devices, you can still perform dynamic analysis on physical non-jailbroken devices.

If you're running MobSF in Docker and need it to communicate with a physical iOS device via USB, make sure to mount the @MASTG-TOOL-0069 socket. For Android physical devices, you would typically pass the USB device directly using `--device /dev/bus/usb` or connect via ADB over TCP:

```bash
# Example for iOS (mounting usbmuxd):
docker run -it --rm -p 8000:8000 -v /var/run/usbmuxd:/var/run/usbmuxd opensecurity/mobile-security-framework-mobsf:latest

# Example for Android (direct USB access):
docker run -it --rm -p 8000:8000 --device /dev/bus/usb opensecurity/mobile-security-framework-mobsf:latest
```

Once the container has USB access, upload your APK or repackaged IPA to the MobSF web interface.
