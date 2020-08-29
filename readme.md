# QuickShot
[![](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)
[![APK](https://img.shields.io/badge/Download-Demo-brightgreen.svg)](https://github.com/Muddz/QuickShot/raw/master/demo.apk)

An Android library that saves any `View`, `SurfaceView` or `Bitmap` as an image in `JPG`,`PNG` or `.nomedia`.
The library works on a asynchronous thread, handles errors, I/O operations and memory for you. 

### Features
- Support for Android API 29+ and scoped storage
- For Android API 29+ you must use the `setInternalPath()` to save to a internal/private path due to scoped storage
- For Android API's below 29 `setPath()` can be used to save to any full path
- `Bitmap` objects can now be passed into the constructor too (with `Context` as second parameter)


## Example of simplest usage with defaults
<i>You can use a simple one-liner and let QuickShot set default values for File Attributes like in the following example:</i>

Filename defaults to a timestamp.   
Path defaults to `/Pictures` in the public storage.  
Image format defaults to `.JPG`

```java
   QuickShot.of(view).setResultListener(this).save();
```

## Example of a detailed usage
```java
    QuickShot.of(view).setResultListener(this)
                      .setFilename("Hello World")
                      .setPath("MyApp/Pictures")
                      .toPNG()
                      .save();
```

## Installation

Add the dependency in your `build.gradle`
```groovy
dependencies {
    implementation 'com.muddzdev:quickshot:1.2.1'  
}
```
 ----

## License

    Copyright 2018 Muddi Walid

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
