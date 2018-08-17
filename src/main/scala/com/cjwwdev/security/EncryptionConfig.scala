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

package com.cjwwdev.security

import java.security.MessageDigest
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

private[security] object EncryptionConfig {

  private val LENGTH: Int = 16

  private val config: Config = ConfigFactory.load

  private val KEY: String  = config.getString("data-security.key")
  private val SALT: String = config.getString("data-security.salt")

  val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")

  val secretKeySpec: SecretKeySpec = {
    val sha512 = MessageDigest.getInstance("SHA-512").digest(s"$SALT$KEY".getBytes("UTF-8"))
    new SecretKeySpec(util.Arrays.copyOf(sha512, LENGTH), "AES")
  }
}
