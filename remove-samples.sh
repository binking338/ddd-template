#!/usr/bin/env bash

rm -f ./*-adapter/src/main/java/com/*/*/adapter/external/clients/*.java
rm -f ./*-adapter/src/main/java/com/*/*/adapter/external/clients/*/*.java
rm -f ./*-adapter/src/main/java/com/*/*/adapter/domain/repositories/*.java
rm -f ./*-adapter/src/main/java/com/*/*/adapter/application/queries/*.java
rm -f ./*-adapter/src/main/java/com/*/*/adapter/portal/api/controller/AccountController.java
rm -f ./*-adapter/src/main/java/com/*/*/adapter/portal/api/controller/BillController.java
rm -f ./*-adapter/src/main/java/com/*/*/adapter/portal/api/controller/OrderController.java
#rm -f ./*-adapter/src/main/java/com/*/*/adapter/portal/jobs/*.java
#rm -f ./*-adapter/src/main/java/com/*/*/adapter/portal/queues/*.java

rm -f ./*-application/src/main/java/com/*/*/convention/schemas/*.java
rm -f ./*-application/src/main/java/com/*/*/application/**/*.java
rm -f ./*-application/src/main/java/com/*/*/application/subscribers/**/*.java
rm -rf ./*-application/src/main/java/com/*/*/application/commands/account
rm -rf ./*-application/src/main/java/com/*/*/application/commands/bill
rm -rf ./*-application/src/main/java/com/*/*/application/commands/order

rm -rf ./*-domain/src/main/java/com/*/*/domain/aggregates/relationsamples
rm -rf ./*-domain/src/main/java/com/*/*/domain/aggregates/samples
rm -f ./*-domain/src/main/java/com/*/*/domain/events/**/*.java
rm -f ./*-domain/src/main/java/com/*/*/domain/services/*.java

rm -f ./*-external/src/main/java/com/*/*/external/**/*.java

rm -f ./ddl.sql