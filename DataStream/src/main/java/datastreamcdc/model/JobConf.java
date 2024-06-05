package datastreamcdc.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobConf {
	
	private JobReaderConf reader;
	private JobWriterConf writer;
}