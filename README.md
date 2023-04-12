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

## Related Project:

- The [`fragmenty`](https://github.com/Maders/fragmenty) repository contains the infrastructure provisioning code for the Fragmenty project using Amazon Web Services (AWS). The infrastructure is organized into different components.

- The [`fragmenty-spider`](https://github.com/Maders/fragmenty-spider) repository contains the web crawler code for the Fragmenty project. The web crawler is built with the Scrapy framework.
