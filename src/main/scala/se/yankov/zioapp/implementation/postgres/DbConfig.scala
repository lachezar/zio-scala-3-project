package se.yankov.zioapp
package implementation
package postgres

final case class DbConfig(driver: String, url: String, user: String, password: String, maxPoolSize: Int)
