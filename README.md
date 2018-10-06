# EPRDownloader
### An extension of ["PRDownloader" by "Mindorks"](https://github.com/MindorksOpenSource/PRDownloader) having a DownloaderQueue to control downloads properly
### A file downloader library for Android with pause and resume support
#
[![](https://jitpack.io/v/arefbhrn/EPRDownloader.svg)](https://jitpack.io/#arefbhrn/EPRDownloader)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Demo
<img src=https://raw.githubusercontent.com/MindorksOpenSource/PRDownloader/master/assets/sample_download.png width=360 height=640 />

### Overview of EPRDownloader library
* EPRDownloader can be used to download any type of files like image, video, pdf, apk and etc.
* Supports pause and resume while downloading a file.
* Supports large file download.
* Simple interface to make download request.
* We can check if the status of downloading with the given download Id.
* EPRDownloader gives callbacks for everything like onProgress, onCancel, onStart, onError and etc while downloading a file. (Resetable anytime)
* Supports proper request canceling.
* Many requests can be made in parallel.
* All types of customization are possible.
* -> Has a Request Queue to handle download requests properly.

## Using EPRDownloader Library in your Android application

Add this in your build.gradle
```groovy
implementation 'com.github.arefbhrn:EPRDownloader:1.0.0'
```
Do not forget to add internet permission in manifest if already not present
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
Then initialize it in onCreate() Method of application class :
```java
EPRDownloader.initialize(getApplicationContext());
```
Initializing it with some customization
```java
// Enabling database for resume support even after the application is killed:
EPRDownloaderConfig config = EPRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
EPRDownloader.initialize(getApplicationContext(), config);

// Setting timeout globally for the download network requests:
EPRDownloaderConfig config = EPRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
EPRDownloader.initialize(getApplicationContext(), config);
```

### Make a download request
```java
DownloadRequest download = EPRDownloader.download("", "/", "").build()
        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
            @Override
            public void onStartOrResume() {

            }
        })
        .setOnPauseListener(new OnPauseListener() {
            @Override
            public void onPause() {

            }
        })
        .setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel() {

            }
        })
        .setOnProgressListener(new OnProgressListener() {
            @Override
            public void onProgress(Progress progress) {

            }
        })
        .setOnDownloadListener(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {

            }

            @Override
            public void onError(Error error) {

            }
        });
download.start();
```

### Pause a download request
```java
EPRDownloader.pause(downloadId);
```

### Resume a download request
```java
EPRDownloader.resume(downloadId);
```

### Cancel a download request
```java
// Cancel with the download id
EPRDownloader.cancel(downloadId);
// The tag can be set to any request and then can be used to cancel the request
EPRDownloader.cancel(TAG);
// Cancel all the requests
EPRDownloader.cancelAll();
```

### Generate ID of a download request
```java
int downloadId = Utils.getUniqueId(url, folderName + "/", fileName);
```
Or if download object is already defined:
```java
int downloadId = download.getDownloadId();
```

### Status of a download request
```java
Status status = EPRDownloader.getStatus(downloadId);
```

### Clean up resumed files if database enabled
```java
// Method to clean up temporary resumed files which is older than the given day
EPRDownloader.cleanUp(days);
```

Using DownloaderQueue
=
```java
DownloaderQueue.setRunningLimit(3); // set maximum number of concurrent downloads
int downloadId = Utils.getUniqueId(url, folderName + "/", fileName);
DownloadRequest download = DownloaderQueue.get(downloadId);
```
Other downloads in this FIFO queue will wait until they can meet conditions to start.

### Add to DownloaderQueue
```java
DownloaderQueue.add(download);
```

### Get download from DownloaderQueue (not remove it)
```java
download = DownloaderQueue.get(downloadId);
```

### Remove download from DownloaderQueue
```java
DownloaderQueue.remove(downloadId);
```

##
### If this library helps you in anyway, show your love :heart: by putting a :star: on this project :v:
##

License
=
This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details