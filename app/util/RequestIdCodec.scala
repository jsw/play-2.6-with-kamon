package util

import kamon.context.{Codecs, Context, Key, TextMap}

class RequestIdCodec extends Codecs.ForEntry[TextMap] {

  private val key = Key.broadcast("request-id", "undefined")
  private val headerName = "Ebay-N-Request-Id"

  override def encode(context: Context): TextMap = {
    val m = TextMap.Default()
    val requestId = context.get(key)
    m.put(headerName, requestId)
    m
  }

  override def decode(carrier: TextMap, context: Context): Context = {
    val incomingRequestId = carrier.values.find(_._1.equalsIgnoreCase(headerName)).map(_._2)
    val requestId = RequestIdUtil.newRequestId(incomingRequestId)
    context.withKey(key, requestId)
  }
}
