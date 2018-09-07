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

import java.time.LocalDateTime

import com.cjwwdev.fixtures._
import com.cjwwdev.security.obfuscation.Obfuscation._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}

class ObfuscationSpec extends PlaySpec {

  trait Test[T] {
    val testValue: T
    val result: String
  }

  "Obfuscation" should {
    "obfuscate an int" in new Test[Int] {
      override val testValue: Int = 616
      override val result: String = Obfuscation.encrypt(testValue)

      assert(testValue.toString != result)
    }

    "obfuscate a short" in new Test[Short] {
      override val testValue: Short = 1
      override val result: String   = Obfuscation.encrypt(testValue)

      assert(testValue.toString != result)
    }

    "obfuscate a byte" in new Test[Byte] {
      override val testValue: Byte = 2
      override val result: String   = Obfuscation.encrypt(testValue)

      assert(testValue.toString != result)
    }

    "obfuscate a long" in new Test[Long] {
      override val testValue: Long = 123456789123456789L
      override val result: String   = Obfuscation.encrypt(testValue)

      assert(testValue.toString != result)
    }

    "obfuscate a float" in new Test[Float] {
      override val testValue: Float = 1.0F
      override val result: String   = Obfuscation.encrypt(testValue)

      assert(testValue.toString != result)
    }

    "obfuscate a double" in new Test[Double] {
      override val testValue: Double = 1.01234
      override val result: String   = Obfuscation.encrypt(testValue)

      assert(testValue.toString != result)
    }

    "obfuscate a big decimal" in new Test[BigDecimal] {
      override val testValue: BigDecimal = 1.07654321
      override val result: String        = Obfuscation.encrypt(testValue)

      assert(testValue.toString != result)
    }

    "obfuscate a boolean" in new Test[Boolean] {
      override val testValue: Boolean = true
      override val result: String     = Obfuscation.encrypt(testValue)

      assert(testValue.toString != result)
    }

    "obfuscate a string" in new Test[String] {
      override val testValue: String = "testString"
      override val result: String    = Obfuscation.encrypt(testValue)

      assert(testValue != result)
    }

    "obfuscate a jsvalue" in new Test[JsValue] {
      override val testValue: JsValue = Json.parse("""{ "abc" : "xyz" }""")
      override val result: String     = Obfuscation.encrypt(testValue)

      assert(testValue.toString != result)
    }

    "obfuscate a case class" in new Test[TestModel] {
      implicit val testModelObfuscation: Obfuscator[TestModel] = new Obfuscator[TestModel] {
        override def encrypt(value: TestModel): String = Obfuscation.obfuscateJson(Json.toJson(value))
      }

      override val testValue: TestModel = TestModel(string = "testString", int = 616, dateTime = LocalDateTime.of(2018, 10, 26, 12, 0, 0, 0))
      override val result: String       = Obfuscation.encrypt(testValue)

      assert(testValue.toString != result)
    }
  }
}
