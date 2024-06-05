package flink.test.rocksdb

import org.rocksdb.{Options, RocksDB}

object RocksDBOperater {

  def main(args: Array[String]): Unit = {
    // 指定RocksDB存储的目录
    val dbPath = "/opt/rocksdb_dir"

    // 创建RocksDB选项
    val options = new Options().setCreateIfMissing(true)

    // 打开RocksDB数据库
    val db = RocksDB.open(options, dbPath)

    try {
      // 写入数据
      val key = "myKey".getBytes("UTF-8")
      val value = "myValue".getBytes("UTF-8")
      db.put(key, value)

      // 读取数据
      val retrievedValue = db.get(key)
      val data =new String(retrievedValue)
      System.out.println(data)
      println(s"Retrieved value: ${data}")

      // 删除数据
      db.delete(key)

      // 再次尝试读取数据
      val afterDeleteValue = db.get(key)
      if (afterDeleteValue == null) {
        println("Key not found after deletion")
      }
    } finally {
      // 关闭RocksDB数据库
      db.close()
    }
  }
}
