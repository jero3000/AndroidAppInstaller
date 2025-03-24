
<div align="center">
  <img src="https://github.com/user-attachments/assets/3c695769-9daf-4ea1-a0f0-710d2324a9d7" alt="Android application installer" />
  <br>
  <h1>Android application installer</h1>
  <p>A tool to automate the download and installation of Android applications on Android devices</p>
  <img src="https://img.shields.io/badge/Platform-macOs-lightgrey.svg?logo=apple" alt="macOS supported" />
  <img src="https://img.shields.io/badge/Platform-Linux-lightgrey.svg?logo=linux" alt="Linux supported" />
  <img src="https://img.shields.io/badge/Platform-Windows-lightgrey.svg" alt="Windows supported" />
</div>

AndroidAppInstaller is a versatile and user-friendly application designed to simplify the process 
of downloading and installing applications on Android devices. It caters to various needs by 
supporting multiple application versions and different projects, ensuring that you can easily manage 
your app installations regardless of the target device type.

This tool could be particularly useful for Dev/QA/CI teams.

Key Features:

- **Multi-Version Support**: Seamlessly handle different versions of an application. Whether you need to downgrade, upgrade, or install a specific version, AndroidAppInstaller makes it easy. 
- **Highly customizable**: Applications are organized by project and target/variant, ensuring a tailored experience for various development environments. A JSON configuration file allows you to precisely define the location of packages.
- **Device Compatibility**: It automatically detects the type of target device and provides placeholders that may be useful in locating the correct package to install.
- **Easy Downloads**: Download applications directly from trusted sources (currently supporting FTP and AWS S3, new source types are planned to be supported)
- **Credentials management**: Securely handle credentials by prompting users only when necessary for downloads. These credentials are then stored in an encrypted format, ensuring that users won't need to re-enter them in future sessions.
- **One-Click Installations**: Simplify the installation process with one-click installs. The installer takes care of the details, so you can focus on your work.
- **Batch Installations**: Save time by installing multiple applications simultaneously. This is perfect for setting up new devices or preparing devices for testing.

### Dependencies

This application requires the Android Debug Bridge (adb) tool installed on the system. You can get adb tool here (Linux/Windows/macOS): https://developer.android.com/tools/releases/platform-tools

If you use AWS S3 to store the application packages, then ASW CLI must be properly configured in your system: https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html

### Overview

Here the main sreen once the configuration file was loaded:

<div align="center">
  <img src="https://github.com/user-attachments/assets/75017f52-a977-43c1-b7bb-a44ff95e6d45" alt="Main appliction screenshot" />
</div>

The description for each input:
- **Project**: allows to select a project: projects are configured in the JSON configuration file
- **Target**: allows to select a target/variant: targets are configured in the JSON configuration file
- **Version**: specifies the version number for the package to download (build number is optional)
- **Device**: allows to select one of the available devices connected through ADB

The steps to download and install packages are the following:
1. Select a project
2. Select a target/variant
3. Specify the version number
4. Select a device
4. Configure the packages to download&install (checking/unchecking the related package)
5. Press the "Download" button to download the packages
6. If the download was successful, then the "install" button is enabled and you can install those packages on the device

### JSON configuration file

The configuration file is structured as follows:

```
{
   "projects":[
      {
         "name":"MyProject",
         "buildVariants":[
            {
               "name":"Operator Tier",
               "location":"ftp://myserver.com/releases",
               "deviceMap":{
                  "Manufacturer1":"man1",
                  "Manufacturer2":"man2"
               },
               "packages":[
                  {
                     "name":"Main application",
                     "packageName":"com.jero3000.application.ot.mainapp",
                     "path":"operatorTier/{device}/R{major}.{minor}.{micro}_{build}/mainapp-operator-release_{device}{major}.{minor}.{micro}.{build}.apk"
                     "altPath":{
                        "man1": "operatorTier/{device:uppercase}/R{major}.{minor}.{micro}_{build}/mainapp-operator-release_{device}{major}.{minor}.{micro}.{build}.apk"
                     }
                  },
                  ...
               ]
            },
            ...
         ]
      },
      ...
   ]
}
```

You can define as many projects as you want. Each project is identified by it name and declares a 
list of "buildVariants" or targets. Each build variant declares:
- **name**: a name to identify the build variant or target
- **location**: an uri to the server where the application packages are located. For AWS S3 the location uri must be: s3://{aws-region}/{bucket-name}. Replacing {aws-region} and {bucket-name} by your own values.
- **deviceMap** (optional): defines a mapping for the device manufacturer string (see the info related to placeholders)
- **packages**: a list defining the application packages for the current build variant or target

Each application package declares:
- **name**: the name of the application
- **packageName**: the Android package name of the application
- **path**: the path where the application package is located in the server
- **altPath** (optional): defines a mapping for alternative package locations based on the device manufacturer (key)

#### Placeholders

Application packages are usually located in an FTP or cloud storage server with a specific directory 
hierarchy. You can use some placeholders which are based on user input or the target device selected.
They can be used to configure the fields **location** and **path**. You must place the placeholder 
name between brackets, example:

```
"path": "mainapp-operator-release_{device}{major}.{minor}.{micro}.{build}.apk"
```

The placeholders supported are:

- **major**: based on the major input text field
- **minor**: based on the minor input text field
- **micro**: based on the micro input text field
- **build**: based on the build input text field
- **device**: based on the "ro.product.manufacturer" device property. If **deviceMap** was configured, then the property value is mapped to the related value

Some modifiers are also available  when a specific placeholder is applied, these are:
- **lowercase**: transforms the resulting string in lower case
- **uppercase**: transforms the resulting string in upper case
- **camelcase**: transforms the resulting string in camel case

You can use the modifiers as follows:

```
"path": "mainapp-operator-release_{device:camelcase}.apk"
```

#### Hardcoded devices support

Sometimes, instead of installing application packages, you may only need to download them. In such 
cases, you can define a set of predefined or hardcoded devices directly in the configuration JSON 
file, as shown below:

```
{
   "devices":[
      {
         "manufacturer": "Manufacturer1"
      },
      {
         "manufacturer": "Manufacturer2"
      }
   ],
   "projects":[
   ...
   ]
}
```

