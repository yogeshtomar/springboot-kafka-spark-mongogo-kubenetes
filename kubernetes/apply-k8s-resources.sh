#!/bin/bash

# Define the manifest files
manifests=(
    "zookeeper.yml"
    "kafka.yml"
    "mongo.yml"
    "spark-master.yml"
    "spark-worker.yml"
    "springboot-app.yml"
)

# Loop through each manifest and apply it
for manifest in "${manifests[@]}"; do
    echo "Applying $manifest..."
    kubectl apply -f "$manifest"
    if [ $? -ne 0 ]; then
        echo "Error applying $manifest. Exiting."
        exit 1
    fi
done

echo "All manifests applied successfully."
kubectl port-forward -n app-namespace service/springboot-app 8080:8080