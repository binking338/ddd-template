#!/usr/bin/env bash

rm -f ./*-adapter/src/main/java/com/abc/*/adapter/external/**/*.java
rm -f ./*-adapter/src/main/java/com/abc/*/adapter/external/clients/rest/ServiceMockRest.java
rm -f ./*-adapter/src/main/java/com/abc/*/adapter/domain/**/*.java
rm -f ./*-adapter/src/main/java/com/abc/*/adapter/portal/api/controller/app/AccountController.java
rm -f ./*-adapter/src/main/java/com/abc/*/adapter/portal/api/controller/app/BillController.java
rm -f ./*-adapter/src/main/java/com/abc/*/adapter/portal/api/controller/app/OrderController.java
rm -f ./*-adapter/src/main/java/com/abc/*/adapter/portal/jobs/*.java
rm -f ./*-adapter/src/main/java/com/abc/*/adapter/portal/queues/*.java

rm -f ./*-application/src/main/java/com/abc/*/convention/schemas/*.java
rm -f ./*-application/src/main/java/com/abc/*/application/**/*.java
rm -f ./*-application/src/main/java/com/abc/*/application/**/**/*.java
rm -rf ./*-application/src/main/java/com/abc/*/application/commands/account
rm -rf ./*-application/src/main/java/com/abc/*/application/commands/bill
rm -rf ./*-application/src/main/java/com/abc/*/application/commands/order

rm -f ./*-domain/src/main/java/com/abc/*/domain/**/*.java
rm -f ./*-domain/src/main/java/com/abc/*/domain/**/**/*.java
rm -rf ./*-domain/src/main/java/com/abc/*/domain/aggregates/relationsamples
rm -rf ./*-domain/src/main/java/com/abc/*/domain/aggregates/samples

rm -f ./*-external/src/main/java/com/abc/*/external/**/*.java

rm -f ./ddl.sql