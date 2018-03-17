package util

import kamon.context.{Codecs, Context, TextMap}

// used to disable span
class NoopCodec extends Codecs.ForEntry[TextMap] {
  override def encode(context: Context): TextMap = TextMap.Default()
  override def decode(carrier: TextMap, context: Context): Context = context
}
