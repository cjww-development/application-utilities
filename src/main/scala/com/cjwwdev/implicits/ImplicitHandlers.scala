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

package com.cjwwdev.implicits

import com.cjwwdev.security.encryption.DataSecurity
import play.api.libs.json._

trait ImplicitHandlers {

  implicit class ImplicitDataSecurityHandlers(string: String) {
    def encrypt: String = DataSecurity.encryptString(string)
    def decrypt: String = DataSecurity.decryptString(string)

    def decryptType[T](implicit format: Format[T]): T =
      DataSecurity.decryptIntoType[T](string)(format).get
  }

  implicit class ImplicitGenericTypeHandler[T](typeT: T)(implicit format: Format[T]) {
    def encryptType: String = DataSecurity.encryptType[T](typeT)(format)
  }

  implicit class ImplicitJsValueHandlers(jsValue: JsValue) {
    def get[T](key: String)(implicit reads: Reads[T]): T = jsValue.\(key).getOrThrow(new NoSuchElementException(s"No data found for key '$key'"))
    def getOption[T](key: String)(implicit reads: Reads[T]): Option[T] = jsValue.\(key).asOpt[T]

    def getFromLevel[T](keys: String*)(implicit reads: Reads[T]): T = {
      def leftOrRight(json: JsValue, int: Int): T = json.\(keys(int)).validate[T] match {
        case JsSuccess(x,_) => x
        case JsError(_)     => leftOrRight(json.get[JsValue](keys(int)),int+1)
      }
      leftOrRight(jsValue, int = 0)
    }
  }

  implicit class JsLookupResultExtensions(lookupResult: JsLookupResult) {
    def getOrThrow[T](orElse: Throwable)(implicit reads: Reads[T]): T = lookupResult.validate[T] match {
      case JsSuccess(x,_) => x
      case JsError(_)     => throw orElse
    }
  }
}
