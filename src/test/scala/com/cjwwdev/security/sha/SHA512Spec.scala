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

package com.cjwwdev.security.sha

import org.scalatestplus.play.PlaySpec

class SHA512Spec extends PlaySpec {
  
  val testSec = SHA512

  "sha512" should {
    "return a string exactly 128 chars long and isnt equal to what was input" in {
      val result = testSec.encrypt("testString")
      result.length mustBe 128
      assert(result != "testString")
    }

    "encrypt a string that is five chars long and still return a 128 char string" in {
      val result = testSec.encrypt("aaaaa")
      result.length mustBe 128
    }

    "encrypt a string that is ten chars long and still return a 128 char string" in {
      val result = testSec.encrypt("aaaaaaaaaa")
      result.length mustBe 128
    }

    "encrypt a string that is fifty chars long and still return a 128 char string" in {
      val result = testSec.encrypt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
      result.length mustBe 128
    }

    "encrypt a string that is one hundred chars long and still return a 128 char string" in {
      val result = testSec.encrypt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
      result.length mustBe 128
    }

    "encrypt a string that is two hundred chars long and still return a 128 char string" in {
      val result = testSec.encrypt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
      result.length mustBe 128
    }
  }
}
