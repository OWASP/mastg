---
title: Host-Device Data Transfer
platform: ios
---

There might be various scenarios where you might need to transfer data from the iOS device or app data sandbox to your host computer or vice versa. The following section will show you different ways on how to achieve that.

## Copying App Data Files via SSH and SCP

As we know now, files from our app are stored in the Data directory. You can now simply archive the Data directory with `tar` and pull it from the device with `scp`:

```bash
iPhone:~ root# tar czvf /tmp/data.tgz /private/var/mobile/Containers/Data/Application/8C8E7EB0-BC9B-435B-8EF8-8F5560EB0693
iPhone:~ root# exit
$ scp -P 2222 root@localhost:/tmp/data.tgz .
```

## Grapefruit

After starting @MASTG-TOOL-0061 you can select the app that is in scope for testing. There are various functions available, of which one is called "Finder". When selecting it, you will get a listing of the directories of the app sandbox.

<img src="Images/Chapters/0x06b/grapefruit_data_dir.png" width="100%" />

When navigating through the directories and selecting a file, a pop-up will show up and display the data either as hexadecimal or text. When closing this pop-up you have various options available for the file, including:

- Database viewer
- Plist viewer
- Download

<img src="Images/Chapters/0x06b/grapefruit_file_download.png" width="100%" />

## Objection

When you are starting objection you will find the prompt within the Bundle directory.

```bash
org.owasp.MSTG on (iPhone: 10.3.3) [usb] # pwd print
Current directory: /var/containers/Bundle/Application/DABF849D-493E-464C-B66B-B8B6C53A4E76/org.owasp.MSTG.app
```

Use the `env` command to get the directories of the app and navigate to the Documents directory.

```bash
org.owasp.MSTG on (iPhone: 10.3.3) [usb] # cd /var/mobile/Containers/Data/Application/72C7AAFB-1D75-4FBA-9D83-D8B4A2D44133/Documents
/var/mobile/Containers/Data/Application/72C7AAFB-1D75-4FBA-9D83-D8B4A2D44133/Documents
```

With the command `filesystem download <filename>` you can download a file from the iOS device to your host computer and can analyze it afterwards.

```bash
org.owasp.MSTG on (iPhone: 10.3.3) [usb] # filesystem download .com.apple.mobile_container_manager.metadata.plist
Downloading /var/mobile/Containers/Data/Application/72C7AAFB-1D75-4FBA-9D83-D8B4A2D44133/.com.apple.mobile_container_manager.metadata.plist to .com.apple.mobile_container_manager.metadata.plist
Streaming file from device...
Writing bytes to destination...
Successfully downloaded /var/mobile/Containers/Data/Application/72C7AAFB-1D75-4FBA-9D83-D8B4A2D44133/.com.apple.mobile_container_manager.metadata.plist to .com.apple.mobile_container_manager.metadata.plist
```

As per objection v1.12.0, objection does support downloading folders by using the strict syntax `filesystem download <remote folder> <local destination> --folder`. However this only applies to folders and does not allow specifying multiple individual files directly.

```bash
...[usb] # filesystem download Documents tmp/Downloads --folder
Downloading /var/mobile/Containers/Data/Application/3577EAF1-A9F2-4049-B6EB-67919F669552/Documents to tmp/Downloads
 Do you want to download the full directory? [Y/n]:Y
Downloading directory recursively...
Successfully downloaded /var/mobile/Containers/Data/Application/3577EAF1-A9F2-4049-B6EB-67919F669552/Documents/apikey_chacha.enc to tmp/Downloads/apikey_chacha.enc
Successfully downloaded /var/mobile/Containers/Data/Application/3577EAF1-A9F2-4049-B6EB-67919F669552/Documents/secret.txt to tmp/Downloads/secret.txt
Recursive download finished.
```

You can also upload files to the iOS device with `file upload <local_file_path>`.
