package com.rockthejvm.views

import com.rockthejvm.views.htmx.HtmxAttributes
import scalatags.Text.TypedTag
import scalatags.Text.all.*

object HomePage {
  def generate(bodyContents: TypedTag[String]): TypedTag[String] = generate(List(bodyContents))
  def generate(bodyContents: List[TypedTag[String]] = List.empty): TypedTag[String] = {
    html(
      head(
        link(rel := "stylesheet", href := "https://cdn.jsdelivr.net/npm/@picocss/pico@1/css/pico.min.css"),
        script(src := "https://unpkg.com/htmx.org@1.9.10"),
        link(rel := "stylesheet", href := "/static/css/main.css")
      ),
      body(
        `class` := "container",
        div(
          HtmxAttributes.boost(),
          bodyContents
        )
      )
    )
  }
}
