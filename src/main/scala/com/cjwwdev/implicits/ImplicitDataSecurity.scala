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

import com.cjwwdev.security.deobfuscation.{DeObfuscator, DecryptionError}
import com.cjwwdev.security.obfuscation.Obfuscator
import com.cjwwdev.security.sha.SHA512

object ImplicitDataSecurity {
  implicit class ImplicitObfuscation[T](data: T)(implicit obfuscation: Obfuscator[T]) {
    def encrypt: String = obfuscation.encrypt(data)
  }

  implicit class ImplicitDeObfuscation(data: String) {
    def decrypt[T](implicit deObfuscation: DeObfuscator[T]): Either[T, DecryptionError] = deObfuscation.decrypt(data)
  }

  implicit class ImplicitSHA512(data: String) {
    def sha512: String = SHA512.encrypt(data)
  }
}
