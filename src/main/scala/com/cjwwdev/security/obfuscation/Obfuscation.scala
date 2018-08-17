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

package com.cjwwdev.security.obfuscation

import javax.crypto.Cipher
import org.apache.commons.codec.binary.Base64
import play.api.libs.json.{JsObject, JsValue, Json, Writes}

import scala.annotation.implicitNotFound

@implicitNotFound(
  "No Obfuscator found for type ${T}. Try to implement an implicit Obfuscator for type ${T}"
)
trait Obfuscator[T] {
  def encrypt(value: T): String
}

object Obfuscation {
  import com.cjwwdev.security.EncryptionConfig._

  def obfuscateJson(json: JsValue): String = {
    val bytes = json.toString.getBytes("UTF-8")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
    Base64.encodeBase64URLSafeString(cipher.doFinal(bytes))
  }

  implicit val intObfuscate: Obfuscator[Int] = new Obfuscator[Int] {
    override def encrypt(value: Int): String = obfuscateJson(Json.toJson(value))
  }

  implicit val shortObfuscate: Obfuscator[Short] = new Obfuscator[Short] {
    override def encrypt(value: Short): String = obfuscateJson(Json.toJson(value))
  }

  implicit val byteObfuscate: Obfuscator[Byte] = new Obfuscator[Byte] {
    override def encrypt(value: Byte): String = obfuscateJson(Json.toJson(value))
  }

  implicit val longObfuscate: Obfuscator[Long] = new Obfuscator[Long] {
    override def encrypt(value: Long): String = obfuscateJson(Json.toJson(value))
  }

  implicit val floatObfuscate: Obfuscator[Float] = new Obfuscator[Float] {
    override def encrypt(value: Float): String = obfuscateJson(Json.toJson(value))
  }

  implicit val doubleObfuscate: Obfuscator[Double] = new Obfuscator[Double] {
    override def encrypt(value: Double): String = obfuscateJson(Json.toJson(value))
  }

  implicit val bigDecimalObfuscate: Obfuscator[BigDecimal] = new Obfuscator[BigDecimal] {
    override def encrypt(value: BigDecimal): String = obfuscateJson(Json.toJson(value))
  }

  implicit val booleanObfuscate: Obfuscator[Boolean] = new Obfuscator[Boolean] {
    override def encrypt(value: Boolean): String = obfuscateJson(Json.toJson(value))
  }

  implicit val stringObfuscate: Obfuscator[String] = new Obfuscator[String] {
    override def encrypt(value: String): String = obfuscateJson(Json.toJson(value))
  }

  implicit val jsonObfuscate: Obfuscator[JsValue] = new Obfuscator[JsValue] {
    override def encrypt(value: JsValue): String = obfuscateJson(value)
  }

  implicit val jsObjectObfuscate: Obfuscator[JsObject] = new Obfuscator[JsObject] {
    override def encrypt(value: JsObject): String = obfuscateJson(value)
  }

  def apply[T](f: T => String)(implicit writes: Writes[T]): Obfuscator[T] = new Obfuscator[T] {
    override def encrypt(value: T): String = f(value)
  }

  def encrypt[T](value: T)(implicit obfuscator: Obfuscator[T]): String = obfuscator.encrypt(value)
}
