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

import com.cjwwdev.logging.output.Logger
import com.cjwwdev.request.RequestBuilder._
import play.api.http.HttpErrorHandler
import play.api.http.Status.{FORBIDDEN, NOT_FOUND}
import play.api.mvc.Results.{InternalServerError, NotFound, Redirect, Status}
import play.api.mvc.{Call, Request, RequestHeader, Result}
import play.twirl.api.Html

import scala.concurrent.Future

trait FrontendRequestErrorHandler extends HttpErrorHandler with Logger {

  def loginRedirect: Call

  def standardErrorView: Html
  def notFoundView: Html
  def serverErrorView: Html

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    implicit val req: Request[String] = buildRequest[String](request, "")
    LogAt.error(s"[ErrorHandler] - [onClientError] - Url: ${request.uri}, status code: $statusCode")
    statusCode match {
      case NOT_FOUND  => Future.successful(NotFound(notFoundView))
      case FORBIDDEN  => Future.successful(Redirect(loginRedirect))
      case _          => Future.successful(Status(statusCode)(standardErrorView))
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    implicit val req: Request[String] = buildRequest[String](request, "")
    LogAt.error(s"[onServerError] - Server Error!", exception)
    Future.successful(InternalServerError(serverErrorView))
  }
}
