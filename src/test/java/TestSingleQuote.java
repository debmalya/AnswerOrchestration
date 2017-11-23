
public class TestSingleQuote {
	public static void main(String[] args) {
		String value = "why is it important for people in the united states to save energy aren't we just a relatively small part of a large planet";
		int index = value.indexOf("'");
		String modifiedValue =  value;
		while (index > -1){
			modifiedValue = modifiedValue.substring(0,index)+"\\"+modifiedValue.substring(index);
			index = modifiedValue.indexOf("'");
		}
		
		System.out.println(modifiedValue); 
	}
}
