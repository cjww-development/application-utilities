// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.cjwwdev.identifiers

import java.util.UUID

import org.slf4j.LoggerFactory
import play.api.mvc.Result
import play.api.mvc.Results.NotAcceptable

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait IdentifierValidation {
  val CONTEXT   = "context"
  val SESSION   = "session"
  val FEED_ITEM = "feed-item"
  val USER      = "user"
  val ORG_USER  = "org-user"
  val DIAG      = "diag"
  val DEVERSITY = "deversity"

  private val logger = LoggerFactory.getLogger(getClass)

  def validateAs(prefix: String, id: String)(f: => Future[Result]): Future[Result] = {
    if(id.contains(prefix)) {
      Try(UUID.fromString(id.replace(s"$prefix-", ""))) match {
        case Success(_) => f
        case Failure(_) =>
          logger.warn("[validateAs] - Given identifier was invalid")
          Future.successful(NotAcceptable)
      }
    } else {
      logger.warn("[validateAs] - Couldn't validate the given identifier against the specified prefix")
      Future.successful(NotAcceptable)
    }
  }
}
