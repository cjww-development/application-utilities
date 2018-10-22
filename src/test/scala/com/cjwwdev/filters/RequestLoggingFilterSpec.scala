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
import org.mockito.ArgumentMatchers
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito.{times, verify, verifyZeroInteractions, reset}
import org.slf4j.Logger
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest

import scala.concurrent.Future

class RequestLoggingFilterSpec extends UnitTestSpec with MockitoSugar {

  private def prepareMaterializer: ActorMaterializer = {
    ActorMaterializer()(ActorSystem())
  }

  private val mockLogger = mock[Logger]

  private val testFilter = new RequestLoggingFilter {
    override implicit def mat: Materializer = prepareMaterializer
    override val logger: Logger             = mockLogger
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    System.setProperty("sbt.log.noformat", "true")
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockLogger)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    reset(mockLogger)
  }

  override def afterAll(): Unit = {
    super.beforeAll()
    System.setProperty("sbt.log.noformat", "false")
  }

  "RequestLoggingFilter" should {
    "log at info level if the request path isn't an asset route" in {
      val request = FakeRequest("GET", "/test-route")
      val testAction = Future(Ok("Test pass"))

      val awaitedResult = await(testFilter.apply(_ => testAction)(request))

      verify(mockLogger, times(1)).info(
        ArgumentMatchers.matches("^GET request to /test-route returned a 200 and took [0-9][0-9]ms$")
      )
    }

    "log nothing if the route contains /assets/" in {
      val request = FakeRequest("GET", "/test-route/assets/")
      val testAction = Future(Ok("Test pass"))

      val awaitedResult = await(testFilter.apply(_ => testAction)(request))

      verifyZeroInteractions(mockLogger)
    }
  }
}
