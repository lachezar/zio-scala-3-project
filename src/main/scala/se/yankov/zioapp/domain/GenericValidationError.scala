package se.yankov.zioapp
package domain

trait GenericValidationError:
  def getMessage: String
