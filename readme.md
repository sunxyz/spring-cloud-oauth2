## 授权认证


### 对外暴露两个端口 

8080 的网关接口 （作为资源服务器）

8090 的Oauth2接口 （作为授权服务器）

### 分成两部分

**资源服务器**

* registry 8761 注册服务

* api-gateway 8080 网关服务

* account-service 9000 账户服务

* admin-server 9000 admin服务

**授权服务器**

* oauth2-server 8090 认证服务


## postMan 测试

token 共享基于 JdbcTokenStore 此处可以换为 RedisTokenStore 细节可以参考  [Spring Security TokenStore实现3+1详解](https://blog.csdn.net/DuShiWoDeCuo/article/details/78929333)

**初始化表结构**

```
Drop table  if exists oauth_access_token;
create table oauth_access_token (
  create_time timestamp default now(),
  token_id VARCHAR(255),
  token BLOB,
  authentication_id VARCHAR(255),
  user_name VARCHAR(255),
  client_id VARCHAR(255),
  authentication BLOB,
  refresh_token VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

Drop table  if exists oauth_refresh_token;
create table oauth_refresh_token (
  create_time timestamp default now(),
  token_id VARCHAR(255),
  token BLOB,
  authentication BLOB
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
### setup1 登录授权 获取 token 

为了方便测试 此次采用密码模式 4中模式的详解可以参考 [理解OAuth 2.0](http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html)

此处的 Authorization 为应用的client_id的值与secret的值的加密

```
     POST http://localhost:8090/oauth/token HTTP/1.1
     Authorization: Basic U2FtcGxlQ2xpZW50SWQ6c2VjcmV0
     Content-Type: application/x-www-form-urlencoded

     grant_type=password&username=admin&password=admin
     
```

响应信息

```
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache
     
{
    "access_token": "63ac3e98-3a82-4837-b399-d4dbb7e1be38",
    "token_type": "bearer",
    "refresh_token": "4e699657-9fd9-4b83-881c-7e9942402353",
    "expires_in": 43011,
    "scope": "user_info"
}
```
### setup2 请求资源 携带认证信息

```
GET http://localhost:8080/api/account HTTP/1.1
Authorization: bearer 63ac3e98-3a82-4837-b399-d4dbb7e1be38
```

响应信息如下

```
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

hello account service
```

### setup3 注销 

access_token 值为 上一步获取到的 access_token 值
Authorization 值为 应用的client_id与secret的加密

```
DELETE http://localhost:8090/oauth/token?access_token=63ac3e98-3a82-4837-b399-d4dbb7e1be38 HTTP/1.1
Authorization: Basic U2FtcGxlQ2xpZW50SWQ6c2VjcmV0
```

响应

```
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

注销成功
```