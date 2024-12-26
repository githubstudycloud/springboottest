## Prompt:
mongodb单机和集群docker部署，以及使用springboot3.2.9链接的配置类和工具类

## Response:
Let me help you with MongoDB deployment and Spring Boot configuration.

MongoDB Docker Compose Configuration

Click to open code

MongoDB Configuration and Utility Classes

Click to open code

Spring Boot MongoDB Properties

Click to open code

To initialize the MongoDB cluster after deployment, execute these commands in the primary node:

```bash
# Connect to primary node
docker exec -it mongors1n1 mongosh

# Initialize replica set
rs.initiate({
 _id: "rs1",
 members: [
   {_id: 0, host: "mongors1n1:27017"},
   {_id: 1, host: "mongors1n2:27017"},
   {_id: 2, host: "mongors1n3:27017"}
 ]
})

# Check status
rs.status()
```

The code includes:

1.  Docker Compose files for single node and cluster deployments
2.  Spring Boot MongoDB configuration with transaction support
3.  Utility class for common MongoDB operations
4.  Application properties for both deployments

