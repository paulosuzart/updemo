# updemo

Serves a bunch of json through a simple REST api.

# Solution

## What is the context?

A couple of questions can steer the solution to hundreds of different directions depending on the answer. The questions include:

1. Data ingestion frequency (batch? one by one? async?)
1. Can this data be snapshot and served for a while? Like being update every now and then?
1. What is the volume of querying?
1. Does this data need HA? Multi datacenter replication? Sharding? Multi master?
1. Is it small enough to fit in memory or embedded disk databases on each node?
1. Store all json now or just the relevant for the API? Each path brings completely different consequences
1. The questions goes on...

Whatever assumption from my side wold be leaving a whole lot of other consideration behind, that's why I did a very non orthodox thing.

## What I did

It's possible that data are extracted from a data wharehouse or some golden source every now and then, and served continuously until another snapshot is generated.
Some cases the data either fits in memory, or if it does'nt, a disk based embedded database can do the trick and multiple instances of the application can be spin up to serve the data either sharing via a network file system or just as isolated nodes.

This application does exactly this. Takes a json file in a given path and serves it according to the API availabe [via swagger UI](http://localhost:8080/swagger-ui.html). No insertion is provided, although possible as is.

## Technology used

Data transformation is done using [JOL](https://github.com/bazaarvoice/jolt) at application startup. If the ingestion happened through workers for a Queue (Kafka, Pub/Sub, RabbitMQ), the same transformation spec could be reused.

The spec below can be customized to accommodate new fields or different sources:

```json
[{
  "operation": "shift",
  "spec": {
    "*": {
      "features": {
        "*": {
          "properties": {
            "id": "[&4].id",
            "timestamp": "[&4].timestamp",
            "quicklook": "[&4].quicklook",
            "acquisition": {
              "endViewingDate": "[&5].endViewingDate",
              "beginViewingDate": "[&5].beginViewingDate",
              "missionName": "[&5].missionName"
            }
          }
        }
      }
    }
  }
}]
```

After data is transformed, it's loaded into a [CQEngine](https://github.com/npgall/cqengine) [ConcurrentIndexedCollection](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/cqengine/master/documentation/javadoc/apidocs/com/googlecode/cqengine/ConcurrentIndexedCollection.html). Two attributes are used for indexing:

```java
  // The id is indexe through a Unique Index
  public static final Attribute<Feature, UUID> ID = new SimpleAttribute<>("featureId") {
    @Override
    public UUID getValue(Feature o, QueryOptions queryOptions) {
      return o.id;
    }
  };
	
	// And the timestamp is indexed through a Navigable Index for ordering.
  public static final Attribute<Feature, Long> TIMESTAMP = new SimpleAttribute<>("timestamp") {
    @Override
    public Long getValue(Feature o, QueryOptions queryOptions) {
      return o.getTimestamp();
    }
  };
```

The advantage is that querying or sorting by new fields requires just new index against the collection and the library is extremely fast. It can persist both data and index on disk if needed. But for this demo all is in memory.

Also, data is stored as is in memory and could be served as is by keeping the `quicklook` out of the main `Feature` object, in a separated collection perhaps encoded to `byte[]`.
 
# Running

As there's no other dependency other the application jar itself, it's quite simple:

```$bash
 UPDEMO_SOURCE=file:///Users/paulosuzart/Downloads/source-data.json ./mvnw spring-boot:run
```

Then browse to [Swagger](http://localhost:8080/swagger-ui.html) or issue your http requests to `http://localhost:8080/features`.

## General notes

I tried to pretend I was doing git for real, but there's no CI then some commits went straight to master. Other then that I created couple branches that are in the history. Then they were squashed to master.

Tests are trivial, mostly happy path. No Data Providers or intricate mocks and `verifies`.

It took me much more than 3 hours especially because I spent 3 hours trying to understando how this JOLT spec worked and din't want to fallback to another solution like couchbase that would open the door to many of the topics in the first session.



   