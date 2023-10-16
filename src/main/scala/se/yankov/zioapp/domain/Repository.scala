package se.yankov.zioapp
package domain

trait Repository:
  // For some reason the scala's own `identity` does not compile with Quill
  inline final def identity[A]: A => A = a => a
