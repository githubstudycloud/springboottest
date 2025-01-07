## Prompt:
mongodb写入慢，我需要用docker搭建一个可动态扩展的mongodb集群，第一批三个，后续动态增加，需要能支持百万并发写入，千万读取，同时内存占用要小于16gb，从技术上可以吗，请评估

## Prompt:
1万qps是多少数据，内存我暂时只打算用25%

## Prompt:
我是64g的内存，数据压缩策略和索引策略是通用的吗

## Prompt:
计算下这个集群的处理数据量，详细的

## Prompt:
单表最大多少，现在我一个单机的写入160万数据就慢的不行

## Prompt:
按这个先给出一个搭建初始集群的步骤，我不要docker-compose，就只用docker一步步来

## Prompt:
\# 运行容器时添加以下参数 --wiredTigerCacheSizeGB 4 \\ --setParameter maxTransactionLockRequestTimeoutMillis=5000 \\ --setParameter wiredTigerConcurrentReadTransactions=128 \\ --setParameter wiredTigerConcurrentWriteTransactions=128 这个你不能直接加命令或者配置文件里吗？优化的配置文件也没给，重新整理完整输出

## Prompt:
你这个密码多少，我原来连接的单机，现在怎么改，用的springboot3.2.9配置文件和配置类

