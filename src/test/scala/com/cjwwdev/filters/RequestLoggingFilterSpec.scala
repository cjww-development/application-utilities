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

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.cjwwdev.testing.unit.UnitTestSpec
import org.joda.time.DateTimeUtils
import org.mockito.ArgumentMatchers
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito.{reset, times, verify, verifyZeroInteractions}
import org.slf4j.Logger
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest

import scala.concurrent.Future

class RequestLoggingFilterSpec extends UnitTestSpec with MockitoSugar {

  val elapsedTime: Long = DateTimeUtils.currentTimeMillis - 3L

  override def beforeAll(): Unit = {
    super.beforeAll()
    System.setProperty("sbt.log.noformat", "true")
  }

  private def prepareMaterializer: ActorMaterializer = {
    ActorMaterializer()(ActorSystem())
  }

  private val testFilter = new RequestLoggingFilter {
    override implicit def mat: Materializer = prepareMaterializer
  }

  def futureAction: Future[Result] = Future(Ok("Test"))

  "logRequest" should {
    "return a string" when {
      "the request path doesn't contain /assets/" in {
        implicit val request = FakeRequest("GET", "/test-route")

        assert(testFilter.logRequest(200, elapsedTime, request).get.contains("GET request to /test-route returned a 200 and took"))
      }
    }

    "return none" when {
      "the request path contains /assets/" in {
        implicit val request = FakeRequest("GET", "/test-route/assets/")

        testFilter.logRequest(200, elapsedTime, request) mustBe None
      }
    }
  }
}
