# api-rate-limiter
Rate limiter, which gives possibility to rate limit request based on multiple criteria

# Description

 Rate limiter uses Redis cache to store attempts and perform rate limiting.
 Approach from the official documentation is used: 
 
 `https://redis.com/redis-best-practices/basic-rate-limiting/#:~:text=Building%20a%20rate%20limiter%20with,in%20a%20given%20time%20period.`
 
Rate limit key is IP address of the request.

 # How to run and test
 
 ## Prerequisites
 
 1. Docker installed
 
 ## Launch and test
 
 1. Run docker image with redis
 `docker-compose up -d redis`
 
 2. Run RateLimiter application
 `docker-compose build ratelimiter`
 `docker-compose up -d ratelimiter`
 
 or simply use command 
 `make docker-build-and-run`
. It is launched by default with port 8097
 
 3. Run several requests (default configuration 5 requests per minute)

`curl -l http://localhost:8097/api/v1/customers/1`
 
 4. If you execute more than 5 requests in a minute the next exception will be displayed
 ```
{
  "timestamp": "2022-05-22T17:34:45.901192300Z",
  "path": "/api/v1/customers/1",
  "status": 429,
  "error": "LimitExceededException",
  "message": "Limit for clientId = 'clientId' is greater than allowed (5)"
}
```