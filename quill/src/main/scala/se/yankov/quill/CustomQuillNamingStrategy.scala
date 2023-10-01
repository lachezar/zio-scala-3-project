package se.yankov.quill

import io.getquill.{ NamingStrategy, PostgresEscape, SnakeCase }

trait CustomQuillNamingStrategy extends NamingStrategy with PostgresEscape:
  override def table(s: String): String   = default(s.stripSuffix("Entity"))
  override def column(s: String): String  = default(s)
  override def default(s: String): String = SnakeCase.default(s)

object CustomQuillNamingStrategy extends CustomQuillNamingStrategy
