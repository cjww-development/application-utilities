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

import java.util.Base64

import com.cjwwdev.logging.output.Logger
import com.cjwwdev.request.RequestBuilder._
import play.api.mvc.Results.Redirect
import play.api.mvc.{Call, Filter, RequestHeader, Result}

import scala.concurrent.Future

trait IpWhitelistFilter extends Filter with Logger {
  val enabled: Boolean
  val whitelistIps: String
  val excludedPaths: String

  val baseAppUri: String

  val serviceOutage: Call

  private val ipHeader = "X-Forwarded-For"

  private def decodeIntoList(encodedString: String): Seq[String] = {
    Some(new String(Base64.getDecoder.decode(encodedString), "UTF-8")).map(_.split(",")).getOrElse(Array.empty).toSeq
  }

  private lazy val whiteListSeq               = decodeIntoList(whitelistIps)
  private lazy val excludedPathSeq: Seq[Call] = decodeIntoList(excludedPaths) map(Call("GET", _))

  private def uriIsWhitelisted(rh: RequestHeader): Boolean = excludedPathSeq contains Call(rh.method, rh.uri)
  private def isAssetRoute(rh: RequestHeader): Boolean     = rh.uri contains s"$baseAppUri/assets/"

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    implicit val req = buildEmptyRequest(rh)
    if(enabled) {
      LogAt.info(s"Attempting to access ${rh.uri} whilst whitelist filter is active")
      if(uriIsWhitelisted(rh) | isAssetRoute(rh)) {
        f(rh)
      } else {
        rh.headers.get(ipHeader) match {
          case Some(ip) => if(whiteListSeq contains ip) f(rh) else Future.successful(Redirect(serviceOutage))
          case None     =>
            LogAt.warn(s"[IPWhitelistFilter] - No X-Forwarded-For header present blocking request")
            Future.successful(Redirect(serviceOutage))
        }
      }
    } else {
      f(rh)
    }
  }
}
