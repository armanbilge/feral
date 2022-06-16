/*
 * Copyright 2021 Typelevel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package feral.lambda.events

import cats.Show
import cats.kernel.Hash

private[events] object HashShow {

  def by[A, B <: Product](f: A => B)(implicit hashB: Hash[B], showB: Show[B]): HashShow[A] =
    new Hash[A] with Show[A] {

      def hash(x: A): Int = hashB.hash(f(x))

      def eqv(x: A, y: A): Boolean = hashB.eqv(f(x), f(y))

      def show(t: A): String = s"${t.getClass.getSimpleName}${showB.show(f(t))}"
    }

  def narrow[A, B <: A](implicit hs: HashShow[A]): HashShow[B] =
    hs.asInstanceOf[HashShow[B]]

  trait Impl {
    protected def hashShow: HashShow[this.type]

    override def equals(that: Any): Boolean =
      getClass.isInstance(that) && hashShow.eqv(this, that.asInstanceOf[this.type])

    override def hashCode: Int = hashShow.hash(this)

    override def toString: String = hashShow.show(this)

  }

}
