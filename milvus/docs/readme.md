# 总体思路
```aidl
环境准备: 创建python环境 下载Hugging face模型 安装相关依赖
生成嵌入向量: 执行代码将文本转化为向量化数据 InserData文件夹embeddings是通过调用Hugging face的bert精简模型产生的向量
录入向量数据库: 将数据写入向量数据库
查询向量数据库: 查询数据 通过向量相似度比对进行语言搜索
```

## 环境准备
```aidl
# 创建虚拟环境 [目前需要python 3.10版本]
python3.10 -m venv ~/venvs/my_env  # 创建新的虚拟环境
source ~/venvs/my_env/bin/activate  # 激活新的虚拟环境

# 安装相关库 PyTorch  transformers等
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu -i https://pypi.tuna.tsinghua.edu.cn/simple

pip install transformers

主要是安装gene_embb.py 需要的依赖

--------另外一个大头就是milvus的docker环境，尝试过多个不同版本，比如2.3.0等都有问题 最终选择2.0.2的版本
[依赖minio + etcd]
采用 docker-compose down 清除历史容器
采用  docker-compose up -d 启动容器

docker-compose.yml文件在本地/opt/milvus 路径下。我也保存一份在当前代码中

```

## 下载hugging face bert精简模型
```aidl
指定本地模型路径，这里Huggingface模型基于bert模型精简版本
https://huggingface.co/distilbert/distilbert-base-uncased
model_name = "/Users/mengxiaopeng/models/distilbert-base-uncased"
```

## 生产嵌入向量
```aidl
执行 python  /Users/mengxiaopeng/Desktop/code/AllTools/milvus/data/InsertData/gene_embb.py
会生成嵌入向量文件768个纬度和书名文件，其中源书名是在gene_embb.py中定义的
```

## 录入向量数据库
```aidl
SeaTunnelHuggingFaceMilvusDemo 采用这个Demo 会创建my_book 表结构3个字段，录入4条数据，其中book_intro字段是存储的书名的768纬度的向量
```

## 查询向量数据库
```aidl
milvus支持query 和 search 两种查询方式，query是固定查询，search是向量化查询，可以支持语义搜索场景。

采用/Users/mengxiaopeng/Desktop/code/AllTools/milvus/data/queryData/gene_embb.py
生成类型语义的多纬向量就可以进行查询了。详见SeaTunnelHuggingFaceMilvusDemo
```


## 注意事项
```aidl
在使用的过程中，电脑不要使用代理，否则会出现写入和查询milvus报错，报代理相关的错误
另外代码编写过程中多参考官方文档
https://milvus.io/docs/v2.0.x/query.md
```


## 这个可以作为明年部门分享



