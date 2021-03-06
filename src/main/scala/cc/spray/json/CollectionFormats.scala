/*
 * Original implementation (C) 2009-2011 Debasish Ghosh
 * Adapted and extended in 2011 by Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.spray.json

trait CollectionFormats {

  /**
    * Supplies the JsonFormat for Lists.
   */
  implicit def listFormat[T :JsonFormat] = new JsonFormat[List[T]] {
    def write(list: List[T]) = JsArray(list.map(_.toJson))
    def read(value: JsValue) = value match {
      case JsArray(elements) => elements.map(_.fromJson[T])
      case _ => throw new DeserializationException("List expected")
    }
  }
  
  /**
    * Supplies the JsonFormat for Arrays.
   */
  implicit def arrayFormat[T :JsonFormat :ClassManifest] = new JsonFormat[Array[T]] {
    def write(array: Array[T]) = JsArray(array.map(_.toJson).toList)
    def read(value: JsValue) = value match {
      case JsArray(elements) => elements.map(_.fromJson[T]).toArray[T]
      case _ => throw new DeserializationException("Array expected")
    }
  }
  
  /**
    * Supplies the JsonFormat for Maps. The implicitly available JsonFormat for the key type K must
    * always write JsStrings, otherwise a [[cc.spray.json.SerializationException]] will be thrown.
   */
  implicit def mapFormat[K :JsonFormat, V :JsonFormat] = new JsonFormat[Map[K, V]] {
    def write(m: Map[K, V]) = JsObject {
      m.toList.map { t =>
        t._1.toJson match {
          case JsString(x) => JsField(x, t._2.toJson)
          case x => throw new SerializationException("Map key must be formatted as JsString, not '" + x + "'")
        }
      }
    }
    def read(value: JsValue) = value match {
      case JsObject(fields) => fields.map(field => (JsString(field.name).fromJson[K], field.value.fromJson[V])).toMap
      case _ => throw new DeserializationException("Map expected")
    }
  }

  import collection.{immutable => imm}

  implicit def immIterableFormat[T :JsonFormat]   = viaList[imm.Iterable[T], T](list => imm.Iterable(list :_*))
  implicit def immSeqFormat[T :JsonFormat]        = viaList[imm.Seq[T], T](list => imm.Seq(list :_*))
  implicit def immIndexedSeqFormat[T :JsonFormat] = viaList[imm.IndexedSeq[T], T](list => imm.IndexedSeq(list :_*))
  implicit def immLinearSeqFormat[T :JsonFormat]  = viaList[imm.LinearSeq[T], T](list => imm.LinearSeq(list :_*))
  implicit def immSetFormat[T :JsonFormat]        = viaList[imm.Set[T], T](list => imm.Set(list :_*))
  implicit def vectorFormat[T :JsonFormat]        = viaList[Vector[T], T](list => Vector(list :_*))

  import collection._

  implicit def iterableFormat[T :JsonFormat]   = viaList[Iterable[T], T](list => Iterable(list :_*))
  implicit def seqFormat[T :JsonFormat]        = viaList[Seq[T], T](list => Seq(list :_*))
  implicit def indexedSeqFormat[T :JsonFormat] = viaList[IndexedSeq[T], T](list => IndexedSeq(list :_*))
  implicit def linearSeqFormat[T :JsonFormat]  = viaList[LinearSeq[T], T](list => LinearSeq(list :_*))
  implicit def setFormat[T :JsonFormat]        = viaList[Set[T], T](list => Set(list :_*))

  /**
    * A JsonFormat construction helper that creates a JsonFormat for an Iterable type I from a builder function
    * List => I.
   */
  def viaList[I <: Iterable[T], T :JsonFormat](f: List[T] => I): JsonFormat[I] = new JsonFormat[I] {
    def write(iterable: I) = JsArray(iterable.map(_.toJson).toList)
    def read(value: JsValue) = value match {
      case JsArray(elements) => f(elements.map(_.fromJson[T]))
      case _ => throw new DeserializationException("Collection expected")
    }
  }

}