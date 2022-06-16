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
import cats.kernel.Order

private[events] object HashOrderShow {

  def by[A, B <: Product](
      f: A => B)(implicit hashB: Hash[B], orderB: Order[B], showB: Show[B]): HashOrderShow[A] =
    new Hash[A] with Order[A] with Show[A] {

      def hash(x: A): Int = hashB.hash(f(x))

      def compare(x: A, y: A): Int = orderB.compare(f(x), f(y))

      def show(t: A): String = s"${t.getClass.getSimpleName}${showB.show(f(t))}"
    }

  def narrow[A, B <: A](implicit hos: HashOrderShow[A]): HashOrderShow[B] =
    hos.asInstanceOf[HashOrderShow[B]]

  trait Impl {
    protected def hashOrderShow: HashOrderShow[this.type]

    override def equals(that: Any): Boolean =
      getClass.isInstance(that) && hashOrderShow.eqv(this, that.asInstanceOf[this.type])

    override def hashCode: Int = hashOrderShow.hash(this)

    override def toString: String = hashOrderShow.show(this)

  }

}
