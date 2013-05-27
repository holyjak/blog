import org.junit.Assert;
import org.junit.Test;

public class EvilEnumHolderTest {
	
	@org.junit.Test
	public void try_to_access_nested_enum() throws Exception {
		Assert.assertEquals("HIGH", EvilEnumHolder.EvilnessGrade.HIGH.name());
	}
}
