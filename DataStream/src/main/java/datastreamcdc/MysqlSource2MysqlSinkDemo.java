package datastreamcdc;

import java.io.File;

import datastreamcdc.model.JobConf;
import datastreamcdc.sink.AccountAlertCdc2MysqlSink;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.alibaba.fastjson.JSON;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;

public class MysqlSource2MysqlSinkDemo {
	
	public static void main(String[] args) throws Exception {
		
		// 读取配置  按照配置给source设置属性值
		Options options = new Options();
        options.addOption("confPath", true, "Job config.");
        BasicParser parser = new BasicParser();
        CommandLine cl = parser.parse(options, args);
        
        // 示例配置文件在src/main/resources/flink-conf.json
        String confPath = cl.getOptionValue("confPath");
        if (StringUtils.isEmpty(confPath)) {
        	throw new RuntimeException("参数confPath不可为空");
        }
        // 读取配置内容
		String jobConfContent = getJobContent(confPath);
		JobConf jobConf = JSON.parseObject(jobConfContent, JobConf.class);
		
		// 设置source
		MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
				.hostname(jobConf.getReader().getParameter().getConnection().getHost())
                .port(jobConf.getReader().getParameter().getConnection().getPort())
                .databaseList(jobConf.getReader().getParameter().getConnection().getSchema())
                .tableList(jobConf.getReader().getParameter().getConnection().getTable())
                .username(jobConf.getReader().getParameter().getUsername())
                .password(jobConf.getReader().getParameter().getPassword())
	            .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
	            .build();
	    
	    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
	    
	    env.enableCheckpointing(3000);
	    
	    // 设置stateBackend
	    // 1. 可设置为memory
	    env.setStateBackend(new MemoryStateBackend());
	    // 2. 可设置文件系统的后端  可以指定hdfs的  也可以指定fs的
	    // hdfs://namenode:40010/flink/config_checkpoints
	    // env.setStateBackend(new FsStateBackend("file:///data/apps/idatat/idatat_deploy/flink"));
	    
	    // 设置sink-设置来源
	    DataStreamSource<String> fromSource = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source");
	    
	    // 读取配置  传入sink  设置输出模式
	    fromSource.addSink(new AccountAlertCdc2MysqlSink(
	    		jobConf.getWriter().getParameter().getUsername(),
	    		jobConf.getWriter().getParameter().getPassword(),
	    		jobConf.getWriter().getParameter().getConnection().getJdbcUrl(),
	    		jobConf.getWriter().getParameter().getConnection().getTable(),
	    		jobConf.getWriter().getParameter().getColumn()));
	    
	    env.execute("MysqlSource2MysqlSinkDemo");
	}
  
	private static String getJobContent(String jobResource) {
		String jobContent;
		// jobResource 是本地文件绝对路径
        try {
            jobContent = FileUtils.readFileToString(new File(jobResource));
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("获取配置文件失败");
        }
        return jobContent;
	}
}
