package com.rockthejvm.controllers

import zio.http.URL
import java.net.URI

object Redirects {
  
  val contacts = URL.fromURI(URI.create("/contacts")).get
  
  def editContact(id: Long) = contacts.addPath(s"${id}/edit")
}
