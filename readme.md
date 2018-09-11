## PixelShot
[![](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)
[![APK](https://img.shields.io/badge/Download-Demo-brightgreen.svg)](https://github.com/Muddz/StyleableToast/raw/master/demo.apk)


PixelShot is effective Android library that saves any `View`, `ViewGroup` or `SurfaceView` as an image in either `JPG/PNG/.nomedia.`
The library works on a asynchronous task behind the scenes and handles errors of I/O operations and manages memory allocation for you.


## Example of simplest usage:

In this case filename will be a timestamp at the time `save()` is called.
The path will default to `/Pictures` in the internal storage.
```java
   PixelShot.of(view).toPNG().save();
```

## Example of a detailed usage:
```java
             PixelShot.of(view)
                        .setResultListener(this)
                        .setFilename("My image")
                        .setPath("Pictures/myAppName")
                        .toPNG()
                        .save();
```
    
    
## Installation

Add the depedency in your `build.gradle`
```groovy
dependencies {
    implementation 'com.muddzdev:pixelshot:1.0.0'  
}
```
 ----

## License

    Copyright 2017 Muddii Walid (Muddz)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
