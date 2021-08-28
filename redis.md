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











