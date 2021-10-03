# Tracking issue service

## How to run the service with docker image

- Pull docker image
```shellscript
docker pull codingandshare/tracking-issue-service:lastest
```
- Config environment variables for service into file `env.list`
```shell
DB_HOST=localhost # host name mariadb server, default value is localhost
DB_PORT=3306 # db port, default value is 3306
DB_NAME=tracking # db name, default value is tracking
DB_USER=tracking # db user, default value is tracking
DB_PASS=password # db password, it's required
JWT_SECRET=tr0king # Key secret for token, default value is tr0king
```
- Run docker image
```shellscript
docker run --env-file env.list codingandshare/tracking-issue-service:lastest
```
