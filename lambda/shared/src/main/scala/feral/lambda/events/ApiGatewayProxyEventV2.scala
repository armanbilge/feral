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

package feral.lambda
package events

import io.circe.Decoder
import natchez.Kernel

final class Http private (val method: String) extends HashOrderShow.Impl {
  def hashOrderShow = HashOrderShow.narrow[Http, this.type]
}

object Http {
  def apply(method: String): Http = new Http(method)

  implicit val decoder: Decoder[Http] = Decoder.forProduct1("method")(Http.apply)

  implicit val hashOrderShow: HashOrderShow[Http] =
    HashOrderShow.by(http => Tuple1(http.method))
}

final class RequestContext(val http: Http) extends HashOrderShow.Impl {
  def hashOrderShow = HashOrderShow.narrow[RequestContext, this.type]
}

object RequestContext {
  def apply(http: Http): RequestContext = new RequestContext(http)

  implicit val decoder: Decoder[RequestContext] =
    Decoder.forProduct1("http")(RequestContext.apply)

  implicit val hashOrderShow: HashOrderShow[RequestContext] =
    HashOrderShow.by(reqCtx => Tuple1(reqCtx.http))
}

final class ApiGatewayProxyEventV2(
    val rawPath: String,
    val rawQueryString: String,
    val cookies: Option[List[String]],
    val headers: Map[String, String],
    val requestContext: RequestContext,
    val body: Option[String],
    val isBase64Encoded: Boolean
) extends HashShow.Impl {
  def hashShow = HashShow.narrow[ApiGatewayProxyEventV2, this.type]
}

object ApiGatewayProxyEventV2 {
  def apply(
      rawPath: String,
      rawQueryString: String,
      cookies: Option[List[String]],
      headers: Map[String, String],
      requestContext: RequestContext,
      body: Option[String],
      isBase64Encoded: Boolean
  ) = new ApiGatewayProxyEventV2(
    rawPath,
    rawQueryString,
    cookies,
    headers,
    requestContext,
    body,
    isBase64Encoded
  )

  implicit def decoder: Decoder[ApiGatewayProxyEventV2] = Decoder.forProduct7(
    "rawPath",
    "rawQueryString",
    "cookies",
    "headers",
    "requestContext",
    "body",
    "isBase64Encoded"
  )(ApiGatewayProxyEventV2.apply)

  implicit def hashShow: HashShow[ApiGatewayProxyEventV2] =
    HashShow.by { ev =>
      (
        ev.rawPath,
        ev.rawQueryString,
        ev.cookies,
        ev.headers,
        ev.requestContext,
        ev.body,
        ev.isBase64Encoded
      )
    }

  implicit def kernelSource: KernelSource[ApiGatewayProxyEventV2] = e => Kernel(e.headers)
}
