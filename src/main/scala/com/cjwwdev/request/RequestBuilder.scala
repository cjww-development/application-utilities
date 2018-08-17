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

import play.api.libs.typedmap.TypedMap
import play.api.mvc.request.{RemoteConnection, RequestTarget}
import play.api.mvc.{Headers, Request, RequestHeader}

import scala.annotation.implicitNotFound

@implicitNotFound(
  "No RequestBuilder found for type ${T}. Try to implement an implicit RequestBuilder for type ${T}"
)
trait RequestBuilder[T] {
  def buildRequest(requestHeader: RequestHeader, requestBody: T): Request[T]
}

object RequestBuilder {
  implicit val stringRequestBuilder: RequestBuilder[String] = new RequestBuilder[String] {
    override def buildRequest(requestHeader: RequestHeader, requestBody: String): Request[String] = new Request[String] {
      override def body: String                 = requestBody
      override def connection: RemoteConnection = requestHeader.connection
      override def method: String               = requestHeader.method
      override def target: RequestTarget        = requestHeader.target
      override def version: String              = requestHeader.version
      override def headers: Headers             = requestHeader.headers
      override def attrs: TypedMap              = requestHeader.attrs
    }
  }

  def buildRequest[T](requestHeader: RequestHeader, body: T)(implicit requestBuilder: RequestBuilder[T]): Request[T] = {
    requestBuilder.buildRequest(requestHeader, body)
  }
}
