package it.almawave.linkeddata.kb.utils

import java.net.URI
import scala.reflect._
import scala.reflect.runtime.universe._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.beans.BeanProperty

/**
 * This object collects some useful methods for case class to map conversion (back and forth).
 *
 * SEE: https://stackoverflow.com/a/24100624/1202374
 * SEE: https://stackoverflow.com/a/1227643/1202374
 * SEE: https://stackoverflow.com/a/25894047/1202374
 * 
 * ASK: how to handle Java Bean?
 * TODO: handle fields with @BeanProperty! (for Java Bean compatibility)
 * 
 * TODO: move it inside the common library kbaselib
 */
object ModelAdapter {

  /**
   * case class to map: creates a new Map, based on the content a given case class
   * (each case class field will be used to create a corresponding key in the Map)
   */
  def toMap(cc: Product): Map[String, Any] = {
    val values = cc.productIterator
    cc.getClass.getDeclaredFields.map {
      _.getName -> (values.next() match {
        case p: Product if p.productArity > 0 => toMap(p)
        case x                                => x
      })
    }.toMap
  }

  /**
   * map to case class: creates a new instance of a given case class,
   * using values from the input Map.
   * NOTE:
   * 	- each key in the map is mapped to a case class field,
   * 	- if there is no field which corresponds to a map key, it will be ignored (not mapped/used)
   */
  def fromMap[T: TypeTag: ClassTag](m: Map[String, _]) = {

    val rm = runtimeMirror(classTag[T].runtimeClass.getClassLoader)
    val classTest = typeOf[T].typeSymbol.asClass
    val classMirror = rm.reflectClass(classTest)
    val constructor = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod
    val constructorMirror = classMirror.reflectConstructor(constructor)

    val constructorArgs = constructor.paramLists.flatten.map((param: Symbol) => {
      val paramName = param.name.toString
      // if a parameter is optional (it can be omitted!)
      if (param.typeSignature <:< typeOf[Option[Any]]) m.get(paramName)
      // else take the required parameter, or throw an exception!
      else m.get(paramName)
        .getOrElse(throw new IllegalArgumentException(s"Map is missing required parameter ${paramName}!"))
    })

    constructorMirror(constructorArgs: _*).asInstanceOf[T]
  }

}