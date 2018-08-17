[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[ ![Download](https://api.bintray.com/packages/cjww-development/releases/application-utilities/images/download.svg) ](https://bintray.com/cjww-development/releases/application-utilities/_latestVersion)

application-utilities
=====================

This library contains common configuration for microservices and utilities for validation, regex, json formatting, loading configuration, data encryption and request body processing.

To utilise this library add this to your sbt build file

```sbtshell
"com.cjww-dev.libs" % "application-utilities_2.11" % "4.2.0" 
```

## About
#### resources/common.conf
Contains common configuration strings for CJWW scala play apps. To utilise add the following snippet to your application.conf file. 
```hocon
    include "common.conf"
```

<br>

#### ConfigurationLoader.scala
Contains functions to pull a domain for a microservice from configuration and to pull an applications applicationId. Throws **MissingConfigurationException** if configuration value is not found.
Also contains a generic config `get[T]` method to pull any type of config value. Requires the ConfigLoader import to bring implicit config loaders into scope.

```scala
    import play.api.ConfigLoader._

    val configLoader = new ConfigurationLoader()
    
    configLoader.getServiceUrl("test-app")
    
    configLoader.getServiceId("test-app")
    
    configLoader.get[String]("test-key")
```

**Note: ConfigurationLoader needs a play.api.Configuration class to be injected to work.**

<br>

#### IdentifierValidation.scala
Validates if the given string is prefixed correctly and if it a UUID. Returns a NotAcceptable response code if the given string is not in the correct format. 

```scala
    class ExampleController extends Controller with IdentifierValidation {
      def exampleAction(id: String): Action[AnyContent] = Action.async {
        implicit request =>
          validateAs(USER, id) {
            Future.successful(Ok)
          }
      }
    }
```

<br>

#### com.cjwwdev.json
This package contains traits to ensure case classes have a Json formatter. Also contains Json reads and writes for **LocalDateTime**

<br>

#### RequestParsers.scala
Contains functions to decrypt either part of the url or the request body into type **T**.

```scala
    import com.cjwwdev.security.deobfuscation.DeObfuscation._

    //Example decrypting to into type T
    case class ExampleModel(str: String, int: Int)
    implicit val format = Json.format[ExampleModel]
    
    class ExampleController extends Controller with RequestParsers {
      def exampleActionDecryptingRequestBody(id: String): Action[AnyContent] = Action.async {
        implicit request =>
          withJsonBody[ExampleModel] { decryptedModel =>
            Ok(decryptedModel)
          }
      }
      
      def exampleActionDecryptingUrl(id: String): Action[AnyContent] = Action.async {
        implicit request =>
          withEncryptedUrl(id) { decryptedUrl =>
            Ok(decryptedUrl)
          }
      }
    }
```

**Note: Both `withJsonBody` and `withEncryptedUrl` require an implicit `DeObfuscation[T]`as explained in the Obfuscation/DeObfuscation section**

#### Obfuscation/DeObfuscation
Obfuscation and de-obfuscation provide functionality to encrypt type `T` to a `String` and decrypt `String` into type `T`.

```scala
    import com.cjwwdev.security.obfuscation.Obfuscation
    
    val encString: String = Obfuscation.encrypt(616)
```

```scala
    import com.cjwwdev.security.deobfuscation.DeObfuscation
    
    val decType: Boolean = DeObfuscation.decrypt[Boolean]("some-padded-string")
```

**Need a encrypt a different type?**

Both Obfuscation and DeObfuscation common obfuscators and de-obfuscators that can encrypt and decrypt common types such as `String`, `Int`, `Boolean` and others. 

But lets say you want to encrypt something that is covered by the common obfuscators. You have to define your own obfuscator. 
```scala
    case class ExampleModel(str: String, int: Int)
    
    val testModel = ExampleModel("testString", 616)
    
    implicit val exampleModelObfuscator: Obfuscator[ExampleModel] = new Obfuscator[ExampleModel] {
      override def encrypt(value: ExampleModel): String = Obfuscation.obfuscateJson(Json.toJson(value))
    }
    
    Obfuscation.encrypt(testModel)
```

Similar if you want to decrypt your example model.
```scala
    case class ExampleModel(str: String, int: Int)
    
    val testEncString = "example-padded-string"
    
    implicit val exampleModelDeObfuscator: DeObfuscator[ExampleModel] = new DeObfuscator[ExampleModel] {
      override def decrypt(value: String): Either[ExampleModel, DecryptionError] = DeObfuscation.deObfuscate[ExampleModel](value)
    }
    
    DeObfuscation.decrypt[ExampleModel](testEncString)
```



### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")