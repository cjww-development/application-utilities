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

package com.cjwwdev.scheduling

import akka.actor.ActorSystem
import com.cjwwdev.logging.Logging

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

sealed trait JobCompletionStatus
case object JobComplete extends JobCompletionStatus
case object JobFailed extends JobCompletionStatus
case object JobDisabled extends JobCompletionStatus

trait ScheduledJob extends Logging {
  val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  val jobName: String
  val enabled: Boolean
  val interval: Long

  def scheduledJob: Future[JobCompletionStatus]

  private def jobRunner(f: => Future[JobCompletionStatus]) = {
    actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = interval.second)(f)
  }
}
