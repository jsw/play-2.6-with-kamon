package util

import play.api.libs.typedmap.TypedKey

object RequestAttrKey {
  val RequestId = TypedKey[String](ContextNames.RequestId)
}
