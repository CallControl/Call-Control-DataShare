# Call Control DataShare

[Download](https://s01.oss.sonatype.org/content/groups/public/com/callcontrol/datashare/)

Call Control DataShare is a mechanism which allows other apps to interact with [Call Control for Android](https://play.google.com/store/apps/details?id=com.flexaspect.android.everycallcontrol) application. It allows to enable powerful calls/texts blocking possibilities in 3rd party apps without significant effort. 

The benefits of this approach:

- Call Control maintains the infrastructure, provides powerful features like Community IQ™ and other blocking features. No need to implement your own.
- Call Control takes care of user settings and allows the 3rd party apps to honor it effortlessly, providing slick user experience and eliminating the need to force user to have different settings in different apps for the same purpose.
- Very basic implementation is needed for 3rd party apps.


See [Wiki](wiki) for more details.

## Adding to Your Project

To integrate with Call Control you need to install Call Control from [Google Play](https://play.google.com/store/apps/details?id=com.flexaspect.android.everycallcontrol), then in your application's `build.gradle` add the following dependency:

```gradle

repositories {
    mavenCentral()
}
    
dependencies {
	...
	implementation 'com.callcontrol:datashare:1.3.0'
	...
}

```

That's all you need to do to integrate, you can now use all power of Call Control!

Please refer to [Wiki](wiki) and [javadoc](https://callcontrol.github.io/Call-Control-DataShare/) for more details.

## License

Call Control DataShare is released under the MIT license.
See [LICENSE](./LICENSE) for details.

---
_Call Control® is a registered trademark of Call Control, LLC_
