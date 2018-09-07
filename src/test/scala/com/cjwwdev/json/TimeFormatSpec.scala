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

package com.cjwwdev.json

import java.time.{LocalDateTime, ZoneId}

import org.scalatestplus.play.PlaySpec
import play.api.libs.functional.syntax._
import play.api.libs.json._

class TimeFormatSpec extends PlaySpec {

  val dateTime: LocalDateTime = LocalDateTime.now

  object TimeFormat extends TimeFormat

  case class TestModel(int: Int, ldt: LocalDateTime)

  object TestModel {
    val format: Format[TestModel] = (
      (__ \ "int").format[Int] and
      (__ \ "ldt").format[LocalDateTime](TimeFormat.dateTimeReadLDT)(TimeFormat.dateTimeWriteLDT)
    )(TestModel.apply, unlift(TestModel.unapply))
  }

  "dateTimeWrite" should {
    "write a LocalDateTime to json" in {
      val expectedJson = Json.parse(
        s"""
          |{
          |   "int" : 616,
          |   "ldt" : {
          |       "$$date" : ${dateTime.atZone(ZoneId.systemDefault).toInstant.toEpochMilli}
          |   }
          |}
        """.stripMargin
      )

      Json.toJson(TestModel(int = 616, ldt = dateTime))(TestModel.format) mustBe expectedJson
    }
  }

  "dateTimeRead" should {
    "read json into a LocalDateTime" in {
      val testJson = Json.parse(
        s"""
          |{
          |   "int" : 616,
          |   "ldt" : {
          |       "$$date" : ${dateTime.atZone(ZoneId.systemDefault).toInstant.toEpochMilli}
          |   }
          |}
        """.stripMargin
      )

      Json.fromJson(testJson)(TestModel.format) mustBe JsSuccess(TestModel(int = 616, ldt = dateTime))
    }
  }
}
