# Fragmenty API

The Fragmenty API provides a RESTful and Websocket interface to access the phone numbers auction data that is collected and stored in a MongoDB database by the [Fragmenty Scrapy project](https://github.com/Maders/fragmenty-spider). The API is built with the Scala programming language and the Play Framework.

## Tech Stack

- Scala 2.13
- Play Framework 2.8
- MongoDB Scala Driver 4.9

## Installation and Setup

1. Clone the repository.

```
git clone https://github.com/Maders/fragmenty-api.git
cd fragmenty-api
```

3. Configure the settings in `conf/application.conf`. In particular, set the `uri` property in the `mongodb` section to the MongoDB connection string.

4. Start the API server.

   `sbt run`
