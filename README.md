# BlurView

[![](https://jitpack.io/v/sergei-lapin/BlurView.svg)](https://jitpack.io/#sergei-lapin/BlurView)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## What is it?

A RenderScript base BlurView that is capable of blurring the content underneath it

## Usage

Just drop it to your layout of choice and you're done.

Adjustable attributes:

- `bvBlurRadius` — float that represents blur radius. Coerced in (0, 25]. Default is 12
- `bvTint` — tint color that will multiply with blur output. Default is WHITE (#ffffff)
- `bvCornerRadius` — dimension that represents radius of the corners of the BlurView. For example, with that an effect of iOS-like toast could be achieved. Default is 0

## Sample

You could see an example of usage in [sample](https://github.com/sergei-lapin/BlurView/blob/main/sample/src/main/res/layout/activity_main.xml)

## Download
Library is distributed through JitPack

#### Add repository in the root build.gradle
``` Gradle
subprojects {
    repositories {
        maven { url("https://jitpack.io") }
    }
}
```

#### Add library module:

`implementation("com.github.sergei-lapin.BlurView:blurview:{latest-version}")`