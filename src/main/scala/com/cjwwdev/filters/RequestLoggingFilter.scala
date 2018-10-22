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

package com.cjwwdev.filters

import akka.stream.Materializer
import com.cjwwdev.logging.Logging
import javax.inject.Inject
import org.joda.time.DateTimeUtils
import play.api.mvc.{Filter, RequestHeader, Result}
import play.utils.Colors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions

class DefaultRequestLoggingFilter @Inject()(implicit val mat: Materializer) extends RequestLoggingFilter

trait RequestLoggingFilter extends Filter with Logging {

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    val result = f(rh)
    result map { res =>
      logRequest(res.header.status, DateTimeUtils.currentTimeMillis, rh) foreach logger.info
      res
    }
  }

  def logRequest(status: Int, startTime: Long, rh: RequestHeader): Option[String] = {
    if(!rh.path.contains("/assets/")) {
      Some(
        s"${Colors.yellow(rh.method.toUpperCase)} request to ${Colors.green(rh.path)} " +
        s"returned a ${Colors.cyan(status.toString)} " +
        s"and took ${Colors.magenta(startTime.toString)}ms"
      )
    } else {
      None
    }
  }
}
