package se.yankov.zioapp
package implementation
package postgres

import domain.RepositoryError

import java.sql.SQLException

import org.postgresql.util.PSQLException

extension (ex: SQLException)
  def toDbExOrConflict: RepositoryError.DbEx | RepositoryError.Conflict =
    ex match
      case pgEx: PSQLException if pgEx.getSQLState == "23505" => RepositoryError.Conflict()
      case _                                                  => RepositoryError.DbEx(ex)

  def toDbEx: RepositoryError.DbEx = RepositoryError.DbEx(ex)
