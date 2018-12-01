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

package com.cjwwdev.request

import java.time.LocalDateTime

import com.cjwwdev.fixtures.TestModel
import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import com.cjwwdev.security.obfuscation.Obfuscation._
import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{Format, JsValue, Json, Reads}
import play.api.mvc.Results.Ok
import play.api.mvc.{Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class RequestParsersSpec extends PlaySpec with GuiceOneAppPerSuite {

  val testParsers = new RequestParsers {}

  implicit val testModelObfuscation: Obfuscator[TestModel] = new Obfuscator[TestModel] {
    override def encrypt(value: TestModel): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  implicit val testModelDeObfuscation: DeObfuscator[TestModel] = new DeObfuscator[TestModel] {
    implicit val reads: Reads[TestModel] = TestModel.standardFormat
    override def decrypt(value: String): Either[TestModel, DecryptionError] = DeObfuscation.deObfuscate[TestModel](value)
  }

  val now                   = LocalDateTime.now
  val testModel             = TestModel("testString", 616, now)
  val testEncString: String = Obfuscation.encrypt(testModel)

  def okFunction[T](data: T)(implicit format: Format[T]): Future[Result] = Future.successful(Ok(Json.toJson[T](data)))

  "withJsonBody" should {
    "decrypt the request body and return an Ok" in {
      implicit val request: Request[String] = FakeRequest().withBody(testEncString)

      val result = testParsers.withJsonBody[TestModel] { data =>
        okFunction[TestModel](data)
      }

      status(result)        mustBe OK
      contentAsJson(result) mustBe Json.toJson(testModel)
    }

    "return a BadRequest" when {
      "the decrypted value can't be parsed into the desired type" in {
        implicit val request: Request[String] = FakeRequest().withBody("invalid-string")

        val result = testParsers.withJsonBody[TestModel] { data =>
          okFunction[TestModel](data)
        }

        status(result)                                     mustBe BAD_REQUEST
        contentAsJson(result).\("errorMessage").as[String] mustBe s"Couldn't decrypt request body on ${request.path}"
      }

      "the decrypted value is missing a field" in {
        implicit val request: Request[String] = FakeRequest().withBody(Obfuscation.encrypt(Json.obj(
          "string" -> "testString",
          "int"    -> 616
        )))

        val result = testParsers.withJsonBody[TestModel] { data =>
          okFunction[TestModel](data)
        }

        status(result)                                     mustBe BAD_REQUEST
        contentAsJson(result).\("errorMessage").as[String] mustBe "Decrypted json was missing a field"
        contentAsJson(result).\("errorBody").as[JsValue]   mustBe Json.parse("""{ "dateTime" : "error.path.missing" }""")
      }
    }
  }

  "withEncryptedUrl" should {
    "decrypt the url and return an Ok" in {
      implicit val request: Request[String] = FakeRequest().withBody("")

      val result = testParsers.withEncryptedUrl[TestModel](testEncString) { data =>
        okFunction[TestModel](data)
      }

      status(result)        mustBe OK
      contentAsJson(result) mustBe Json.toJson(testModel)
    }

    "return a BadRequest" when {
      "the decrypted value can't be parsed into the desired type" in {
        implicit val request: Request[String] = FakeRequest().withBody("")

        val result = testParsers.withEncryptedUrl[TestModel]("invalid-string") { data =>
          okFunction[TestModel](data)
        }

        status(result)                                     mustBe BAD_REQUEST
        contentAsJson(result).\("errorMessage").as[String] mustBe s"Couldn't decrypt request url on ${request.path}"
      }

      "the decrypted value is missing a field" in {
        implicit val request: Request[String] = FakeRequest().withBody("")

        val testEncString = Obfuscation.encrypt(Json.obj(
          "string" -> "testString",
          "int"    -> 616
        ))

        val result = testParsers.withEncryptedUrl[TestModel](testEncString) { data =>
          okFunction[TestModel](data)
        }

        status(result)                                     mustBe BAD_REQUEST
        contentAsJson(result).\("errorMessage").as[String] mustBe "Decrypted json was missing a field"
        contentAsJson(result).\("errorBody").as[JsValue]   mustBe Json.parse("""{ "dateTime" : "error.path.missing" }""")
      }
    }
  }
}
