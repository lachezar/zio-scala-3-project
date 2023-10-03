# ZIO 2 + Scala 3 project template

This project can be used as a starting point for a ZIO 2 + Scala 3 web service with public, private and internal APIs served on different ports. The architecture is 3 layered - Domain, Implementation and API layers. The Domain layer should be used for business logic only, the Implementation should hold the actual implementation (e.g. code that interacts with Postgres or Kafka) and the API layer is responsible for http communication with the outside world.

## Notable dependencies

- Chimney (transformations between domain / dto / dao objects)
- Flyway (database migrations)
- zio-http
- zio-json
- zio-kafka
- zio-prelude (validation of inputs)
- zio-quill (type safe sql queries)

## SBT Plugins

- sbt-dependency-updates (check for new versions of the project dependencies)
- sbt-dotenv (load configuration from a `.env` file automatically when running `sbt run`)
- sbt-native-packager (package the compiled code / add required scripts to run it)
- sbt-scalafmt (code formatter)
- sbt-tpolecat (good scala compiler options defaults)
- sbt-wartremover (linter)

## What is missing?

Currently there is no way to define OpenAPI specification for the web APIs, since zio-http does not have support for this (see https://github.com/zio/zio-http/issues/1520 and https://github.com/zio/zio-http/issues/1498). It is possible to use Tapir if OpenAPI specification is a must.

## How to run it

You can start the Kafka (Redpanda) and Postgres using `docker compose up` and then start the application with `sbt run`. You might need to adjust the configuration of the project using a `.env` file (see the `.env.example`) or editing the `src/main/resource/application.conf`.

## Example usage

Create an item:

```
curl -v --data '{"name":"lego", "price": 24.95, "productType":"Toys"}' -H "Authorization: Bearer tokenhere" -H "Content-Type: application/json" localhost:1338/items
```

View an item by id:

```
curl -v -H "Authorization: Bearer tokenhere" -H "Content-Type: application/json" localhost:1338/items/<ITEM ID HERE>
```

Update an item by id:

```
curl -v -X PUT --data '{"name":"Gameboy", "price": 199.95, "productType":"Electronics"}' -H "Authorization: Bearer tokenhere" -H "Content-Type: application/json" localhost:1338/items/<ITEM ID HERE>
```

Delete an item by id:

```
curl -v -X DELETE -H "Authorization: Bearer tokenhere" -H "Content-Type: application/json" localhost:1338/items/<ITEM ID HERE>
```