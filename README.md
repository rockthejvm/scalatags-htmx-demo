# ScalaTags + htmx + ZIO HTTP Demo

This is a small demo project showcasing an integration between
- ZIO HTTP
- ScalaTags
- htmx

Welcome to Neon Pages: an extremely minimal CRM where you can keep a database of people with name, email, phone number or other data. You can add, remove, edit and search for people in the web app, and bulk-edit/bulk-delete.

This is a single-page application (SPA) built with ZIO, ScalaTags and htmx.

## How to run

This demo project has a main class in `Main.scala`, so you can run the project by

```
sbt run
```

## How to develop

Fork or clone this repo and in an SBT console do

```
~compile
```

and SBT will pick up your changes as you develop. The server will have to be restarted.

## Structure

This application is built with
- a SQLite store, which can be replaced by Postgres or some other database
- [Quill](https://zio.dev/zio-quill/) for type-safe queries
- [ZIO](https://zio.dev/) for managing effects
- [ZIO HTTP](https://zio.dev/zio-http/) for server endpoints
- [ScalaTags](https://com-lihaoyi.github.io/scalatags/) for server-side rendering (SSR)
- [htmx](https://htmx.org/), a JavaScript library which manages classic single-page application (SPA) flows and backend calls via custom HTML attributes

The "architecture" of the application is layered using ZLayers:
- in `db` we have layers for the Quill data sources
- `repositories` is layer for type-safe CRUD operations we're interested in, created with Quill, based on the `db` layer
- `services` is a layer dedicated to business logic, usually with one or more actions from `repositories`
- `controllers` is a layer for HTTP endpoints and request/response handling logic, usually delegating to one or more `services`
- on top of that, `views` is the Scala representation of the web pages (with ScalaTags and htmx attributes), which we serve directly

Besides the classical layers, we have a `domain` for the data types and errors we're using in the app, and a `config` layer which can fetch configuration from `application.conf`. The application also contains a `Flyway` service for migrations.
