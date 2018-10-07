# AndroidView
自定义控件的使用与封装，包括各种原形图片、柱状图、折线图、饼图、组合图形以及复杂的控件特效等等，是现在在Android开发中直接引用。


# 最新版本

版本号：[![](https://www.jitpack.io/v/YouAreOnlyOne/AndroidView.svg)](https://www.jitpack.io/#YouAreOnlyOne/AndroidView)

使用自行替换下面的版本号，以获得最新版本。

# 使用方法

后期会介绍在不同的项目开发环境中，如何快速的使用该库。

## Android中使用：

方法一：

1.第一步，在项目的build.gradle下配置，注意是项目的build.gradle：

     allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
    
    
2.第二步,在app的build.gradle下添加如下依赖：

    dependencies {
            ...
            implementation 'com.github.YouAreOnlyOne:AndroidHttp:版本号'
            ...
     }
    
