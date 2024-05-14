package com.rockthejvm.domain.data

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonCodec, JsonDecoder, JsonEncoder}

case class Contact(
    id: Long,
    name: String,
    phone: String,
    email: String
) derives JsonCodec
