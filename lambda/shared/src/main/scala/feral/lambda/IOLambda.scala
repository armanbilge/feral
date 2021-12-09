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

package feral
package lambda

import cats.effect.IO
import cats.effect.IOLocal
import cats.effect.kernel.Resource
import io.circe.Decoder
import io.circe.Encoder

abstract class IOLambda[Event, Result](
    implicit private[lambda] val decoder: Decoder[Event],
    private[lambda] val encoder: Encoder[Result]
) extends IOLambdaPlatform[Event, Result]
    with IOSetup {

  final type Setup = (Event, Context[IO]) => IO[Option[Result]]
  final override protected def setup: Resource[IO, Setup] =
    handler.map { handler => (event, context) =>
      for {
        event <- IOLocal(event)
        context <- IOLocal(context)
        env = LambdaEnv.ioLambdaEnv(event, context)
        result <- handler(env)
      } yield result
    }

  def handler: Resource[IO, LambdaEnv[IO, Event] => IO[Option[Result]]]

}
