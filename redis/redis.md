# Redis
 
## 1 概述

+ what

ReDiS = Remote Dictionary Server

kv nosql数据库 结构化数据库

由 C 语言编写，多种语言接口


+ 作用

1. 内存存储、持久化，内存中是断电即失
2. 效率高，可用于高速缓存
3. 发布订阅系统
4. 地图信息分析
5. 计时器、计数器
6. ... ...


+ 特性

1. 多样的数据类型
2. 持久化
3. 集群
4. 事务
5. ... ...


+ 安装 redis

最新版 redis 6.2.5 {22:14:06 星期六 2021年8月28日}

```bash
wget https://download.redis.io/releases/redis-6.2.5.tar.gz
tar zxvf 6.2.5.tar.gz
sudo apt-get install gcc # 基本的 cpp 环境得安装
make
make install
```

设置允许后台启动
```bash
# By default Redis does not run as a daemon. Use 'yes' if you need it.
# Note that Redis will write a pid file in /var/run/redis.pid when daemonized.
# When Redis is supervised by upstart or systemd, this parameter has no impact.
daemonize yes
```

用该配置后台启动
```bash
jia@jia-virtual-machine:~$ redis-server config/redis.conf 
```

打开客户端
```bash
jia@jia-virtual-machine:~$ redis-cli -p 6379
```

查看 redis 服务
```bash
jia@jia-virtual-machine:~$ ps -ef|grep redis
jia        17873     891  0 22:23 ?        00:00:00 redis-server 127.0.0.1:6379
jia        17950    5908  0 22:31 pts/0    00:00:00 grep --color=auto redis
```

关闭
```bash
jia@jia-virtual-machine:~$ redis-cli -p 6379
127.0.0.1:6379> SHUTDOWN 
not connected> exit
jia@jia-virtual-machine:~$ ps -ef|grep redis
jia        17962    5908  0 22:32 pts/0    00:00:00 grep --color=auto redis
```

+ redis-benchmark

压力测试工具
```bash
jia@jia-virtual-machine:~$ redis-benchmark -h localhost -p 6379 -c 100 -n 100000
```

测试结果
```bash
# 前边各种命令一顿测试，最后给出总结

Summary:
  throughput summary: 55710.31 requests per second
  latency summary (msec):
          avg       min       p50       p95       p99       max
        1.061     0.216     1.039     1.695     1.935     4.479
```

+ 基础知识

redis 默认有 16 个数据库，默认使用第 1 个

+ 一些操作

```bash
127.0.0.1:6379> select 3 # 选择数据库
OK
127.0.0.1:6379[3]> select 0 # 选择了 3 号数据库
OK
127.0.0.1:6379> set 1 2
OK
127.0.0.1:6379> get 1
"2"
127.0.0.1:6379> dbsize 
(integer) 1
127.0.0.1:6379> keys *
1) "1"
127.0.0.1:6379> flushdb # 清空当前数据库
OK
127.0.0.1:6379> dbsize
(integer) 0
127.0.0.1:6379[3]> flushall # 清空全部数据库
OK
```

+ redis 是单线程的

redis 是很快的，基于内存操作的，cpu 不是性能瓶颈，redis 的瓶颈是机器内存和网络带宽

## 2 数据类型

### 2.1 Redis-Key
```bash
127.0.0.1:6379> set name weiyupeng
OK
127.0.0.1:6379> get name
"weiyupeng"
127.0.0.1:6379> exists name
(integer) 1
127.0.0.1:6379> exists name name2 # 实际上的返回存在的个数
(integer) 1
127.0.0.1:6379> set name2 123
OK
127.0.0.1:6379> expire name2 10 # 存活时间 单位是秒
(integer) 1
127.0.0.1:6379> ttl name2 # 查看 key 的剩余时间
(integer) 6
127.0.0.1:6379> ttl name2
(integer) 4
127.0.0.1:6379> ttl name2
(integer) 0
127.0.0.1:6379> exists name2
(integer) 0
127.0.0.1:6379> get name2
(nil)
127.0.0.1:6379> type name
string
```

### 2.2 String

```bash
127.0.0.1:6379> append name ,hello
(integer) 15 # 返回字符串长度
127.0.0.1:6379> append name2 zhangsan # 对于不存在的 key，append 相当于 set
(integer) 8
127.0.0.1:6379> del name2
(integer) 1
127.0.0.1:6379> get name
"weiyupeng,hello"

127.0.0.1:6379> substr name 2 5 # 取子串 两边都是闭区间
"iyup"
127.0.0.1:6379> substr name 5 -5 # 负数表示从后往前数
"peng,h"
127.0.0.1:6379> substr name -1 2
""
127.0.0.1:6379> substr name 6 18
"eng,hello"
127.0.0.1:6379> substr name 18 25
""

127.0.0.1:6379> set age 18
OK
127.0.0.1:6379> incr age # ++
(integer) 19
127.0.0.1:6379> decr age # --
(integer) 18
127.0.0.1:6379> incrby age 10
(integer) 28
127.0.0.1:6379> decrby age 10
(integer) 18
127.0.0.1:6379> decrby age -10
(integer) 28
127.0.0.1:6379> incrby age -10
(integer) 18

127.0.0.1:6379> set key weiyupeng
OK
127.0.0.1:6379> getrange key 0 3
"weiy"
127.0.0.1:6379> getrange key 0 -1
"weiyupeng"
127.0.0.1:6379> setrange key 0 W
(integer) 9
127.0.0.1:6379> get key
"Weiyupeng"
127.0.0.1:6379> setrange key 0 WEI # 从 index 0 开始替换
(integer) 9
127.0.0.1:6379> get key
"WEIyupeng"

# setex (set with expire) # 设置过期时间
# setnx (set if not exists) # 不存在才设置
127.0.0.1:6379> setex key2 20 ming
OK
127.0.0.1:6379> setnx key3 redis
(integer) 1
127.0.0.1:6379> setnx key3 redis
(integer) 0

127.0.0.1:6379> mset k1 v1 k2 v2 k3 v3
OK
127.0.0.1:6379> mget k1 k2 k3
1) "v1"
2) "v2"
3) "v3"
127.0.0.1:6379> msetnx k1 v1 k4 v4 k5 v5
(integer) 0 # 说明是原子操作 k1 插不进去后边也插不进去

# 存对象
127.0.0.1:6379> set user:1 {name:weiyupeng,age:25}
OK
127.0.0.1:6379> get user:1
"{name:weiyupeng,age:25}"
127.0.0.1:6379> mset user:1:name weiyupeng user:1:age 25
OK
127.0.0.1:6379> mget user:1:name user:1:age
1) "weiyupeng"
2) "25"

127.0.0.1:6379> getset db redis
(nil)
127.0.0.1:6379> getset db memcache # 先 get 再 set
"redis"
127.0.0.1:6379> getset db redis
"memcache"
```

### 2.3 List

列表，可以当成 stack 和 queue 使用

```bash
127.0.0.1:6379> lpush list 1
(integer) 1
127.0.0.1:6379> lpush list 2 3
(integer) 3
127.0.0.1:6379> get list
(error) WRONGTYPE Operation against a key holding the wrong kind of value
127.0.0.1:6379> lrange list 0 -1 # 结果是反的 因为 lpsuh = left push
1) "3"
2) "2"
3) "1"
127.0.0.1:6379> rpush list 1 2 3
(integer) 3
127.0.0.1:6379> lrange list 0 -1
1) "1"
2) "2"
3) "3"
127.0.0.1:6379> lrange list 5 -8
(empty array)

127.0.0.1:6379> lpop list 1
1) "1"
127.0.0.1:6379> rpop list 1
1) "3"
127.0.0.1:6379> lrange list 0 -1
1) "2"
127.0.0.1:6379> lpop list # 默认弹出一个元素
"2"

127.0.0.1:6379> rpush list 1 2 3
(integer) 3
127.0.0.1:6379> lindex 0
(error) ERR wrong number of arguments for 'lindex' command
127.0.0.1:6379> lindex list 0
"1"
127.0.0.1:6379> lindex list -1
"3"
127.0.0.1:6379> lindex list -5
(nil)
127.0.0.1:6379> lindex list 5
(nil)

127.0.0.1:6379> llen list
(integer) 3

127.0.0.1:6379> rpush list 3 3
(integer) 5
127.0.0.1:6379> lrange list 0 -1
1) "1"
2) "2"
3) "3"
4) "3"
5) "3"
127.0.0.1:6379> lrem list 1 1
(integer) 1
127.0.0.1:6379> lrange list 0 -1
1) "2"
2) "3"
3) "3"
4) "3"
127.0.0.1:6379> lrem list 2 3
(integer) 2
127.0.0.1:6379> lrange list 0 -1
1) "2"
2) "3"

127.0.0.1:6379> rpush list 1 2 1 2 1 2 1 2 1 2 1
(integer) 11
127.0.0.1:6379> lrem list 3 2
(integer) 3
127.0.0.1:6379> lrange list 0 -1 # 说明 lrem 是从左边开始删除
1) "1"
2) "1"
3) "1"
4) "1"
5) "2"
6) "1"
7) "2"
8) "1"

127.0.0.1:6379> rpush hello hello1 hello2 hello3 hello4
(integer) 4
127.0.0.1:6379> ltrim hello 1 2 # 切片
OK
127.0.0.1:6379> lrange hello 0 -1
1) "hello2"
2) "hello3"

127.0.0.1:6379> rpush list 1 2 3 4 5
(integer) 5
127.0.0.1:6379> rpoplpush list newlist # 第一个表 rpop 第二个表 lpush
"5"
127.0.0.1:6379> rpoplpush list newlist
"4"
127.0.0.1:6379> lrange list 0 -1
1) "1"
2) "2"
3) "3"
127.0.0.1:6379> lrange newlist 0 -1
1) "4"
2) "5"
127.0.0.1:6379> 

127.0.0.1:6379> lset list 0 11
OK
127.0.0.1:6379> lrange list 0 -1
1) "11"
2) "2"
3) "3"
127.0.0.1:6379> lset list -1 33
OK
127.0.0.1:6379> lset list 3 44
(error) ERR index out of range
127.0.0.1:6379> lrange list 0 -1
1) "11"
2) "2"
3) "33"
127.0.0.1:6379> lset list2 0 1
(error) ERR no such key

127.0.0.1:6379> rpush list 1 2 3
(integer) 3
127.0.0.1:6379> linsert list after 2 5 # 插入
(integer) 4
127.0.0.1:6379> linsert list before 1 0
(integer) 5
127.0.0.1:6379> linsert list before 8 0 # 插入失败返回 -1
(integer) -1
127.0.0.1:6379> lrange list 0 -1
1) "0"
2) "1"
3) "2"
4) "5"
5) "3"

```

本质上是一个链表，在两边操作效率最高

### 2.4 Set

集合，无序不重复

```bash
127.0.0.1:6379> sadd set 1 1 2 3
(integer) 3
127.0.0.1:6379> sadd set 1
(integer) 0
127.0.0.1:6379> SMEMBERS
(error) ERR wrong number of arguments for 'smembers' command
127.0.0.1:6379> SMEMBERS set
1) "1"
2) "2"
3) "3"
127.0.0.1:6379> SISMEMBER set 2 # contains
(integer) 1
127.0.0.1:6379> SISMEMBER set 5
(integer) 0
127.0.0.1:6379> scard set # size
(integer) 3
127.0.0.1:6379> srem set 1 # remove
(integer) 1
127.0.0.1:6379> srem set 1
(integer) 0

127.0.0.1:6379> SRANDMEMBER set 1 # 随机选出指定个数的元素
1) "3"

127.0.0.1:6379> spop set 1 # 随机删除元素
1) "2"
127.0.0.1:6379> spop set 1
1) "3"
127.0.0.1:6379> spop set 1
(empty array)

127.0.0.1:6379> SMEMBERS set
1) "1"
2) "2"
3) "3"
127.0.0.1:6379> smove set newset 1
(integer) 1
127.0.0.1:6379> SMEMBERS set
1) "2"
2) "3"
127.0.0.1:6379> SMEMBERS newset
1) "1"

127.0.0.1:6379> sadd set1 1 2 3
(integer) 3
127.0.0.1:6379> sadd set2 1 3 5
(integer) 3
127.0.0.1:6379> sdiff set1 set2 # 差集
1) "2"
127.0.0.1:6379> sdiff set2 set1
1) "5"
127.0.0.1:6379> sinter set1 set2 # 交集
1) "1"
2) "3"
127.0.0.1:6379> sunion set1 set2 # 并集
1) "1"
2) "2"
3) "3"
4) "5"
```

### 2.5 Hash

Map 集合 ~ key-map！

和 String 类型相像，就是把 kv 放到一个 hash 里了，所以多了一些集合操作

```bash
127.0.0.1:6379> hset hash f1 v1 f2 v2 f3 v3
(integer) 3
127.0.0.1:6379> hget hash f2
"v2"
127.0.0.1:6379> hmset hash f1 v11 f3 v33
OK
127.0.0.1:6379> hmget f1 f2 f3
1) (nil)
2) (nil)
127.0.0.1:6379> hmget hash f1 f2 f3
1) "v11"
2) "v2"
3) "v33"
127.0.0.1:6379> hgetall hash
1) "f1"
2) "v11"
3) "f2"
4) "v2"
5) "f3"
6) "v33"
127.0.0.1:6379> hdel hash f1 f2 # remove
(integer) 2
127.0.0.1:6379> hgetall hash
1) "f3"
2) "v33"
127.0.0.1:6379> hlen hash
(integer) 1
127.0.0.1:6379> hexists hash f3 # contains
(integer) 1
127.0.0.1:6379> hexists hash f1
(integer) 0
127.0.0.1:6379> hkeys hash # get keySet
1) "f3"
127.0.0.1:6379> hvals hash # getValues
1) "v33"

127.0.0.1:6379> hset hash f1 100
(integer) 1
127.0.0.1:6379> hincrby hash f1 10
(integer) 110
127.0.0.1:6379> hsetnx hash f1 50 # 如果不存在才 set
(integer) 0
127.0.0.1:6379> hsetnx hash f2 50
(integer) 1

127.0.0.1:6379> hset user:1 name weiyupeng age 25
(integer) 2
127.0.0.1:6379> hincrby user:1 age 1
(integer) 26
```

### 2.6 ZSet

有序集合，在 set 的基础上增加了一个 score 作为排序标准

```bash
127.0.0.1:6379> zadd zset 1 one 3 three 2 two
(integer) 3
127.0.0.1:6379> zrange zset 0 -1
1) "one"
2) "two"
3) "three"
127.0.0.1:6379> zrangebyscore zset 2 +inf # -inf +inf 表示所有值
1) "two"
2) "three"
127.0.0.1:6379> zrangebyscore zset 2 +inf withscores # 带上分数
1) "two"
2) "2"
3) "three"
4) "3"
127.0.0.1:6379> zrem zset one # remove
(integer) 1
127.0.0.1:6379> zcard zset # size
(integer) 2
127.0.0.1:6379> zcount zset -inf 2 # 范围 size
(integer) 1
```

### 2.7 Geospatial 地理位置

城市经度纬度查询 [http://www.jsons.cn/lngcode/](http://www.jsons.cn/lngcode/)

```bash
# 添加城市 先经度 后纬度
127.0.0.1:6379> geoadd china:city 116.405285 39.904989 beijing
(integer) 1
127.0.0.1:6379> geoadd china:city 121.472644 31.231706 shanghai
(integer) 1
127.0.0.1:6379> geoadd china:city 120.856394 40.755572 huludao
(integer) 1
127.0.0.1:6379> geoadd china:city 120.153576 30.287459 hangzhou
(integer) 1
127.0.0.1:6379> geoadd china:city 125.14904 42.927 xian
(integer) 1

# 获取城市的经纬度定位
127.0.0.1:6379> geopos china:city beijing huludao
1) 1) "116.40528291463851929"
   2) "39.9049884229125027"
2) 1) "120.85639268159866333"
   2) "40.7555724065153484"

# 计算距离
# m 米 (默认) km 千米 mi 英里 ft 英尺
127.0.0.1:6379> geodist china:city beijing huludao km
"389.0408"
127.0.0.1:6379> geodist china:city beijing shanghai
"1067597.9668"

# 获取附近的人 经度 纬度 半径
127.0.0.1:6379> georadius china:city 118 39 800 km
1) "beijing"
2) "huludao"
3) "xian"
127.0.0.1:6379> georadius china:city 118 39 800 km withdist # 显示距离
1) 1) "beijing"
   2) "169.9662"
2) 1) "huludao"
   2) "312.3357"
3) 1) "xian"
   2) "742.0631"
127.0.0.1:6379> georadius china:city 118 39 800 km withcoord # 显示位置
1) 1) "beijing"
   2) 1) "116.40528291463851929"
      2) "39.9049884229125027"
2) 1) "huludao"
   2) 1) "120.85639268159866333"
      2) "40.7555724065153484"
3) 1) "xian"
   2) 1) "125.14903753995895386"
      2) "42.92699958624972822"
127.0.0.1:6379> georadius china:city 118 39 1000 km 
1) "beijing"
2) "huludao"
3) "xian"
4) "hangzhou"
5) "shanghai"
127.0.0.1:6379> georadius china:city 118 39 1000 km count 2 # 取前 2
1) "beijing"
2) "huludao"
127.0.0.1:6379> georadius china:city 118 39 1000 km withdist withcoord count 1
1) 1) "beijing"
   2) "169.9662"
   3) 1) "116.40528291463851929"
      2) "39.9049884229125027"
127.0.0.1:6379> GEORADIUSBYMEMBER china:city beijing 1000 km # 中心点从位置变成元素
1) "beijing"
2) "huludao"
3) "xian"

# 返回的结果以 geohash 表示，将二维的经纬度转换为长度为 11 的字符串，字符串越接近距离越近
127.0.0.1:6379> geohash china:city beijing hangzhou
1) "wx4g0b7xrt0"
2) "wtmkq1tjjp0"

# GEO 的底层实现是 ZSet，我们可以使用 ZSet 命令操作 GEO
127.0.0.1:6379> zrange china:city 0 -1
1) "hangzhou"
2) "shanghai"
3) "beijing"
4) "huludao"
5) "xian"
127.0.0.1:6379> zrem china:city huludao
(integer) 1
127.0.0.1:6379> zrange china:city 0 -1
1) "hangzhou"
2) "shanghai"
3) "beijing"
4) "xian"
```

### 2.8 Hyperloglog

+ 基数

A{1,3,5,5,7} => 基数为不重复数的个数 4，可以接受误差

Hyperloglog 就是基数统计的算法

应用场景：一个人访问一个网站多次，但还是算作一个人

传统方式：用 set 保存用户 id，但是占用太多内存，我们只想计数，而不是保存 id

Hyperloglog：占用固定的 12kB 内存，但官网说有 0.81% 的错误率

```bash
# 一共就 3 个方法
127.0.0.1:6379> pfadd key1 1 4 5 8 6 3 2 7 9 2 1 69 3 45 6 13456 56 4153 53 1153153 153 
(integer) 1
127.0.0.1:6379> pfadd key2 1 4 6 9 8 7 5 2 6 9 5 6 6 4 45 41 412 412 4 14 1
(integer) 1
127.0.0.1:6379> pfcount key1
(integer) 17
127.0.0.1:6379> pfcount key2
(integer) 12
127.0.0.1:6379> pfmerge key3 key1 key2 # key3 = union(key1, key2)
OK
127.0.0.1:6379> pfcount key3
(integer) 20
```

### 2.9 Bitmaps

位存储，只有 0 和 1

登录 / 未登录，会员 / 非会员，状态标记，操作二进制位

```bash
# 用 bitmaps 记录 7 天 leetcode 打卡情况
127.0.0.1:6379> setbit present 0 1 
(integer) 0
127.0.0.1:6379> setbit present 1 0
(integer) 0
127.0.0.1:6379> setbit present 2 1
(integer) 0
127.0.0.1:6379> setbit present 3 1
(integer) 0
127.0.0.1:6379> setbit present 4 1
(integer) 0
127.0.0.1:6379> setbit present 5 0
(integer) 0
127.0.0.1:6379> setbit present 6 1
(integer) 0

# 查看
127.0.0.1:6379> getbit present 3
(integer) 1

# 统计打卡天数
127.0.0.1:6379> bitcount present 0 -1
(integer) 5
```

## 3 事务

一组命令一起执行，按顺序执行

redis 事务没有隔离级别的概念，因为都是单线程啊！

redis的事务：
1. 开启事务
2. 命令入队
3. 执行事务

```bash
127.0.0.1:6379> multi # 开启
OK
127.0.0.1:6379(TX)> set k1 v1
QUEUED
127.0.0.1:6379(TX)> set k2 v2
QUEUED
127.0.0.1:6379(TX)> get k1
QUEUED
127.0.0.1:6379(TX)> exec # 执行
1) OK
2) OK
3) "v1"

127.0.0.1:6379> multi
OK
127.0.0.1:6379(TX)> set k4 v4
QUEUED
127.0.0.1:6379(TX)> discard # 取消事务
OK
127.0.0.1:6379> get k4
(nil)

# 编译型异常，错了全部不执行
127.0.0.1:6379> multi
OK
127.0.0.1:6379(TX)> get k1 k2
(error) ERR wrong number of arguments for 'get' command
127.0.0.1:6379(TX)> exec # 出错了，放弃事务
(error) EXECABORT Transaction discarded because of previous errors.

# 运行时异常，错了不影响其他
127.0.0.1:6379> multi
OK
127.0.0.1:6379(TX)> set k1 v1
QUEUED
127.0.0.1:6379(TX)> incr k1
QUEUED
127.0.0.1:6379(TX)> get k1
QUEUED
127.0.0.1:6379(TX)> exec
1) OK
2) (error) ERR value is not an integer or out of range
3) "v1"
```

+ redis 实现乐观锁

正常执行完毕
```bash
127.0.0.1:6379> set money 100
OK
127.0.0.1:6379> set out 0
OK
127.0.0.1:6379> watch money # 监视 money 对象
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379(TX)> decrby money 20
QUEUED
127.0.0.1:6379(TX)> incrby out 20
QUEUED
127.0.0.1:6379(TX)> exec
1) (integer) 80
2) (integer) 20
```

多线程下：
```bash
# 客户端 1
127.0.0.1:6379> watch money # 监视 money
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379(TX)> decrby money 10
QUEUED
127.0.0.1:6379(TX)> incrby out 10
QUEUED

# 客户端 2
127.0.0.1:6379> set money 180 # 在客户端 1 exec 之前充值 100
OK

# 客户端 1
127.0.0.1:6379(TX)> exec # watch 发现 money 变化了，导致事务执行失败
(nil)
127.0.0.1:6379> unwatch # 发现失败，先解锁
OK
127.0.0.1:6379> watch money # 重新监视，获取最新的值，这里可以自旋实现
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379(TX)> decrby money 10
QUEUED
127.0.0.1:6379(TX)> incrby out 10
QUEUED
127.0.0.1:6379(TX)> exec # 执行成功
1) (integer) 170
2) (integer) 30
```

## 4 Jedis

+ 导入依赖

```xml
        <!-- https://mvnrepository.com/artifact/redis.clients/jedis -->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>3.6.3</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.78</version>
        </dependency>
```

代码：redis-01-jedis

连接时遇到 bug，解决方法：

1. 注释 bind [https://www.cnblogs.com/shihuibei/p/9256397.html](https://www.cnblogs.com/shihuibei/p/9256397.html)
2. [https://blog.csdn.net/qq_40369944/article/details/82794888](https://blog.csdn.net/qq_40369944/article/details/82794888)

Jedis 的 API 和 redis 的一模一样！




