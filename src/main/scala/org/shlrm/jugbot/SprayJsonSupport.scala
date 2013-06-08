package org.shlrm.jugbot

/**
 * It appears that this is still a problem:
 * https://groups.google.com/d/msg/spray-user/Kt4V7yO9xQU/VD1QVTr_oeQJ
 * see the spray issue: https://github.com/spray/spray-json/issues/31
 */

trait SprayJsonSupport extends spray.httpx.SprayJsonSupport {

  import spray.json._

  implicit object JsObjectWriter extends RootJsonFormat[JsObject] {
    def write(jsObject: JsObject) = jsObject

    def read(value: JsValue) = value.asJsObject
  }

  implicit object JsArrayWriter extends RootJsonFormat[JsArray] {
    def write(jsArray: JsArray) = jsArray

    def read(value: JsValue) = value.asInstanceOf[JsArray]
  }

}

object SprayJsonSupport extends SprayJsonSupport