/*
 *  Copyright 2018 CJWW Development
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cjwwdev.config

import javax.inject.Inject
import play.api.{ConfigLoader, Configuration}

class DefaultConfigurationLoader @Inject()(val loadedConfig: Configuration) extends ConfigurationLoader

trait ConfigurationLoader {
  protected val loadedConfig: Configuration

  private val configRoot = "microservice.external-services"

  def get[T](key: String)(implicit configLoader: ConfigLoader[T]): T = configLoader.load(loadedConfig.underlying, key)

  def getServiceUrl(service: String): String = loadedConfig.get[String](s"$configRoot.$service.domain")

  def getServiceId(service: String): String = loadedConfig.get[String](s"$configRoot.$service.application-id")
}
