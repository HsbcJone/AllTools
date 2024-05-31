# kadmin.local -q "list_principals"
Authenticating as principal root/admin@EXAMPLE.COM with password.
K/M@EXAMPLE.COM
kadmin/admin@EXAMPLE.COM
kadmin/changepw@EXAMPLE.COM
krbtgt/EXAMPLE.COM@EXAMPLE.COM


docker exec -it kerberos bash

# 创建 /etc/kafka 目录（如果尚不存在）
mkdir -p /etc/kafka

# 添加 Kafka 服务主体
kadmin.local -q "addprinc -randkey kafka/your-hostname@EXAMPLE.COM"

# 将密钥添加到 keytab 文件
kadmin.local -q "xst -k /etc/kafka/kafka_server.keytab kafka/your-hostname@EXAMPLE.COM"

# 验证 keytab 文件是否创建成功
ls -l /etc/kafka/kafka_server.keytab


docker cp kerberos:/etc/kafka/kafka_server.keytab /Users/mengxiaopeng/Desktop/code/cheetah/cheetah-tests/src/main/resources/kafka_server.keytab