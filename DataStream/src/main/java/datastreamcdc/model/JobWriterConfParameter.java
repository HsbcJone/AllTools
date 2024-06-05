package datastreamcdc.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobWriterConfParameter {
	private String password;
	private String username;
	private JobConConnection connection;
	private List<String> column;
}
