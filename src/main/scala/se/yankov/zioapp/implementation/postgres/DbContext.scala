package se.yankov.zioapp
package implementation
package postgres

import io.getquill.PostgresZioJdbcContext
import se.yankov.quill.CustomQuillNamingStrategy

lazy val DbContext = new PostgresZioJdbcContext(CustomQuillNamingStrategy)
