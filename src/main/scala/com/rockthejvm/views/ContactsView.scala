package com.rockthejvm.views

import com.rockthejvm.domain.data.Contact
import com.rockthejvm.views.htmx.HtmxAttributes
import scalatags.Text
import scalatags.Text.all.*

object ContactsView {
  def fullBody(contacts: List[Contact] = List.empty, page: Int = 0, search: String = "", count: Long = 0): Text.TypedTag[String] = {
    div(searchForm(search), listView(contacts, page, search, count))
  }

  def newContactForm(
      previousFormValues: Map[String, String] = Map.empty,
      errors: Map[String, String] = Map.empty
  ): Text.TypedTag[String] = {
    div(
      `class` := "container",
      form(
        action := "/contacts/new",
        method := "post",
        fieldset(
          legend("Contact Values"),
          p(
            label(`for` := "email", "Email"),
            input(
              name := "email",
              id := "email",
              `type` := "email",
              placeholder := "Email",
              value := previousFormValues.getOrElse("email", "")
            ),
            errors.get("email").map(email => span(cls := "error", email))
          ),
          p(
            label(`for` := "name", "Name"),
            input(name := "name", id := "name", `type` := "text", placeholder := "Name", value := previousFormValues.getOrElse("name", "")),
            errors.get("name").map(name => span(cls := "error", name))
          ),
          p(
            label(`for` := "phone", "Phone"),
            input(
              name := "phone",
              id := "phone",
              `type` := "text",
              placeholder := "Phone",
              value := previousFormValues.getOrElse("phone", "")
            ),
            errors.get("phone").map(phone => span(cls := "error", phone))
          ),
          button("Save")
        )
      ),
      p(
        a(href := "/contacts", "Back")
      )
    )
  }

  private def searchForm(formInputValue: String): Text.TypedTag[String] = form(
    action := "/contacts",
    method := "get",
    `class` := "tool-bar",
    h1(
      `for` := "search",
      "Neon Pages - a Scala + HTMX demo"
    ),
    div(
      style := "display: flex",
      input(
        id := "search-input",
        `type` := "search",
        style := "flex: 1; margin-right: 20px",
        name := "q",
        value := formInputValue,
        HtmxAttributes.get("/contacts"),
        HtmxAttributes.trigger("search, keyup delay:200ms changed"),
        HtmxAttributes.target("tbody"),
        HtmxAttributes.pushUrl(),
        HtmxAttributes.indicator(),
        HtmxAttributes.select("tbody tr")
      ),
      input(
        `type` := "submit",
        id := "search-submit",
        style := "flex: 0 0 100px",
        value := "Search"
      )
    )
  )

  def listView(contacts: List[Contact], page: Int, searchTerm: String, count: Long) = div(
    `class` := "container",
    form(
      table(
        `class` := "table",
        thead(
          tr(
            th(),
            th("Name"),
            th("Phone"),
            th("Email")
          )
        ),
        tbody(
          contacts.map(c =>
            tr(
              td(input(`type` := "checkbox", name := "selected_contact_ids", value := c.id)),
              td(c.name),
              td(c.email),
              td(c.phone),
              td(a(href := s"/contacts/${c.id}/edit", "Edit")),
              td(a(href := s"/contacts/${c.id}", "View")),
              td(
                a(
                  href := "#",
                  HtmxAttributes.delete(s"/contacts/${c.id}"),
                  HtmxAttributes.swap("outerHTML swap:1s"),
                  HtmxAttributes.confirm("Are you sure you want to delete this contact?"),
                  HtmxAttributes.target("closest tr"),
                  "Delete"
                )
              )
            ),
          )
        )
      ),
      div(
        style := "display: flex; justify-content: space-between",
        button(
          style := "width: 160px",
          HtmxAttributes.get(s"/contacts?page=${page}&q=${searchTerm}"),
          HtmxAttributes.target("closest tr"),
          HtmxAttributes.swap("outerHTML"),
          HtmxAttributes.select("tbody > tr"),
          "Load More"
        ),
        button(
          style := "width: 280px",
          HtmxAttributes.delete("/contacts"),
          HtmxAttributes.confirm("Are you sure you want to delete these contacts?"),
          HtmxAttributes.target("body"),
          "Delete Selected Contacts"
        )
      )
    ),
    p(
      a(href := "/contacts/new", "Add Contact"),
      span(s" (${count} total Contacts)")
    )
  )

  def editContact(contact: Contact, errMap: Map[String, String] = Map.empty) = div(
    `class` := "container",
    form(
      action := s"/contacts/${contact.id}/edit",
      method := "post",
      fieldset(
        legend("Contact Values"),
        p(
          label(`for` := "email", "Email"),
          input(
            name := "email",
            id := "email",
            `type` := "text",
            placeholder := "Email",
            HtmxAttributes.get(s"/contacts/${contact.id}/email"),
            HtmxAttributes.trigger("change, keyup delay:200ms changed"),
            HtmxAttributes.target("next .error"),
            value := contact.email
          ),
          span(cls := "error", errMap.getOrElse("email", ""))
        ),
        p(
          label(`for` := "name", "Name"),
          input(name := "name", id := "name", `type` := "text", placeholder := "Name", value := contact.name),
          errMap.get("name").map(name => span(cls := "error", name))
        ),
        p(
          label(`for` := "phone", "Phone"),
          input(name := "phone", id := "phone", `type` := "text", placeholder := "Phone", value := contact.phone),
          errMap.get("phone").map(phone => span(cls := "error", phone)),
          button("Save")
        )
      )
    ),
    button(
      id := "delete-btn",
      HtmxAttributes.delete(s"/contacts/${contact.id}"),
      HtmxAttributes.pushUrl(),
      HtmxAttributes.confirm(s"Are you sure you want to delete ${contact.name}"),
      HtmxAttributes.target("body"),
      "Delete Contact"
    ),
    p(
      a(href := "/contacts/", "Back")
    )
  )

  def viewContact(contact: Contact) = div(
    h1(contact.name),
    div(
      div(s"Phone: ${contact.phone}"),
      div(s"Email: ${contact.email}")
    ),
    p(
      a(href := s"/contacts/${contact.id}/edit", "Edit"),
      a(href := "/contacts", "Back")
    )
  )
}
