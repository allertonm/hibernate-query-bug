# hibernate-query-bug

This project demonstrates a bug in Quarkus 3.1 that causes the error "HR000069: Detected use of the reactive Session from a different Thread" when two simultaneous requests are received.

This bug does not appear to be reproducible on Quarkus 3.2, but there do not appear to be any closed issues related to HR000069 that affect 3.2 but not 3.1.

The reproduction requires the following...
1. A persisted entity (BarEntity in this example) with a field whose value is an instance of a class (Foo2, here) that does not implement equals/hashcode
2. An endpoint that queries for that entity twice in succession within the same session

To reproduce the problem, checkout the `quarkus-3.1` branch and run the application using `./gradlew quarkusDev` (requires a Postgres server with a DB "bug_test" on localhost:5432)

In a shell session, create the entity...
```
% curl http://localhost:8083/create2 
Created 01f9f686-f47a-44e0-9727-d58cb3c54216%
```

Copy the UUID written to stdout and then...

```
seq 1 100 | xargs -P2 -Iname curl http://localhost:8083/read/9f99c6da-98a0-49dc-ba08-79601f5fe7d3
```

You should hit HR000069 almost immediately, usually on the first two requests.

You will not hit this error if, instead of using `create2`, you use `create1` or `create3`, which set the value of `BarEntity.foo` to be either a Kotlin data class instance or an instance of a class with `equals` and `hashCode` implemnented.

The `main` branch of this repo uses Quarkus 3.2 and also does not exhibit this problem.


