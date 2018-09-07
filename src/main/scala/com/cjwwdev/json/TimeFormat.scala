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

package com.cjwwdev.json

import java.time.{Instant, LocalDateTime, ZoneId}

import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json._

trait TimeFormat {
  implicit val dateTimeReadLDT: Reads[LocalDateTime] = Reads[LocalDateTime] {
    _.\("$date").validate[Long].fold(
      JsError(_),
      long => JsSuccess(LocalDateTime.ofInstant(Instant.ofEpochMilli(long), ZoneId.systemDefault))
    )
  }

  implicit val dateTimeWriteLDT: Writes[LocalDateTime] = Writes[LocalDateTime] {
    date => Json.obj("$date" -> date.atZone(ZoneId.systemDefault).toInstant.toEpochMilli)
  }

  @deprecated("Use dateTimeReadLDT", "2018-05-31")
  implicit val dateTimeRead: Reads[DateTime] = (__ \ "$date").read[Long] map {
    new DateTime(_, DateTimeZone.UTC)
  }

  @deprecated("Use dateTimeWriteLDT", "2018-05-31")
  implicit val dateTimeWrite: Writes[DateTime] = Writes[DateTime] {
    dateTime => Json.obj("$date" -> dateTime.getMillis)
  }
}
