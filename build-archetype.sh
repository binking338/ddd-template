#!/usr/bin/env bash

mvn clean archetype:create-from-project

rm -f target/**/*.iml
rm -rf target/**/.idea/
rm -rf target/generated-sources/archetype/**/archetype/
cp -f archetype/archetype-metadata.xml target/generated-sources/archetype/src/main/resources/META-INF/maven/archetype-metadata.xml
cp -f .gitignore target/generated-sources/archetype/src/main/resources/archetype-resources/.gitignore

insert_escape_def() {
    sed -i '' '1i\
#set( $symbol_escape = '"'"'\\'"'"' )
' $1
    sed -i '' '1i\
#set( $symbol_dollar = '"'"'$'"'"' )
' $1
    sed -i '' '1i\
#set( $symbol_pound = '"'"'#'"'"' )
' $1
}
escape_symbol() {
    sed -i '' 's#\$#${symbol_dollar}#g' $1
    sed -i '' 's/#/${symbol_pound}/g' $1
    sed -i '' 's/\\/${symbol_escape}/g' $1
}
escape_project_name() {
    sed -i '' 's#ddd-template#${artifactId}#g' $1
    sed -i '' 's/com.abc.dddtemplate/${package}/g' $1
}
to_template() {
    escape_symbol $1
    escape_project_name $1
    insert_escape_def $1
}

to_template target/generated-sources/archetype/src/main/resources/archetype-resources/README.MD
to_template target/generated-sources/archetype/src/main/resources/archetype-resources/Dockerfile
to_template target/generated-sources/archetype/src/main/resources/archetype-resources/Dockerfile-sonar
to_template target/generated-sources/archetype/src/main/resources/archetype-resources/.gitlab-ci.yml

to_template target/generated-sources/archetype/src/main/resources/archetype-resources/.helm/Chart.yaml
to_template target/generated-sources/archetype/src/main/resources/archetype-resources/.helm/values.yaml

escape_project_name target/generated-sources/archetype/src/main/resources/archetype-resources/__rootArtifactId__-start/pom.xml

cd target/generated-sources/archetype
mvn clean install
cd ../../../

#mvn clean
