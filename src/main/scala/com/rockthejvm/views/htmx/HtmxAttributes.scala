package com.rockthejvm.views.htmx

import scalatags.Text.all.*

object HtmxAttributes {
  def boost(value: Boolean = true) = attr("hx-boost") := value
  def pushUrl(value: Boolean = true) = attr("hx-push-url") := value
  def confirm(message: String) = attr("hx-confirm") := message

  def delete(endpoint: String) = attr("hx-delete") := endpoint
  def get(endpoint: String) = attr("hx-get") := endpoint
  def indicator(indicatorType: String = "#spinner") = attr("hx-indicator") := indicatorType
  def select(value: String) = attr("hx-select") := value
  def swap(value: String) = attr("hx-swap") := value

  def target(element: String) = attr("hx-target") := element
  def trigger(value: String) = attr("hx-trigger") := value
}
