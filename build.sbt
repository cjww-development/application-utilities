/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.typesafe.config.ConfigFactory
import scala.util.{Failure, Success, Try}

val libraryName = "application-utilities"

val bTVersion : String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

val playImports: Seq[ModuleID] = Seq(guice)

val dependencies: Seq[ModuleID] = Seq(
  "com.typesafe.play"       % "play_2.12"           % "2.6.17",
  "org.scalatestplus.play" %% "scalatestplus-play"  % "3.1.2"   % Test,
  "ch.qos.logback"          % "logback-classic"     % "1.2.3"
)

lazy val library = Project(libraryName, file("."))
  .settings(
    version                              :=  bTVersion,
    scalaVersion                         :=  "2.12.6",
    organization                         :=  "com.cjww-dev.libs",
    resolvers                            ++= Seq(
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "cjww-dev"            at "http://dl.bintray.com/cjww-development/releases"
    ),
    libraryDependencies                  ++= dependencies ++ playImports,
    bintrayOrganization                  :=  Some("cjww-development"),
    bintrayReleaseOnPublish in ThisBuild :=  true,
    bintrayRepository                    :=  "releases",
    bintrayOmitLicense                   :=  true,
    fork                    in Test      :=  true,
    javaOptions             in Test      :=  Seq(
      "-Ddata-security.key=testKey",
      "-Ddata-security.salt=testSalt"
    )
  )
