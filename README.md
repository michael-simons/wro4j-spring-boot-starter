# wro4j-spring-boot-starter

A Spring Boot starter and auto-configuration for wro4j:

> [Wro4j](http://alexo.github.io/wro4j/) is a tool for analysis and optimization of web resources. It brings together almost all the modern web tools: JsHint, CssLint, JsMin, Google Closure compressor, YUI Compressor, UglifyJs, Dojo Shrinksafe, Css Variables Support, JSON Compression, Less, Sass, CoffeeScript and much more.

[![Build Status](https://github.com/michael-simons/wro4j-spring-boot-starter/workflows/build/badge.svg)](https://github.com/michael-simons/wro4j-spring-boot-starter/actions) [![Test coverage](https://sonarcloud.io/api/project_badges/measure?project=eu.michael-simons%3Awro4j-spring-boot-starter&metric=coverage)](https://sonarcloud.io/dashboard?id=eu.michael-simons%3Awro4j-spring-boot-starter) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=eu.michael-simons%3Awro4j-spring-boot-starter&metric=alert_status)](https://sonarcloud.io/dashboard?id=eu.michael-simons%3Awro4j-spring-boot-starter) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/eu.michael-simons/wro4j-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/eu.michael-simons/wro4j-spring-boot-starter)

*NOTE* Wro4j has not been updated to the Jakarta Servlet API. Spring Boot 3 has. For now, this project has come to an end. It was good while it lasted and I will keep on updating it with the latest Spring Boot 2.7.x versions, but that's it.

## Introduction

This starter does the following auto configuration for you:

* Creating the _WroFilter_ and the _WroModelFactory_
* Registering them as a ServletFilter through a Spring _FilterRegistrationBean_

Furthermore it provides a Spring Based caching strategy if a [CacheManager](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/CacheManager.html) is present (which is the case if you're Spring Boot Application is configured with [@EnableCaching](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/annotation/EnableCaching.html)) and a cache name is configured.

The wro4j-spring-boot-starter expecteds the Wro4j model configuration in xml format. If you want the Groovy version you have to role your own _WroModelFactory_, which is recognized by the auto configuration.

This starter doesn't bring in the _wro4j-extension_ artifact which are a lot of third party libraries. If you need them, you have to include this in your Maven or Gradle build file.

With the starter comes an additional resource processor _removeSourceMaps_ which removes stale source maps from minified and concatenated files.

This starter is in production at [euregjug.eu](http://www.euregjug.eu) and in various [ENERKO Informatik](http://www.enerko-informatik.de) products.

## Usage and configuration

Just include the starter in your pom.xml:

```
<dependency>
    <groupId>eu.michael-simons</groupId>
    <artifactId>wro4j-spring-boot-starter</artifactId>
    <version>0.11.3</version>
</dependency>
```

These versions have been tested together:

| Spring Boot | Wro4j Starter |
|-------------|---------------|
| 2.0.x       | 0.4.2         |
| 2.1.x       | 0.5.3         |
| 2.2.x       | 0.6.3         |
| 2.3.x       | 0.7.2         |
| 2.4.x       | 0.8.x         |
| 2.5.x       | 0.9.x         |
| 2.6.x       | 0.10.x        |
| 2.7.x       | 0.11.x        |

Add a wro.xml to your resources:

```
<?xml version="1.0" encoding="UTF-8"?>
<groups xmlns="http://www.isdc.ro/wro">
</groups>
```

including your CSS and JS files and you're pretty much good to go. Be aware that this file belongs to your resources (i.e. /src/main/resources) and not under your WEB-INF directory as without the starter. I prefer having those configurations in one place. Read more about the wro.xml format at the official [Wro4j documentation](http://wro4j.readthedocs.org/en/stable/GettingStarted/#step-3-create-wroxml-under-web-inf-directory-and-organize-your-resources-in-groups).

To actually minify your resources, you have to configure some processors. The starter is used at [euregjug.eu](http://www.euregjug.eu) for example with the following configuration:

```
wro4j.managerFactory.preProcessors = removeSourceMaps, cssUrlRewriting, cssImport, cssMinJawr, semicolonAppender, jsMin
```

Have a look at the configuration of the JUGs site at the source [here](https://github.com/EuregJUG-Maas-Rhine/site). If you're already looking for a solution to use Wro4j with Spring Boot and found this starter than i guess you already know about the runtime solution Wro4j provides for your CSS and JS resources.

You can use all processors as described [here](http://wro4j.readthedocs.org/en/stable/AvailableProcessors/).

For further configuration you can use all properties described under [Available Configuration Options](http://wro4j.readthedocs.org/en/stable/ConfigurationOptions/) under the namespace _wro4j.*_, the options for configuring the pre- and postprocessors are under the subnamespace _wro4j.managerFactory.*_, as _wro4j.managerFactory.preProcessors_ and _wro4j.managerFactory.postProcessors_.

As an alternative, you can add processors via their fully qualified classname as _wro4j.preProcessors_ and _wro4j.postProcessors_. Configuring the processors via name or fully qualified class are mutually exclusive.

### Configuration of Pre- and PostProcessors

You can configure Pre- and PostProcessors at two different points:

If you use `wro4j.managerFactory.preProcessors` and `wro4j.managerFactory.postProcessors` you must use predefined [aliases](http://wro4j.readthedocs.io/en/stable/RegisterCustomProcessors/?highlight=alias) or register your own processor provider as described in the comments of [issue #3](https://github.com/michael-simons/wro4j-spring-boot-starter/issues/3).

You can however use "our" custom options `wro4j.preProcessors` and `wro4j.postProcessors` (note the difference: No manager factory in between!). Those options take a comma separated list of classes that must implement `ResourcePreProcessor` and `ResourcePostProcessor` respectively.

If you do this, the `wro4j-spring-boot-starter` first checks, if a bean of the given type exists in the application context. This way, you can manage your processors as normal Spring beans. The only thing you have to take care of is to make sure that those are present in the context before `Wro4jAutoConfiguration` runs. See the example [Wro4jAutoConfigurationIntegrationTests](https://github.com/michael-simons/wro4j-spring-boot-starter/blob/master/src/test/java/ac/simons/spring/boot/wro4j/Wro4jAutoConfigurationIntegrationTests.java#L95).

### Options not present in the original Wro4j version

<table>
        <thead>
                <tr>
                        <th>Option</th>
                        <th>Default</th>
                        <th>Meaning</th>
                </tr>
        </thead>
        <tfoot />
        <tbody>
                <tr>
                        <td>wro4j.model</td>
                        <td>/wro.xml</td>
                        <td>The resource containing the Wro4j model definition (will be looked up as a classloader resource, not inside WEB-INF)</td>
                </tr>
                <tr>
                        <td>wro4j.filterUrl</td>
                        <td>/wro4j</td>
                        <td>Url to which the filter is mapped. Will be expanded to <em>value/*</em></td>
                </tr>
                <tr>
                        <td>wro4j.cacheName</td>
                        <td></td>
                        <td>The name of a Spring Cache. If this property is set and a CacheManager is configured (for example through @EnableCaching), then a CacheStrategy based on Spring cache abstraction will be used.</td>
                </tr>
        </tbody>
</table>

###  Misc

Beans of the following types are recognized and added to wro4j

* `ResourceAuthorizationManager`

### Not configurable at the moment

The options _uriLocators_, _namingStrategy_ and _hashStrategy_ are not configurable at the moment through this starter. If you need those, you have to provide your own _WroManagerFactory_ as a Spring Bean, configured to your needs. The starter will still configure the model and processors factories for you and pass them to your manager factory, though.

## Acknowledgements

I've been using Wro4j as a runtime solution since 2012 on [dailyfratze.de](https://dailyfratze.de) and it really worked well for me. Thanks [Alex](https://twitter.com/wro4j) for your work.

As always, the Spring documentation is a valuable resource. Here's how to start your own auto-configuration or starter: [Creating your own auto-configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html). Thanks to all the people involved.

There's another Wro4j [starter](https://github.com/sbuettner/spring-boot-autoconfigure-wro4j) by Simon Buettner from which I had the basic idea, but I didn't like the fact that it centers around the Groovy model and especially the whole wro4-extensions.

## Examples

* [euregjug.eu](http://www.euregjug.eu), Source code here [github.com/EuregJUG-Maas-Rhine/site](https://github.com/EuregJUG-Maas-Rhine/site). Pretty standard site that uses a theme from [html5up](http://html5up.net), some JQuery.
* [biking2](http://biking.michael-simons.eu), Source code here [github.com/michael-simons/biking2](https://github.com/michael-simons/biking2). A full-blown AngularJS application.
* [Minimal working example](https://github.com/michael-simons/wro4j-spring-boot-starter/files/901848/wro4jdemo.zip), created as a request in [#6](https://github.com/michael-simons/wro4j-spring-boot-starter/issues/6). Just download, unzip and run with `mvn spring-boot:run` to see it working.
* [An example using the wroj4-extensions, especially the SASS/SCSS processor](https://github.com/michael-simons/wro4j-spring-boot-starter/files/947239/wro4jscssdemo.zip), again, just download, unzip and run with `mvn spring-boot:run` to see it working. See explanation in [#7](https://github.com/michael-simons/wro4j-spring-boot-starter/issues/7).

[ENERKO Informatik GmbH](http://www.enerko-informatik.de) is using Wro4j and this starter in several products as well.
