## 系统介绍

本系统是前后端分离的高并发限时选课系统，前端使用Vue开发，后端使用SpringBoot开发。本选课系统实现了基本的登录、查看选课列表、选课、退选、管理课程（管理员可对课程信息以及可选容量进行设置）、设置用户限选课程门数等功能。除此之外，该项目还针对选课的高并发场景实现了缓存、降级和限流。

## 开发技术

前端技术：Vue + Axios + Element UI

后端技术：SpringBoot + MyBatis + MySQL

中间件技术：Redis + RabbitMQ + Guava

## 开发工具

前端：VS Code

后端：IDEA

## 实现的功能点

### 1. 页面权限控制

前端Vue在访问路由时先判断用户角色权限，如果有权限则允许访问，没有权限则拒绝访问。只有角色为admin的管理员用户可以访问管理课程页面和管理用户页面进行相关操作。

### 2. 支持高并发的选课和退选

后端通过判断是否可以选课/退选 + Redis预处理 + RabbitMQ异步下单，避免大量选课/退选请求短时间内直接落到数据库。

选课情况的具体实现为：

1. 对于用户的选课请求，首先判断当前是否处于选课时间，再依次判断课程是否被选完以及是否达到选课限额，一旦不符合选课条件就立即返回提示信息，不再进行后续操作。
2. 选课过程中，课程库存减一的操作在Redis中完成，不直接访问数据库。
3. 使用RabbitMQ异步下单，保护数据库不受高流量冲击。

退选情况类似。

### 3. 解决超卖

当某门课程的容量为1时，两个用户并发选择该门课程，一个用户选课后使该课程容量被修改为0，另一个用户在不知道的情况下选课，导致该课程容量被修改为-1，即出现超卖现象。为解决此问题，本系统实现了：

1. 对课程容量更新时，先进行判断，只有当课程容量大于0才能做更新操作
2. 实现乐观锁，给课程信息表设计一个version字段，为每一条数据加上版本。每次更新的时候version+1，并且更新时候带上版本号，当提交前版本号等于更新前版本号，说明此时没有被其他线程影响到，正常更新，如果冲突了则不会进行提交更新。当课程容量足够的情况下发生乐观锁冲突就进行一定次数的重试。

### 4. 使用RateLimiter限流

当用户在某一时段开始选课时，系统可能因访问量过大而崩溃。针对这种情况，本系统使用RateLimiter来实现限流，RateLimiter是guava提供的基于令牌桶算法的限流实现类，通过调整生成token的速率来限制用户频繁访问秒杀页面，从而达到防止超大流量冲垮系统。（令牌桶算法的原理是系统会以一个恒定的速度往桶里放入令牌，而如果请求需要被处理，则需要先从桶里获取一个令牌，当桶里没有令牌可取时，则拒绝服务）

### 5. 用户密码MD5加密

在将用户密码存入数据库之前，将密码和固定Salt通过MD5加密，存入加密后的密码，以防数据库被盗导致用户密码泄露。

## 项目页面截图

注意：展示的项目截图是具有admin角色的用户能看到的页面，具有user角色的用户不能看到管理课程页面和管理用户页面。

![image-20211008180850869](C:\Users\jingliwang\AppData\Roaming\Typora\typora-user-images\image-20211008180850869.png)

![image-20211008180935447](C:\Users\jingliwang\AppData\Roaming\Typora\typora-user-images\image-20211008180935447.png)

![image-20211008181014902](C:\Users\jingliwang\AppData\Roaming\Typora\typora-user-images\image-20211008181014902.png)

![image-20211008181047325](C:\Users\jingliwang\AppData\Roaming\Typora\typora-user-images\image-20211008181047325.png)

## 使用说明

前端：克隆前端项目并启动前端

1. npm install
2. npm run serve

后端：创建MySQL数据库（数据库转储文件在项目目录的sql文件夹下），下载安装Redis和RabbitMQ。克隆后端项目，使用IDEA打开项目，运行后端。

## 参考项目

1.  [zaiyunduan123/springboot-seckill: 基于SpringBoot + MySQL + Redis + RabbitMQ + Guava开发的高并发商品限时秒杀系统 (github.com)](https://github.com/zaiyunduan123/springboot-seckill) 是一个前后端不分离的商品秒杀项目，给了我做这个选课系统很大的启发，推荐学习！

2. https://gitee.com/naughtycat/vue_sport 是一个个人运动平台项目的前端部分，新手友好，适合初学者。该项目作者在B站有详细的讲解视频，在此贴上地址：[SpringBoot+Vue 项目_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1q5411s7wH?spm_id_from=333.999.0.0)
