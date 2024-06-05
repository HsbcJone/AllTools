package datastreamcdc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobConConnection {
	private String host;
	private Integer port;
	private String schema;
	private String table;
	private String jdbcUrl;
}
