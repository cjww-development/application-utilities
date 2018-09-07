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

package com.cjwwdev.security.deobfuscation

import com.cjwwdev.logging.Logging
import javax.crypto.Cipher
import org.apache.commons.codec.binary.Base64
import play.api.libs.json.Reads.StringReads
import play.api.libs.json._

import scala.annotation.implicitNotFound
import scala.reflect.ClassTag
import scala.util.Try
import scala.util.control.NoStackTrace

case class DecryptionError(message: String) extends NoStackTrace with Logging {
  def logError[T](implicit tag: ClassTag[T]): Unit = {
    logger.error(s"[deObfuscate] - the input string has failed decryption into type $tag - reason: $message")
  }

  def logValidateError[T](implicit tag: ClassTag[T]): Unit = {
    logger.error(s"[deObfuscate] - the json could not be read into type $tag - reason: $message")
  }
}

@implicitNotFound(
  "No DeObfuscator found for type ${T}. Try to implement an implicit DeObfuscator for type ${T}"
)
trait DeObfuscator[T] {
  def decrypt(value: String): Either[T, DecryptionError]
}

object DeObfuscation {
  import com.cjwwdev.security.EncryptionConfig._

  private def fetchError[T](throwable: Throwable)(implicit tag: ClassTag[T]): Right[T, DecryptionError] = {
    val error = DecryptionError(throwable.getMessage)
    error.logError
    Right(error)
  }

  private def readableJsError[T](errors: Seq[(JsPath, Seq[JsonValidationError])])(implicit tag: ClassTag[T]): DecryptionError = {
    val seq = errors map { case (path, error) => Json.obj(path.toJsonString.replace("obj.", "") -> error.map(_.message).mkString) }
    val decryptionError = DecryptionError(Json.prettyPrint(seq.foldLeft(Json.obj())((obj, a) => obj.deepMerge(a))))
    decryptionError.logValidateError[T]
    decryptionError
  }

  private def jsonToType[T](value: String)(implicit reads: Reads[T], tag: ClassTag[T]): Either[T, DecryptionError] = {
    Json.parse(value).validate[T].fold(
      err  => Right(readableJsError[T](err)),
      data => Left(data)
    )
  }

  def deObfuscate[T](value: String)(implicit reads: Reads[T], tag: ClassTag[T]): Either[T, DecryptionError] = {
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
    Try(cipher.doFinal(Base64.decodeBase64(value))).fold(
      fetchError[T],
      array => jsonToType[T](new String(array))
    )
  }

  implicit val intDeObfuscate: DeObfuscator[Int] = new DeObfuscator[Int] {
    override def decrypt(value: String): Either[Int, DecryptionError] = deObfuscate[Int](value)
  }

  implicit val shortDeObfuscate: DeObfuscator[Short] = new DeObfuscator[Short] {
    override def decrypt(value: String): Either[Short, DecryptionError] = deObfuscate[Short](value)
  }

  implicit val byteDeObfuscate: DeObfuscator[Byte] = new DeObfuscator[Byte] {
    override def decrypt(value: String): Either[Byte, DecryptionError] = deObfuscate[Byte](value)
  }

  implicit val longDeObfuscate: DeObfuscator[Long] = new DeObfuscator[Long] {
    override def decrypt(value: String): Either[Long, DecryptionError] = deObfuscate[Long](value)
  }

  implicit val floatDeObfuscate: DeObfuscator[Float] = new DeObfuscator[Float] {
    override def decrypt(value: String): Either[Float, DecryptionError] = deObfuscate[Float](value)
  }

  implicit val doubleDeObfuscate: DeObfuscator[Double] = new DeObfuscator[Double] {
    override def decrypt(value: String): Either[Double, DecryptionError] = deObfuscate[Double](value)
  }

  implicit val bigDecimalDeObfuscate: DeObfuscator[BigDecimal] = new DeObfuscator[BigDecimal] {
    override def decrypt(value: String): Either[BigDecimal, DecryptionError] = deObfuscate[BigDecimal](value)
  }

  implicit val booleanDeObfuscate: DeObfuscator[Boolean] = new DeObfuscator[Boolean] {
    override def decrypt(value: String): Either[Boolean, DecryptionError] = deObfuscate[Boolean](value)
  }

  implicit val stringDeObfuscate: DeObfuscator[String] = new DeObfuscator[String] {
    override def decrypt(value: String): Either[String, DecryptionError] = deObfuscate[String](value)
  }

  implicit val jsonDeObfuscate: DeObfuscator[JsValue] = new DeObfuscator[JsValue] {
    override def decrypt(value: String): Either[JsValue, DecryptionError] = deObfuscate[JsValue](value)
  }

  implicit val jsObjectDeObfuscate: DeObfuscator[JsObject] = new DeObfuscator[JsObject] {
    override def decrypt(value: String): Either[JsObject, DecryptionError] = deObfuscate[JsObject](value)
  }

  def apply[T](f: String => Either[T, DecryptionError])(implicit reads: Reads[T]): DeObfuscator[T] = new DeObfuscator[T] {
    override def decrypt(value: String): Either[T, DecryptionError] = f(value)
  }

  def decrypt[T](value: String)(implicit deObfuscation: DeObfuscator[T]): Either[T, DecryptionError] = deObfuscation.decrypt(value)
}
