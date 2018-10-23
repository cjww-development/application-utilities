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

import java.time.LocalDateTime

import com.cjwwdev.fixtures.TestModel
import com.cjwwdev.security.deobfuscation.DeObfuscation._
import com.cjwwdev.security.obfuscation.Obfuscation._
import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{Json, Reads}

class DeObfuscationSpec extends PlaySpec {

  trait Test[T] {
    val testValue: String
    val result: Either[T, DecryptionError]
  }

  "DeObfuscation" should {
    "succeed" when {
      "de-obfuscate an int" in new Test[Int] {
        override val testValue: String                    = Obfuscation.encrypt(616)
        override val result: Either[Int, DecryptionError] = DeObfuscation.decrypt[Int](testValue)

        assert(result.isLeft)
        result.left.get mustBe 616
      }

      "de-obfuscate a short" in new Test[Short] {
        override val testValue: String                      = Obfuscation.encrypt(1)
        override val result: Either[Short, DecryptionError] = DeObfuscation.decrypt[Short](testValue)

        assert(result.isLeft)
        result.left.get mustBe 1
      }

      "de-obfuscate a byte" in new Test[Byte] {
        override val testValue: String                     = Obfuscation.encrypt(2)
        override val result: Either[Byte, DecryptionError] = DeObfuscation.decrypt[Byte](testValue)

        assert(result.isLeft)
        result.left.get mustBe 2
      }

      "de-obfuscate a long" in new Test[Long] {
        override val testValue: String                     = Obfuscation.encrypt(123456789987654321L)
        override val result: Either[Long, DecryptionError] = DeObfuscation.decrypt[Long](testValue)

        assert(result.isLeft)
        result.left.get mustBe 123456789987654321L
      }

      "de-obfuscate a float" in new Test[Float] {
        override val testValue: String                      = Obfuscation.encrypt(1.0F)
        override val result: Either[Float, DecryptionError] = DeObfuscation.decrypt[Float](testValue)

        assert(result.isLeft)
        result.left.get mustBe 1.0F
      }

      "de-obfuscate a double" in new Test[Double] {
        override val testValue: String                       = Obfuscation.encrypt(1.1234)
        override val result: Either[Double, DecryptionError] = DeObfuscation.decrypt[Double](testValue)

        assert(result.isLeft)
        result.left.get mustBe 1.1234
      }

      "de-obfuscate a big decimal" in new Test[BigDecimal] {
        override val testValue: String                           = Obfuscation.encrypt(1.567)
        override val result: Either[BigDecimal, DecryptionError] = DeObfuscation.decrypt[BigDecimal](testValue)

        assert(result.isLeft)
        result.left.get mustBe 1.567
      }

      "de-obfuscate a boolean" in new Test[Boolean] {
        override val testValue: String                        = Obfuscation.encrypt(true)
        override val result: Either[Boolean, DecryptionError] = DeObfuscation.decrypt[Boolean](testValue)

        assert(result.isLeft)
        assert(result.left.get)
      }

      "de-obfuscate a string" in new Test[String] {
        override val testValue: String                       = Obfuscation.encrypt("testString")
        override val result: Either[String, DecryptionError] = DeObfuscation.decrypt[String](testValue)

        assert(result.isLeft)
        result.left.get mustBe "testString"
      }

      "de-obfuscate a case class" in new Test[TestModel] {
        val now = LocalDateTime.now

        val model = TestModel(
          string   = "testString",
          int      = 616,
          dateTime = now
        )

        implicit val testModelObfuscation: Obfuscator[TestModel] = new Obfuscator[TestModel] {
          override def encrypt(value: TestModel): String = Obfuscation.obfuscateJson(Json.toJson(value))
        }

        implicit val testModelDeObfuscation: DeObfuscator[TestModel] = new DeObfuscator[TestModel] {
          implicit val reads: Reads[TestModel] = TestModel.standardFormat
          override def decrypt(value: String): Either[TestModel, DecryptionError] = DeObfuscation.deObfuscate[TestModel](value)
        }

        override val testValue: String = Obfuscation.encrypt(model)
        override val result: Either[TestModel, DecryptionError] = DeObfuscation.decrypt[TestModel](testValue)

        assert(result.isLeft)
        result.left.get mustBe model
      }
    }

    "fail" when {
      "the input string isn't correctly padded" in new Test[Int] {
        override val testValue: String = "invalid-string"
        override val result: Either[Int, DecryptionError] = DeObfuscation.decrypt[Int](testValue)

        assert(result.isRight)
        result.right.get.message mustBe "Input length must be multiple of 16 when decrypting with padded cipher"
      }

      "the input string decrypts into a incorrect structure" in new Test[TestModel] {
        implicit val testModelDeObfuscation: DeObfuscator[TestModel] = new DeObfuscator[TestModel] {
          implicit val reads: Reads[TestModel] = TestModel.standardFormat
          override def decrypt(value: String): Either[TestModel, DecryptionError] = DeObfuscation.deObfuscate[TestModel](value)
        }

        override val testValue: String = Obfuscation.encrypt(616)
        override val result: Either[TestModel, DecryptionError] = DeObfuscation.decrypt[TestModel](testValue)

        assert(result.isRight)
        Json.parse(result.right.get.message) mustBe Json.parse("""
                                                                 |{
                                                                 | "dateTime" : "error.path.missing",
                                                                 | "int" : "error.path.missing",
                                                                 | "string" : "error.path.missing"
                                                                 |}
                                                               """.stripMargin)
      }
    }
  }
}
