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

import org.scalatestplus.play.PlaySpec
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.Helpers._

import scala.concurrent.Future

class IdentifierValidationSpec extends PlaySpec {
  val testValidator = new IdentifierValidation {}

  def okFunction: Future[Result] = Future.successful(Ok)

  "validateAs" should {
    "return an Ok" when {
      "it has validated a context id" in {
        val id = s"context-${UUID.randomUUID}"
        println(id)
        val result = testValidator.validateAs(testValidator.CONTEXT, id) {
          okFunction
        }
        status(result) mustBe OK
      }

      "it has validated a session id" in {
        val id = s"session-${UUID.randomUUID}"
        println(id)
        val result = testValidator.validateAs(testValidator.SESSION, id) {
          okFunction
        }
        status(result) mustBe OK
      }

      "it has validated a feed item id" in {
        val id = s"feed-item-${UUID.randomUUID}"
        println(id)
        val result = testValidator.validateAs(testValidator.FEED_ITEM, id) {
          okFunction
        }
        status(result) mustBe OK
      }

      "it has validated a user id" in {
        val id = s"user-${UUID.randomUUID}"
        println(id)
        val result = testValidator.validateAs(testValidator.USER, id) {
          okFunction
        }
        status(result) mustBe OK
      }

      "it has validated a org-user id" in {
        val id = s"org-user-${UUID.randomUUID}"
        println(id)
        val result = testValidator.validateAs(testValidator.ORG_USER, id) {
          okFunction
        }
        status(result) mustBe OK
      }

      "it has validated a diag id" in {
        val id = s"diag-${UUID.randomUUID}"
        println(id)
        val result = testValidator.validateAs(testValidator.DIAG, id) {
          okFunction
        }
        status(result) mustBe OK
      }

      "it has validated a deversity id" in {
        val id = s"deversity-${UUID.randomUUID}"
        println(id)
        val result = testValidator.validateAs(testValidator.DEVERSITY, id) {
          okFunction
        }
        status(result) mustBe OK
      }
    }

    "return a NotAcceptable" when {
      "a normal UUID is presented" in {
        val id = UUID.randomUUID.toString
        val result = testValidator.validateAs(testValidator.CONTEXT, id) {
          okFunction
        }
        status(result) mustBe NOT_ACCEPTABLE
      }

      "given something that looks like a UUID but contains characters above f" in {
        val id = s"context-e772xq25-f4p7-4dmc-8792-v1e6f9k9066b"
        val result = testValidator.validateAs(testValidator.CONTEXT, id) {
          okFunction
        }
        status(result) mustBe NOT_ACCEPTABLE
      }

      "an id has an invalid prefix" in {
        val id = s"invalid-e772xq25-f4p7-4dmc-8792-v1e6f9k9066b"
        val result = testValidator.validateAs(testValidator.CONTEXT, id) {
          okFunction
        }
        status(result) mustBe NOT_ACCEPTABLE
      }

      "some random string is presented" in {
        val id = "some-invalid-identifier"
        val result = testValidator.validateAs(testValidator.CONTEXT, id) {
          okFunction
        }
        status(result) mustBe NOT_ACCEPTABLE
      }
    }
  }
}