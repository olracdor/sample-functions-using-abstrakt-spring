# Introduction 
This is an orchestration api for processing sample for:

1. picking up notification messages from Azure Service Bus
2. transforming incoming messages and invoking Api Calls

# Build and Test
mvn clean package

#Deployment
Deployable Azure Function can be found in target/azure-functions/z-functions
Zip everything and perform a zip deploy - https://docs.microsoft.com/en-us/azure/azure-functions/deployment-zip-push

