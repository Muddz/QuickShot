## PixelShot
[![](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16#l16)
[![APK](https://img.shields.io/badge/Download-Demo-brightgreen.svg)](https://github.com/Muddz/StyleableToast/raw/master/demo.apk)


PixelShot is an Android library that saves any `View`, `ViewGroup` or `SurfaceView` as an image in either `JPG/PNG/.nomedia.`


## Features

----

## Example of simplest usage:
```java
   PixelShot.of(view).toJPG().save();
```

## With all current attributes:
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
